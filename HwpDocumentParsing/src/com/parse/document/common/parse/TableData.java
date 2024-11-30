package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.dogfoot.hwplib.object.bodytext.control.Control;

public class TableData {
    private String[][] data;
    private String[][] mergeInfo;
    private String title;
    private int sequence;
    private int rowCount;
    private int columnCount;
    
    // 셀 내에 있는 Control 객체들을 저장할 리스트
    private List<Control> controls = new ArrayList<>();
    
    public TableData(int maxRows, int maxColumns, int sequence, String title) {
    	this.title = title;
    	this.sequence = sequence;
        this.data = new String[maxRows][maxColumns];
        this.mergeInfo = new String[maxRows][maxColumns];
        this.rowCount = maxRows;
        this.columnCount = maxColumns;
    }
    
    
    public void setData(int row, int col, String value) {
        this.data[row][col] = value;
    }

    public String[][] getData() {
        return data;
    }
    
    public String getData(int col, int row) {
        return this.data[col][row];
    }
    
    public int[] getLength() {
    	int[] length = {rowCount,columnCount};
    	return length;
    }
    
    public void setMergeInfo(int row, int col, String value) {
        this.mergeInfo[row][col] = value;
    }

    public void printData() {
		System.out.println("=======================================================");
    	System.out.println("Title >>>>>>>>"+title);
        System.out.println("Data:");
        for (String[] row : data) {
            System.out.println(""+Arrays.toString(row));
        }
    }

    public void printMergeInfo() {
        System.out.println("Merge Info:");
        for (String[] row : mergeInfo) {
            System.out.println(Arrays.toString(row));
        }
    }

    public void addTableDatas(List<TableData> tableDataList, TableData tableData) {
        if (!tableDataList.contains(tableData)) {
            tableDataList.add(tableData); // tableDataList에 현재 테이블 데이터를 추가
        }
    }
    
    // seq를 설정하는 메서드
    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    // seq를 반환하는 메서드
    public int getSequence() {
        return sequence;
    }
    
    // 타이틀을 설정하는 메서드
    public void setTitle(String title) {
        this.title = title;
    }

    // 타이틀을 반환하는 메서드
    public String getTitle() {
        return title;
    }
    
    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public String getMergeInfo(int row, int col) {
        return mergeInfo[row][col];
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

	public void setRowCount(int maxRows) {
		this.rowCount = maxRows;
	}
	
	public void setColCount(int maxCols) {
		this.columnCount = maxCols;
	}
}
