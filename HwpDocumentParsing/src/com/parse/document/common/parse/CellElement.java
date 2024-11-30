package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

import kr.dogfoot.hwplib.object.bodytext.control.Control;

public class CellElement extends ContainerElement {
    private RowElement row;
    private int rowIndex; // 테이블에서의 행 인덱스
    private int columnIndex; // 테이블에서의 열 인덱스

    // 추가된 필드
    private int rowSpan = 1; // 기본 값 1
    private int colSpan = 1; // 기본 값 1

    // 셀 내에 있는 Control 객체들을 저장할 리스트
    private List<Control> controls = new ArrayList<>();
    
    public CellElement() {
        super(ElementType.CELL, ContentsType.CELL);
    }

    public RowElement getRow() {
        return row;
    }

    public void setRow(RowElement row) {
        this.row = row;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    // 추가된 rowSpan 필드에 대한 getter 및 setter
    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    // 추가된 colSpan 필드에 대한 getter 및 setter
    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (AbstractElement element : getElements()) {
            sb.append(element.getText()); // 각 셀의 요소에서 텍스트를 가져옴
        }
        return sb.toString();
    }

    /**
     * 셀에 Control 객체를 추가하는 메서드
     *
     * @param control 추가할 Control 객체
     */
    public void addControl(Control control) {
        if (control != null) {
            controls.add(control); // Control 객체를 리스트에 추가
        }
    }

    /**
     * 셀에 추가된 모든 Control 객체를 반환하는 메서드
     *
     * @return Control 객체 리스트
     */
    public List<Control> getControls() {
        return controls;
    }

    /**
     * 셀에 Control 객체가 있는지 여부를 반환
     *
     * @return Control 객체가 있으면 true, 없으면 false
     */
    public boolean hasControls() {
        return !controls.isEmpty();
    }
}
