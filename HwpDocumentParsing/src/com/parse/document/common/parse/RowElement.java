package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

public class RowElement extends ContainerElement {
    private final List<CellElement> cells = new ArrayList<>();
    private TableElement table;
    private int rowIndex; // 테이블에서의 행 인덱스

    public RowElement() {
        super(ElementType.ROW, ContentsType.ROW);
    }

    public void addCell(CellElement cell) {
        cells.add(cell);
        addElement(cell);
        cell.setRow(this);
    }

    public CellElement[] getCells() {
        return cells.toArray(new CellElement[0]);
    }

    public TableElement getTable() {
        return table;
    }

    public void setTable(TableElement table) {
        this.table = table;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        for (AbstractElement element : getElements()) {
            sb.append(element.getText());
        }
        return sb.toString();
    }
}
