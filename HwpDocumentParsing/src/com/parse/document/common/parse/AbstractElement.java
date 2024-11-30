package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

public abstract class AbstractElement {
	private ElementType elementType;
	private ContentsType contentsType;
	private int no = -1;
	private int startNo = -1;
	private int endNo = -1;
	private String text;
    private List<AbstractElement> children = new ArrayList<>();
    
    public AbstractElement(ContentsType contentsType) {
        this.contentsType = contentsType;
    }
    
	public AbstractElement(ElementType elementType, ContentsType contentsType) {
		this.elementType = elementType;
		this.contentsType = contentsType;
	}

	public void setStartAndEndNo(int startNo, int endNo) {
		setStartNo(startNo);
		setEndNo(endNo);
	}

	public void setStartNo(int startNo) {
		this.startNo = startNo;
	}

	public int getStartNo() {
		return startNo;
	}

	public void setEndNo(int endNo) {
		this.endNo = endNo;
	}

	public int getEndNo() {
		return endNo;
	}

	public void setTextInfo(String text, int no) {
		setText(text);
		setNo(no);
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public ElementType getElementType() {
		return elementType;
	}

	public void setElementType(ElementType elementType) {
		this.elementType = elementType;
	}

	public ContentsType getContentsType() {
		return contentsType;
	}

	public void setContentsType(ContentsType contentsType) {
		this.contentsType = contentsType;
	}

	public String getText() {
	    return text;
	}

	public String toIndentedString() {
	    StringBuilder builder = new StringBuilder();
	    buildIndentedString(builder, 0);
	    return builder.toString();
	}

	private void buildIndentedString(StringBuilder builder, int depth) {
	    // 현재 요소를 추가
	    builder.append("  ".repeat(depth)) // 들여쓰기
	           .append("[Depth ").append(depth).append("] ")
	           .append("Text: '").append(text).append("' ")
	           .append("| No: ").append(no)
	           .append(" | ContentsType: ").append(contentsType)
	           .append("\n");

	    // 자식 요소 재귀 처리
	    for (AbstractElement child : children) {
	        child.buildIndentedString(builder, depth + 1);
	    }
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
    public void addChild(AbstractElement child) {
        children.add(child);
    }

    public List<AbstractElement> getChildren() {
        return children;
    }

    public boolean isContents() {
        return contentsType == ContentsType.CONTENTS;
    }

    public boolean isTable() {
        return contentsType == ContentsType.TABLE;
    }

    public boolean isGwanOrJO() {
        return contentsType == ContentsType.GWAN || contentsType == ContentsType.JO;
    }


}