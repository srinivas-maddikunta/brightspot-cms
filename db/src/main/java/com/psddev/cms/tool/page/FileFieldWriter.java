package com.psddev.cms.tool.page;

import com.psddev.cms.tool.ToolPageContext;
import com.psddev.dari.db.State;
import com.psddev.dari.util.BrightcoveStorageItem;
import com.psddev.dari.util.StorageItem;

import javax.servlet.ServletException;
import java.io.IOException;

public interface FileFieldWriter {

    public void writePreview(ToolPageContext page) throws IOException, ServletException;
    public void setMetadata(ToolPageContext page, State state, StorageItem fieldValue) throws IOException, ServletException;

    public static class Static {

        public static FileFieldWriter getFileFieldWriter(StorageItem storageItem) {

            if (storageItem == null) {
                return null;
            }

            FileFieldWriter fileFieldWriter = null;
            String contentType = storageItem.getContentType();

            if (contentType.startsWith("image/")) {
                fileFieldWriter = new ImageFilePreview();
            } else if (storageItem instanceof BrightcoveStorageItem) {
                fileFieldWriter = new BrightcoveFilePreview();
            } else if (contentType.startsWith("video/")) {
                fileFieldWriter = new VideoFilePreview();
            }

            return fileFieldWriter;
        }
    }
}
