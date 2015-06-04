package com.psddev.cms.db.layout;

import com.psddev.dari.db.Record;

public class Column extends Record {

    private Cell cell;

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
