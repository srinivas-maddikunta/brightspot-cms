package com.psddev.cms.tool.view;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.psddev.cms.tool.ToolPageContext;
import com.psddev.cms.view.RawView;
import com.psddev.cms.view.ViewModel;

/**
 * Abstract {@link ViewModel} to help render Views for {@link com.psddev.cms.tool.Tool} pages.
 */
public abstract class ToolPageViewModel<M> extends ViewModel<M> {

    @CurrentToolPageContext
    protected ToolPageContext page;

    /**
     * Helper method for getting HTML produced by @{link ToolPageContext}#write methods.
     */
    protected RawView getRawView(ToolPageWriter writer) {
        Writer oldDelegate = page.getDelegate();
        StringWriter newDelegate = new StringWriter();
        try {
            page.setDelegate(newDelegate);
            writer.write();
        } catch (Exception e) {
            // This should never happen.
            throw new IllegalStateException(e);
        } finally {
            page.setDelegate(oldDelegate);
        }

        return RawView.of(newDelegate.toString());
    }

    protected interface ToolPageWriter {
        void write() throws IOException;
    }
}
