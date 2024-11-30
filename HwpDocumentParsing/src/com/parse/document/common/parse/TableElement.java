package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

import kr.dogfoot.hwplib.object.bodytext.control.Control;

public class TableElement extends ContainerElement {
	private final List<RowElement> rows = new ArrayList<>();
    private List<Control> controls = new ArrayList<>();
    
	public TableElement() {
		super(ElementType.TABLE, ContentsType.TABLE);
	}

	public void addRow(RowElement row) {
		rows.add(row);
		addElement(row);
		row.setTable(this);
	}

	public RowElement[] getRows() {
		return rows.toArray(new RowElement[0]);
	}

	@Override
	public String getText() {
		StringBuilder sb = new StringBuilder();

		sb.append("--Table--");
		for (AbstractElement element : getElements()) {
			sb.append(element.getText());
		}
		sb.append("\n--Table End--");
		return sb.toString();
	}
	
	public String getOnlyText() {
		StringBuilder sb = new StringBuilder();

		for (AbstractElement element : getElements()) {
			sb.append(element.getText());
		}
		return sb.toString();
	}

	public RowElement getRow(int rowIndex) {
		// 요청한 인덱스가 현재 테이블에 존재하지 않으면 새로운 행을 생성
		while (rows.size() <= rowIndex) {
			RowElement newRow = new RowElement();
			newRow.setRowIndex(rows.size()); // 새로운 행의 인덱스를 설정
			addRow(newRow); // 새로운 행을 테이블에 추가
		}
		return rows.get(rowIndex);
	}

	public void setControls(List<Control> controls) {
		if (controls != null) {
			this.controls = controls;
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
}