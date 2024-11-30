package com.parse.document.common.factory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parse.document.common.Const;
import com.parse.document.common.Util;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;
import com.parse.document.common.parse.CellElement;
import com.parse.document.common.parse.ParagraphElement;
import com.parse.document.common.parse.RowElement;
import com.parse.document.common.parse.TableElement;

/**
 * ElementFactory 클래스는 다양한 요소(Element)를 생성하는 역할을 합니다. 이 클래스는 주어진 텍스트를 분석하여 해당
 * 텍스트의 유형에 맞는 요소를 생성합니다.
 * 
 * @author YTHONG
 */
public class ElementFactorySanbang extends ElementFactory {

	/**
	 * 산출방법서 텍스트를 분석하여 요소의 유형을 설정합니다.
	 * 
	 * @param text 분석할 텍스트
	 * @param elem 분석 결과를 저장할 ParagraphElement 객체
	 */
	@Override
	protected void parseTextAndSetElementType(String text, ParagraphElement elem) {
		checkJuseokStart(text);

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);

			if (Character.isDigit(ch)) {
				handleDigitCharacter(text, elem, i); // 숫자 문자를 처리
				break;
			} else if (Pattern.matches(Const.BUPYOPATTERN, text)) {
				setType(elem, ContentsType.ATTACHED); // 부표 패턴에 해당하는 경우 ATTACHED로 설정
				break;
			} else if (isInCharSet(ch, SpecialCharSet.HANG)) {
				setType(elem, ContentsType.HANG); // 항 문자 처리
				break;
			} else if (isInCharSet(ch, SpecialCharSet.MOK)) {
				handleMokCharacter(text, elem); // 목 문자 처리
				break;
			} else if (isInCharSet(ch, SpecialCharSet.RANDOM)) {
				setType(elem, ContentsType.JO); // 랜덤 문자 처리
				break;
			} else if (text.startsWith("부표", i)) {
				setType(elem, ContentsType.ATTACHED); // 부표로 시작하는 경우 처리
				break;
			}else if(isJuseokCheck) {
				isJuseokCheck = false;
				setType(elem,ContentsType.JUSEOK);
			}
		}
	}

	@Override
	protected void handleDigitCharacter(String text, ParagraphElement elem, int index) {
		if (index + 1 >= text.length()) {
			return; // 다음 문자가 없으면 종료
		}

		char nextChar = text.charAt(index + 1);

		if (Character.isDigit(nextChar)) {
			handleNextDigitCharacter(text, elem, index); // 다음 문자가 숫자인 경우 추가 처리
		} else if (nextChar == '.' && index < 5 && !text.startsWith("-")) {
			setType(elem, ContentsType.HO); // 다음 문자가 '.'이고 인덱스가 5 미만인 경우 HO 처리
		} else if (nextChar == '관') {
			handleGwanCharacter(text, elem, index); // '관' 문자 처리
		} else if (nextChar == '조') {
			handleJoCharacter(text, elem); // '조' 문자 처리
		} else if (index < 3) {
			handleRandomNumber(text, elem, index); // 3 미만 인덱스에서 랜덤 숫자 처리
		}
	}

	@Override
	protected void setType(ParagraphElement elem, ContentsType type) {
		elem.setContentsType(type);
	}
}
