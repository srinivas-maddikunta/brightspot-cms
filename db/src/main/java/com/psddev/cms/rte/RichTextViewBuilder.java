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

    private String richText;

    private Function<String, V> htmlViewFunction;

    private Function<RichTextElement, V> richTextElementViewFunction;

    private boolean renderUnhandledRichTextElements;

    private List<RichTextProcessor> processors = new ArrayList<>();

    /**
     * Creates a new builder for the given rich text.
     *
     * @param richText the rich text to be converted to a view.
     */
    public RichTextViewBuilder(String richText) {
        Preconditions.checkNotNull(richText, "richText");
        this.richText = richText;
    }

    /**
     * Creates a new builder for the given {@code ReferentialText}.
     *
     * @param referentialText the ReferentialText to be converted to view(s).
     */
    public RichTextViewBuilder(ReferentialText referentialText) {
        Preconditions.checkNotNull(referentialText, "referentialText");
        this.richText = toRichText(referentialText);
    }

    /**
     * Sets a handler for converting raw HTML into a view. The function is
     * passed a (possibly unbalanced) HTML fragment and expected to return a
     * view.
     *
     * @param htmlViewFunction the HTML view function to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> htmlViewFunction(Function<String, V> htmlViewFunction) {
        this.htmlViewFunction = htmlViewFunction;
        return this;
    }

    /**
     * Sets a handler for converting rich text elements into views. The function
     * is passed a RichTextElement and is expected to return the resulting view.
     *
     * @param richTextElementViewFunction the rich text element view function
     *        to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> richTextElementViewFunction(Function<RichTextElement, V> richTextElementViewFunction) {
        this.richTextElementViewFunction = richTextElementViewFunction;
        return this;
    }

    /**
     * Specifies whether rich text element tags should remain in the output
     * if there is no view handler for them.
     *
     * @param renderUnhandledRichTextElements true if the element should remain
     *        in the DOM if unhandled, false otherwise.
     * @return this builder.
     */
    public RichTextViewBuilder<V> renderUnhandledRichTextElements(boolean renderUnhandledRichTextElements) {
        this.renderUnhandledRichTextElements = renderUnhandledRichTextElements;
        return this;
    }

    /**
     * Adds a rich text processor to be applied to the rich text prior to the
     * transformation into a set of views.
     *
     * @param processor the rich text processor to be applied.
     * @return this builder.
     */
    public RichTextViewBuilder<V> addProcessor(RichTextProcessor processor) {
        if (processor != null) {
            this.processors.add(processor);
        }
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

        Document document = Jsoup.parseBodyFragment(richText);
        document.outputSettings().prettyPrint(false);

        for (RichTextProcessor processor : processors) {
            processor.process(document.body());
        }

        toBuilderNodes(document.body().childNodes(), tagTypes)
                .stream()
                .map(RichTextViewBuilderNode::toView)
                .filter(Objects::nonNull)
                .forEach(views::add);

        return views;
    }

    /*
     * Traverses the siblings all the way down the tree, collapsing balanced
     * blocks of HTML that do NOT contain any rich text elements into a single
     * HTML String. If a non-rich text element is found and NONE of its
     * descendants are rich text elements, then it will be collapsed into a
     * String. If ANY of its descendants DO contain a rich text element then
     * each parent of the rich text element will remain an Element object and
     * not be collapsed into a String UNLESS there is no htmlElementWrapperViewFunction
     * defined in which case the element will be converted into a potentially
     * unbalanced HTML String.
     */
    private List<RichTextViewBuilderNode<V>> toBuilderNodes(List<Node> siblings, Map<String, ObjectType> tagTypes) {

        List<RichTextViewBuilderNode<V>> builderNodes = new ArrayList<>();

        for (Node sibling : siblings) {

            if (sibling instanceof Element) {

                Element element = (Element) sibling;
                String tagName = element.tagName();

                ObjectType tagType = tagTypes.get(tagName);

                if (tagType != null && richTextElementViewFunction != null) {

                    RichTextElement rte = (RichTextElement) tagType.createObject(null);

                    rte.fromAttributes(StreamSupport.stream(element.attributes().spliterator(), false)
                            .collect(Collectors.toMap(Attribute::getKey, Attribute::getValue)));

                    rte.fromBody(element.html());

                    builderNodes.add(new RichTextViewBuilderRichTextElementNode<>(rte, richTextElementViewFunction));

                } else if (tagType == null || renderUnhandledRichTextElements) {

                    List<RichTextViewBuilderNode<V>> childRteNodes = toBuilderNodes(element.childNodes(), tagTypes);

                    String elementHtml = element.outerHtml();

                    if (element.tag().isSelfClosing()) {
                        builderNodes.add(new RichTextViewBuilderStringNode<>(elementHtml, htmlViewFunction));

                    } else {
                        int firstGtAt = elementHtml.indexOf('>');
                        int lastLtAt = elementHtml.lastIndexOf('<');

                        // deliberately do not validate the index values
                        // above since they should always be valid. If it
                        // turns out there's an edge case where they aren't,
                        // a RuntimeException will be thrown and we can
                        // re-evaluate from there.

                        builderNodes.add(new RichTextViewBuilderStringNode<>(elementHtml.substring(0, firstGtAt + 1), htmlViewFunction));
                        builderNodes.addAll(childRteNodes);
                        builderNodes.add(new RichTextViewBuilderStringNode<>(elementHtml.substring(lastLtAt), htmlViewFunction));
                    }
                }

            } else if (sibling instanceof TextNode || sibling instanceof DataNode) {

                if (sibling instanceof TextNode) {
                    builderNodes.add(new RichTextViewBuilderStringNode<>(((TextNode) sibling).text(), htmlViewFunction));

                } else {
                    builderNodes.add(new RichTextViewBuilderStringNode<>(((DataNode) sibling).getWholeData(), htmlViewFunction));
                }
            }
        }

        // collapse the nodes as much as possible
        return collapseBuilderNodes(builderNodes);
    }

    private List<RichTextViewBuilderNode<V>> collapseBuilderNodes(List<RichTextViewBuilderNode<V>> builderNodes) {

        List<RichTextViewBuilderNode<V>> collapsedRteNodes = new ArrayList<>();

        List<RichTextViewBuilderStringNode<V>> adjacentHtmlNodes = new ArrayList<>();

        for (RichTextViewBuilderNode<V> childBuilderNode : builderNodes) {

            if (childBuilderNode instanceof RichTextViewBuilderStringNode) {
                adjacentHtmlNodes.add((RichTextViewBuilderStringNode<V>) childBuilderNode);

            } else {
                collapsedRteNodes.add(new RichTextViewBuilderStringNode<>(
                        adjacentHtmlNodes.stream()
                                .map(RichTextViewBuilderStringNode::getHtml)
                                .collect(Collectors.joining()), htmlViewFunction));

                adjacentHtmlNodes.clear();

                collapsedRteNodes.add(childBuilderNode);
            }
        }

        if (!adjacentHtmlNodes.isEmpty()) {
            collapsedRteNodes.add(new RichTextViewBuilderStringNode<>(
                    adjacentHtmlNodes.stream()
                            .map(RichTextViewBuilderStringNode::getHtml)
                            .collect(Collectors.joining()), htmlViewFunction));
        }

        return collapsedRteNodes;
    }

    /**
     * Converts HTML from the Rich Text Editor into a List of views using the
     * most commonly used options. If {@code richText} is null, an empty list
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
     * RichTextViewBuilder} API and supply an {@link #htmlViewFunction(Function)
     * HTML view function} that conforms to the generic type.
     *
     * @param richText the RTE HTML format to convert to views.
     * @param richTextElementViewFunction the handler for converting rich text
     *        elements into views. The function is passed a RichTextElement and
     *        is expected to return the resulting view. Never null.
     * @param <V> the type of views to return.
     * @return the list of views.
     */
    public static <V> List<V> build(String richText, Function<RichTextElement, V> richTextElementViewFunction) {
        Preconditions.checkNotNull(richTextElementViewFunction, "richTextElementViewFunction");
        if (richText != null) {
            return new RichTextViewBuilder<V>(richText)
                    .addProcessor(new RichTextEditorialMarkupProcessor())
                    .addProcessor(new RichTextLineBreakProcessor())
                    .richTextElementViewFunction(richTextElementViewFunction)
                    .build();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Converts ReferentialText into a List of views using the most commonly
     * used options. If {@code referentialText} is null, an empty list
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
     * RichTextViewBuilder} API and supply an {@link #htmlViewFunction(Function)
     * HTML view function} that conforms to the generic type.
     *
     * @param referentialText the ReferentialText to convert to views.
     * @param richTextElementViewFunction the handler for converting rich text
     *        elements into views. The function is passed a RichTextElement and
     *        is expected to return the resulting view. Never null.
     * @param <V> the type of views to return.
     * @return the list of views.
     */
    public static <V> List<V> build(ReferentialText referentialText, Function<RichTextElement, V> richTextElementViewFunction) {
        return build(toRichText(referentialText), richTextElementViewFunction);
    }

    /*
     * Converts ReferentialText into an RTE HTML String.
     */
    private static String toRichText(ReferentialText referentialText) {

        if (referentialText == null) {
            return null;
        }

        // convert the ReferentialText into a String
        StringBuilder builder = new StringBuilder();

        for (Object item : referentialText) {

            if (item instanceof Reference) {

                Reference ref = (Reference) item;

                StringWriter refHtml = new StringWriter();
                try {
                    new HtmlWriter(refHtml) { {
                        writeStart(ReferenceRichTextElement.TAG_NAME,
                                ReferenceRichTextElement.VALUES_ATTRIBUTE, ObjectUtils.toJson(ref.getState().getSimpleValues()));
                        writeEnd();
                    } };
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }

                builder.append(refHtml);

            } else {
                builder.append(item);
            }
        }

        return builder.toString();
    }
}
