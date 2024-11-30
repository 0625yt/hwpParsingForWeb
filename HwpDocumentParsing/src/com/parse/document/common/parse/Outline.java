package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

public class Outline {

	private ContentsType contentsType;
	private int no = 0;
	private String outline = "";

	private static ArrayList<Outline> outlineList = new ArrayList<>();

	public Outline() {
	}

	public Outline(AbstractElement element) {
		this.contentsType = element.getContentsType();
		this.outline = element.getText().trim();
		this.no = element.getNo();
		outlineList.add(this);
	}

	public ContentsType getContentsType() {
		return contentsType;
	}

	public int getNo() {
		return no;
	}

	public String getOutline() {
		return outline;
	}

	public ArrayList<Outline> getOutlines() {
		return new ArrayList<>(outlineList);
	}

	public void clearOutlines() {
		outlineList.clear();
	}
}
