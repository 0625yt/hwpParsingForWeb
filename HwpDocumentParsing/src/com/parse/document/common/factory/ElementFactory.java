package com.parse.document.common.factory;

import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import com.parse.document.common.Const;
import com.parse.document.common.Util;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.parse.AbstractElement;
import com.parse.document.common.parse.ParagraphElement;

/**
 * ElementFactory 클래스는 기본적인 요소 생성 로직을 구현합니다.
 */

public class ElementFactory extends AbstractElementFactory {
	private static  boolean isScrtExcept = false;//타입이attache면서 보험금지급기준표일경우의 분기
	private boolean isTargetText = false;
	public static final String TARGET_TEXT = "보험금지급기준표";
	/**
	 * 약관 텍스트를 분석하여 요소의 유형을 설정합니다.
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
                handleDigitCharacter(text, elem, i);
                break;
            } else if (Pattern.matches(Const.PYUNPATTERN, text)) {
                setType(elem, ContentsType.PYUN);
                break;
            } else if (Pattern.matches(Const.GWANPATTERN, text)) {
                setType(elem, ContentsType.GWAN);
                break;
            } else if (Pattern.matches(Const.JOPATTERN, text)) {
                handleJoCharacter(text, elem);
                break;
            } else if (Pattern.matches(Const.BUPYOPATTERN, text)) {
                setType(elem, ContentsType.ATTACHED);
                break;
            } else if (isInCharSet(ch, SpecialCharSet.HANG)) {
                setType(elem, ContentsType.HANG);
                break;
            } else if (isInCharSet(ch, SpecialCharSet.MOK)) {
                handleMokCharacter(text, elem);
                break;
            } else if (isInCharSet(ch, SpecialCharSet.RANDOM)) {
                setType(elem, ContentsType.RANDOM_SHAPE);
                break;
            } else if (text.startsWith("부표", i)) {
                setType(elem, ContentsType.ATTACHED);
                break;
            }
        }
    }
    


    /**
     * 텍스트가 주석으로 시작하는지 확인합니다.
     * 보험
     * 
     * 2024-11-11 김명현
     * 예외)
     * 보험금지급기준표 라는 단어가있으면 isTargetText = true
     */
    @Override
    protected void checkJuseokStart(String text) {
        for (String juseok : Const.JUSEOK_MARKERS) {

            if (text.startsWith(juseok)) {
            	isJuseokCheck = true;
                break;
            }
        }
       if(Util.replaceBlank(text).contains(TARGET_TEXT)) {
    	   isTargetText = true;
       }else {
    	   isTargetText = false;
       }
        
    }

    /**
     * 요소의 유형을 설정합니다.
     * 
     * 2024-11-11 김명현
     * 예외)
     * 1.타입이attache면서 isTargetText가 True일경우의 분기를 만든다
     * 2. 분기가  타지면 모든 속성을 다 주석으로 만든다
     * 3.만약  다음행이 어태치먼트나 테이블인경우 분기 종료
     */
    @Override
    protected void setType(ParagraphElement elem, ContentsType type) {
 
    	if(isScrtExcept) {
    		
    		if(type.equals(ContentsType.ATTACHED)||type.equals(ContentsType.TABLE)) {
    			isScrtExcept = false;
    			isJuseokCheck = false;
    		}
    	}
    	
    	/**
    	 * isScrtExcept 이게 트루일경우 주석을 만날경우 다음
    	 */
    	if(isScrtExcept && isJuseokCheck) {

    		elem.setContentsType(ContentsType.JUSEOK);
    	}else {
    			
    	      if (isJuseokCheck) {
    	    	  /**
    	    	   *11월11일 김명현
      			  * 첫번째 분기처리  부표면 안타지게 수정
      			  */
    	            if (juseokType == null&&!type.equals(ContentsType.ATTACHED)) {
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
    	 * 보험금지급기준표 부표를 발견하면 트루
    	 */
		if (isTargetText && type.equals(ContentsType.ATTACHED)) {
			isScrtExcept = true;
		}
	}

    public void buildHierarchy(AbstractElement root, List<AbstractElement> elements) {
        Stack<AbstractElement> stack = new Stack<>();

        for (AbstractElement element : elements) {
            if (stack.isEmpty()) {
                // 초기 루트 설정
                stack.push(element);
                root.addChild(element);
                continue;
            }

            // "부표" 처리: 새로운 부모로 설정
            if (element.getContentsType() == ContentsType.ATTACHED) {
                stack.clear(); // 이전 계층 무효화
                stack.push(element);
                root.addChild(element);
                continue;
            }

            // 현재 요소의 뎁스 가져오기
            int currentDepth = getDepth(element);

            // 스택의 상위 요소와 비교하여 계층 결정
            while (!stack.isEmpty() && getDepth(stack.peek()) >= currentDepth) {
                stack.pop();
            }

            // 부모 요소 설정
            if (!stack.isEmpty()) {
                stack.peek().addChild(element);
            } else {
                root.addChild(element); // 루트에 추가
            }

            // 스택에 현재 요소 추가
            stack.push(element);
        }
    }

    private int getDepth(AbstractElement element) {
        // 요소 유형에 따른 깊이 반환
        switch (element.getContentsType()) {
            case PYUN: return 1;  // 편
            case GWAN: return 4;  // 관
            case JO: return 5;    // 조
            case HANG: return 6;  // 항
            case HO: return 7;    // 호
            case MOK: return 8;   // 목
            case JUSEOK: return 9;   // 목
            default: return Integer.MAX_VALUE; // 기타 요소
        }
    }

}
