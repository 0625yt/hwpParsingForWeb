package com.parse.document.common.parse;

import java.util.ArrayList;
import java.util.List;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

public abstract class ContainerElement extends AbstractElement {
	private List<AbstractElement> elemList = new ArrayList<>();

	public ContainerElement(ElementType elementType, ContentsType contentsType) {
		super(elementType, contentsType);
	}

	public void addElement(AbstractElement elem) {
		if (elem != null) {
			elemList.add(elem);
		}
	}

	public List<AbstractElement> getElements() {
		return new ArrayList<>(elemList);
	}

	public int getElementCount() {
		return elemList.size();
	}
}
