package com.psddev.cms.rte;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.base.Preconditions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import com.psddev.cms.db.RichTextElement;
import com.psddev.dari.db.ObjectType;
import com.psddev.dari.db.Reference;
import com.psddev.dari.db.ReferentialText;
import com.psddev.dari.util.HtmlWriter;
import com.psddev.dari.util.ObjectUtils;

/**
 * <p>A builder of views from rich text Strings for the purpose of rendering
 * content produced in the rich text editor. This class supports both
 * fields of type {@code String} annotated with
 * {@link com.psddev.cms.db.ToolUi.RichText @ToolUi.RichText} as well as fields
 * of type {@link com.psddev.dari.db.ReferentialText ReferentialText}.</p>
 *
 * <p>Example Model:</p>
 *
 * <blockquote><pre>
 * public class Article extends Content {
 * &nbsp;   &#64;ToolUi.RichText
 * &nbsp;   String body;
 * }
 * </pre></blockquote>
 *
 * <p>Typical use from within a {@link com.psddev.cms.view.ViewModel ViewModel} might look like:</p>
 *
 * <blockquote><pre>
 * List&lt;MyRichTextItemView&gt; views = RichTextViewBuilder.build(model.body, rte -> createView(MyRichTextItemView.class, rte);
 * </pre></blockquote>
 *
 * <p>For {@code ViewModel} methods expecting the return of a single object
 * instead of a list, the implementer should wrap the list of views inside of
 * another view that is setup to handle such a list.</p>
 */
public class RichTextViewBuilder<V> {

    private final String text;
    private Function<String, V> htmlToView;
    private Function<RichTextElement, V> elementToView;
    private boolean keepUnboundElements;
    private final List<RichTextPreprocessor> preprocessors = new ArrayList<>();

    /**
     * Creates a new builder for the given rich text.
     *
     * @param text the rich text to be converted to a view.
     */
    public RichTextViewBuilder(String text) {
        Preconditions.checkNotNull(text);
        this.text = text;
    }

    /**
     * Creates a new builder for the given {@code ReferentialText}.
     *
     * @param text the ReferentialText to be converted to view(s).
     */
    public RichTextViewBuilder(ReferentialText text) {
        Preconditions.checkNotNull(text);
        this.text = toRichText(text);
    }

    /**
     * Sets a handler for converting raw HTML into a view. The function is
     * passed a (possibly unbalanced) HTML fragment and expected to return a
     * view.
     *
     * @param htmlToView the HTML view function to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> htmlToView(Function<String, V> htmlToView) {
        this.htmlToView = htmlToView;
        return this;
    }

    /**
     * Sets a handler for converting rich text elements into views. The function
     * is passed a RichTextElement and is expected to return the resulting view.
     *
     * @param elementToView the rich text element view function
     *        to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> elementToView(Function<RichTextElement, V> elementToView) {
        this.elementToView = elementToView;
        return this;
    }

    /**
     * Specifies whether rich text element tags should remain in the output
     * if there is no view handler for them.
     *
     * @param keepUnboundElements true if the element should remain
     *        in the DOM if unhandled, false otherwise.
     * @return this builder.
     */
    public RichTextViewBuilder<V> keepUnboundElements(boolean keepUnboundElements) {
        this.keepUnboundElements = keepUnboundElements;
        return this;
    }

    /**
     * Adds a rich text preprocessor to be applied to the rich text prior to the
     * transformation into a set of views.
     *
     * @param preprocessor the rich text preprocessor to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> addPreprocessor(RichTextPreprocessor preprocessor) {
        Preconditions.checkNotNull(preprocessor);
        this.preprocessors.add(preprocessor);
        return this;
    }

    /**
     * Converts the rich text into 1 or many views.
     *
     * @return a list of views.
     */
    public List<V> build() {
        List<V> views = new ArrayList<>();
        Map<String, ObjectType> tagTypes = new HashMap<>(RichTextElement.getConcreteTagTypes());

        tagTypes.put(ReferenceRichTextElement.TAG_NAME, ObjectType.getInstance(ReferenceRichTextElement.class));

        Document document = Jsoup.parseBodyFragment(text);

        document.outputSettings().prettyPrint(false);

        for (RichTextPreprocessor preprocessor : preprocessors) {
            preprocessor.preprocess(document.body());
        }

        toViewNodes(document.body().childNodes(), tagTypes)
                .stream()
                .map(RichTextViewNode::toView)
                .filter(Objects::nonNull)
                .forEach(views::add);

        return views;
    }

    // Traverses the siblings all the way down the tree, collapsing balanced
    // blocks of HTML that do NOT contain any rich text elements into a single
    // HTML String. If a non-rich text element is found and NONE of its
    // descendants are rich text elements, then it will be collapsed into a
    // String. If ANY of its descendants DO contain a rich text element then
    // each parent of the rich text element will remain an Element object and
    // not be collapsed into a String UNLESS there is no htmlElementWrapperViewFunction
    // defined in which case the element will be converted into a potentially
    // unbalanced HTML String.
    private List<RichTextViewNode<V>> toViewNodes(List<Node> siblings, Map<String, ObjectType> tagTypes) {
        List<RichTextViewNode<V>> viewNodes = new ArrayList<>();

        for (Node sibling : siblings) {
            if (sibling instanceof Element) {
                Element element = (Element) sibling;
                ObjectType tagType = tagTypes.get(element.tagName());

                if (tagType != null && elementToView != null) {
                    RichTextElement rte = (RichTextElement) tagType.createObject(null);

                    rte.fromAttributes(StreamSupport
                            .stream(element.attributes().spliterator(), false)
                            .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)));

                    rte.fromBody(element.html());

                    viewNodes.add(new ElementRichTextViewNode<>(rte, elementToView));

                } else if (tagType == null || keepUnboundElements) {
                    List<RichTextViewNode<V>> childViewNodes = toViewNodes(element.childNodes(), tagTypes);
                    String html = element.outerHtml();

                    if (element.tag().isSelfClosing()) {
                        viewNodes.add(new StringRichTextViewNode<>(html, htmlToView));

                    } else {
                        int firstGtAt = html.indexOf('>');
                        int lastLtAt = html.lastIndexOf('<');

                        // deliberately do not validate the index values
                        // above since they should always be valid. If it
                        // turns out there's an edge case where they aren't,
                        // a RuntimeException will be thrown and we can
                        // re-evaluate from there.
                        viewNodes.add(new StringRichTextViewNode<>(html.substring(0, firstGtAt + 1), htmlToView));
                        viewNodes.addAll(childViewNodes);
                        viewNodes.add(new StringRichTextViewNode<>(html.substring(lastLtAt), htmlToView));
                    }
                }

            } else if (sibling instanceof TextNode || sibling instanceof DataNode) {
                if (sibling instanceof TextNode) {
                    viewNodes.add(new StringRichTextViewNode<>(((TextNode) sibling).text(), htmlToView));

                } else {
                    viewNodes.add(new StringRichTextViewNode<>(((DataNode) sibling).getWholeData(), htmlToView));
                }
            }
        }

        // collapse the nodes as much as possible
        List<RichTextViewNode<V>> collapsed = new ArrayList<>();
        List<StringRichTextViewNode<V>> adjacent = new ArrayList<>();

        for (RichTextViewNode<V> childBuilderNode : viewNodes) {
            if (childBuilderNode instanceof StringRichTextViewNode) {
                adjacent.add((StringRichTextViewNode<V>) childBuilderNode);

            } else {
                collapsed.add(new StringRichTextViewNode<>(
                        adjacent.stream()
                                .map(StringRichTextViewNode::getHtml)
                                .collect(Collectors.joining()), htmlToView));

                adjacent.clear();

                collapsed.add(childBuilderNode);
            }
        }

        if (!adjacent.isEmpty()) {
            collapsed.add(new StringRichTextViewNode<>(
                    adjacent.stream()
                            .map(StringRichTextViewNode::getHtml)
                            .collect(Collectors.joining()), htmlToView));
        }

        return collapsed;
    }

    /**
     * Converts HTML from the Rich Text Editor into a List of views using the
     * most commonly used options. If {@code text} is null, an empty list
     * is returned.
     * <p>
     * <b>NOTE:</b> This API deliberately breaks the generics contract of the
     * returned list such that it may contain instances of
     * {@link com.psddev.cms.view.RawView RawView}. Thus, explicitly iterating
     * over the returned list as the type of the generic argument may result in
     * a ClassCastException. If explicit iteration is required for your
     * application to function properly, cast the list items to type
     * {@code Object} and do instanceof checks before casting to the desired
     * type, or use the more general {@link #RichTextViewBuilder(String)
     * RichTextViewBuilder} API and supply an {@link #htmlToView(Function)
     * HTML view function} that conforms to the generic type.
     *
     * @param text the RTE HTML format to convert to views.
     * @param elementToView the handler for converting rich text
     *        elements into views. The function is passed a RichTextElement and
     *        is expected to return the resulting view. Never null.
     * @param <V> the type of views to return.
     * @return the list of views.
     */
    public static <V> List<V> build(String text, Function<RichTextElement, V> elementToView) {
        if (text != null) {
            return new RichTextViewBuilder<V>(text)
                    .addPreprocessor(new EditorialMarkupRichTextPreprocessor())
                    .addPreprocessor(new LineBreakRichTextPreprocessor())
                    .elementToView(elementToView)
                    .build();

        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Converts ReferentialText into a List of views using the most commonly
     * used options. If {@code text} is null, an empty list
     * is returned.
     * <p>
     * <b>NOTE:</b> This API deliberately breaks the generics contract of the
     * returned list such that it may contain instances of
     * {@link com.psddev.cms.view.RawView RawView}. Thus, explicitly iterating
     * over the returned list as the type of the generic argument may result in
     * a ClassCastException. If explicit iteration is required for your
     * application to function properly, cast the list items to type
     * {@code Object} and do instanceof checks before casting to the desired
     * type, or use the more general {@link #RichTextViewBuilder(String)
     * RichTextViewBuilder} API and supply an {@link #htmlToView(Function)
     * HTML view function} that conforms to the generic type.
     *
     * @param text the ReferentialText to convert to views.
     * @param elementToView the handler for converting rich text
     *        elements into views. The function is passed a RichTextElement and
     *        is expected to return the resulting view. Never null.
     * @param <V> the type of views to return.
     * @return the list of views.
     */
    public static <V> List<V> build(ReferentialText text, Function<RichTextElement, V> elementToView) {
        return build(toRichText(text), elementToView);
    }

    // Converts ReferentialText into an RTE HTML String.
    private static String toRichText(ReferentialText text) {
        if (text == null) {
            return null;
        }

        // convert the ReferentialText into a String
        StringBuilder builder = new StringBuilder();

        for (Object item : text) {
            if (item instanceof Reference) {
                Reference ref = (Reference) item;
                StringWriter refString = new StringWriter();
                HtmlWriter refHtml = new HtmlWriter(refString);

                try {
                    refHtml.writeStart(
                            ReferenceRichTextElement.TAG_NAME,
                            ReferenceRichTextElement.VALUES_ATTRIBUTE,
                            ObjectUtils.toJson(ref.getState().getSimpleValues()));

                    refHtml.writeEnd();

                } catch (IOException error) {
                    throw new IllegalStateException(error);
                }

                builder.append(refString);

            } else {
                builder.append(item);
            }
        }

        return builder.toString();
    }
}
