package com.parse.document.common.parse;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;

public class ParagraphElement extends AbstractElement {
	public ParagraphElement(ElementType elementType) {
		super(elementType, ContentsType.CONTENTS);
	}
}
