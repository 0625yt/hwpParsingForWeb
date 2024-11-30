package com.parse.document.common.factory;

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
 * AbstractElementFactory 클래스는 요소를 생성하는 기본 메커니즘을 제공합니다.
 */
public abstract class AbstractElementFactory {
    protected static boolean isJuseokCheck = false;
    protected static ContentsType juseokType = null;
    protected static ContentsType compareType = null;
    protected static ParagraphElement previousElement = null;

    /**
     * SpecialCharSet 열거형은 특수 문자를 정의합니다.
     */
    enum SpecialCharSet {
        MOK("가나다라마바사아자차카타파하".toCharArray()), 
        HANG("①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮".toCharArray()), 
        RANDOM("ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ".toCharArray());

        private final char[] chars;

        SpecialCharSet(char[] chars) {
            this.chars = chars;
        }

        public char[] getChars() {
            return chars;
        }
    }

    /**
     * 주어진 텍스트를 분석하여 ParagraphElement 객체를 생성합니다.
     */
    public ParagraphElement createParagraphElement(String paragraphText, int no) {
        ParagraphElement elem = new ParagraphElement(ElementType.TEXT);
        String processedText = Util.replaceBlank(paragraphText);

        try {
            parseTextAndSetElementType(processedText, elem);

            if (previousElement != null && startsWithSpecialKeyword(paragraphText)) {
                previousElement.setTextInfo(previousElement.getText() + " " + paragraphText, no);
                return null;
            } else {
                previousElement = elem;
            }

        } catch (StringIndexOutOfBoundsException e) {
            // 예외 처리
        }

        return setFinalTextInfo(elem, paragraphText, no);
    }

    /**
     * 텍스트를 분석하여 요소의 유형을 설정합니다.
     */
    protected abstract void parseTextAndSetElementType(String text, ParagraphElement elem);

    /**
     * 텍스트가 주석으로 시작하는지 확인합니다.
     */
    protected void checkJuseokStart(String text) {
        boolean foundJuseokMarker = false;

        for (String juseok : Const.JUSEOK_MARKERS) {

            if (text.startsWith(juseok)) {
                foundJuseokMarker = true;
                break;
            }
        }

        if (foundJuseokMarker) {
            isJuseokCheck = true;
            juseokType = null;
        }
    }

    /**
     * 요소의 유형을 설정합니다.
     */
    protected void setType(ParagraphElement elem, ContentsType type) {
        if (isJuseokCheck) {
            if (juseokType == null) {
                juseokType = type;
                elem.setContentsType(ContentsType.JUSEOK);
            } else if (juseokType == type) {
                elem.setContentsType(ContentsType.JUSEOK);
            } else {
                isJuseokCheck = false;
                elem.setContentsType(type);
            }
        } else {
            elem.setContentsType(type);
        }
    }

    /**
     * 주어진 문자(char)가 특정 문자 집합에 포함되는지 확인합니다.
     */
    protected boolean isInCharSet(char ch, SpecialCharSet set) {
        for (char c : set.getChars()) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * 최종 텍스트 정보를 설정합니다.
     */
    protected ParagraphElement setFinalTextInfo(ParagraphElement elem, String paragraphText, int no) {
        if (elem.getContentsType() == ContentsType.ATTACHED) {
            elem.setTextInfo(paragraphText.replace("\n", ""), no);
        } else {
            elem.setTextInfo(paragraphText, no);
        }
        return elem;
    }

    /**
     * 특정 문단을 이전 문단과 연결하는지 확인합니다.
     */
    protected boolean startsWithSpecialKeyword(String text) {
        text = text.trim();
        for (String keyword : Const.SPECIAL_KEYWORDS) {
            if (text.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }
	/**
	 * 숫자 문자를 처리하여 요소의 유형을 설정합니다.
	 * 
	 * @param text  분석할 텍스트
	 * @param elem  분석 결과를 저장할 ParagraphElement 객체
	 * @param index 현재 문자의 인덱스
	 */
    protected void handleDigitCharacter(String text, ParagraphElement elem, int index) {
		if (index + 1 >= text.length()) {
			return; // 다음 문자가 없으면 종료
		}

		char nextChar = text.charAt(index + 1);

		if (Character.isDigit(nextChar)) {
			handleNextDigitCharacter(text, elem, index); // 다음 문자가 숫자인 경우 추가 처리
		} else if (nextChar == '.' && index < 5) {
			setType(elem, ContentsType.HO); // 다음 문자가 '.'이고 인덱스가 5 미만인 경우 HO 처리
		} else if (nextChar == '관') {
			handleGwanCharacter(text, elem, index); // '관' 문자 처리
		} else if (nextChar == '조') {
			handleJoCharacter(text, elem); // '조' 문자 처리
		} else if (index < 3) {
			handleRandomNumber(text, elem, index); // 3 미만 인덱스에서 랜덤 숫자 처리
		}
	}

	/**
	 * 다음 숫자와 특정 문자가 조합된 경우를 처리합니다.
	 * 
	 * @param text  분석할 텍스트
	 * @param elem  분석 결과를 저장할 ParagraphElement 객체
	 * @param index 현재 문자의 인덱스
	 */
	protected void handleNextDigitCharacter(String text, ParagraphElement elem, int index) {
		if (index + 2 < text.length() && text.charAt(index + 2) == '조') {
			if (text.matches(Const.JOPATTERN)) {
				setType(elem, ContentsType.JO); // 조문 패턴이 일치하는 경우 JO로 설정
			} else {
				setType(elem, ContentsType.CONTENTS); // 조문 패턴이 일치하지 않는 경우 내용으로 설정
			}
		}
	}

	/**
	 * '관' 문자가 포함된 경우를 처리합니다.
	 * 
	 * @param text  분석할 텍스트
	 * @param elem  분석 결과를 저장할 ParagraphElement 객체
	 * @param index 현재 문자의 인덱스
	 */
	protected void handleGwanCharacter(String text, ParagraphElement elem, int index) {
		if (index < 3 || (index + 2 < text.length() && Character.isDigit(text.charAt(index + 2)))) {
			setType(elem, ContentsType.GWAN); // GWAN으로 설정
		}
	}

	/**
	 * '조' 문자가 포함된 경우를 처리합니다.
	 * 
	 * @param text 분석할 텍스트
	 * @param elem 분석 결과를 저장할 ParagraphElement 객체
	 */
	protected void handleJoCharacter(String text, ParagraphElement elem) {
		if (compareType == ContentsType.JO) {
			setType(elem, ContentsType.CONTENTS); // 이전 타입이 조(JO)였으면 CONTENTS로 설정
		} else if (text.matches(Const.JOPATTERN)) {
			setType(elem, ContentsType.JO); // 조문 패턴이 일치하는 경우 'JO'로 설정
		} else {
			setType(elem, ContentsType.CONTENTS); // 조문 패턴이 일치하지 않는 경우 CONTENTS로 설정
		}
	}

	/**
	 * 랜덤 숫자가 포함된 경우를 처리합니다.
	 * 
	 * @param text  분석할 텍스트
	 * @param elem  분석 결과를 저장할 ParagraphElement 객체
	 * @param index 현재 문자의 인덱스
	 */
	protected void handleRandomNumber(String text, ParagraphElement elem, int index) {
		setType(elem, ContentsType.RANDOM_NUM); // 랜덤 숫자로 설정
		if (index > 0 && text.charAt(index - 1) == '「') {
			setType(elem, ContentsType.CONTENTS); // 앞 문자가 '「'이면 내용으로 설정
		}
	}

	/**
	 * MOK 문자가 포함된 경우를 처리합니다.
	 * 
	 * @param text 분석할 텍스트
	 * @param elem 분석 결과를 저장할 ParagraphElement 객체
	 */
	protected void handleMokCharacter(String text, ParagraphElement elem) {
		if (text.length() > 1) {
			char nextChar = text.charAt(1);
			if (nextChar == '.') {
				setType(elem, ContentsType.MOK); // MOK으로 설정
			} else if (nextChar == ')') {
				setType(elem, ContentsType.RANDOM_WORD); // 임의 단어로 설정
			} else {
				setType(elem, ContentsType.CONTENTS); // 그 외는 내용으로 설정
			}
		} else {
			setType(elem, ContentsType.CONTENTS); // 길이가 1 이하인 경우 내용으로 설정
		}
	}


    /**
     * 새로운 객체를 생성합니다.
     */
    public TableElement createTableElement() {
        return new TableElement();
    }

    public RowElement createRowElement() {
        return new RowElement();
    }

    public CellElement createCellElement() {
        return new CellElement();
    }
}
