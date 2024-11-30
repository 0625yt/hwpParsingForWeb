package com.parse.document.common;

import java.util.regex.Pattern;

public class Const {
    // ASCII 코드 상수 정의
    public static final int ASCII_KEEP_WORD_SPACE = 30;   // 단어 간 유지 공백
    public static final int ASCII_FIXED_WIDTH_SPACE = 31;  // 고정 너비 공백
    public static final int ASCII_TAB = 9;     
	public static final String SANBANG_NAME = "산출방법서"; //$NON-NLS-1$
	public static final String SABANG_NAME = "사업방법서"; //$NON-NLS-1$
	public static final String LAYOUT_NAME = "레이아웃"; //$NON-NLS-1$
	public static final String BASE_EXPEN_NAME = "사업비"; //$NON-NLS-1$
	public static final String YACK_GUAN_NAME = "약관"; //$NON-NLS-1$
	public static final String YE_GYU_NAME = "판매예규"; //$NON-NLS-1$
	// 한줄 끝(line break)
	public static final int ASCII_LINE_BREAK = 10;
	// 문단 끝(para break)
	public static final int ASCII_PARA_BREAK = 13;
	
	public static final String PYUNPATTERN = "^제\\d{1,2}편.*"; // 편 패턴
	public static final String GWANPATTERN = "^제\\d{1,2}관.*"; // 관 패턴
	public static final String JOPATTERN = "^제\\d{1,2}(-\\d+)?(조|조의\\d{1,2})(?!\\d|항).*"; // JO 문자열을 찾는 패턴
	public static final String[] JUSEOK_MARKERS = { "주)", "(주)" }; // 주석을 나타태는 배열
	public static final String BUPYOPATTERN = "\\[부표\\d+-\\d+\\].*"; // 부표 패턴 추가
	public static final String ATTACHEDPATTERN = "\\(별첨\\s*제\\s*\\d+\\s*호\\s*\\)"; // 부표 패턴 추가
	
	public static final String[] SPECIAL_KEYWORDS = {"다만", "또한", "그리고", "하지만"};
	public static final String[] SPECIAL_YEGU_KEYWORDS = {"1.주계약","2.특약","3.제도성특약"};
	
	public static final Pattern SPECIAL_YEGU_PATTERN = Pattern.compile("^\\d+\\.\\s*(주계약|특약|제도성특약)$");
	
	public static final String BOJONG_NAME_REGEX = "\\(([^\\)]*?[가-횡][^\\)]*?)\\)";
	
	public static final int ASCII_UNUSABLE = 0;
	public static final int ASCII_TITLE_MARK = 8;
	public static final int ASCII_HYPEN = 24;
	public static final int ASCII_KEYWORD_1 = 25;
	public static final int ASCII_KEYWORD_2 = 26;
	public static final int ASCII_KEYWORD_3 = 27;
	public static final int ASCII_KEYWORD_4 = 28;
	public static final int ASCII_KEYWORD_5 = 29;
	public static final int ASCII_EXT_KEYWORD_1 = 1; // 예약
	public static final int ASCII_EXT_DEF_SECTION = 2; // 구역, 단 정의
	public static final int ASCII_EXT_FIELD_START = 3; // 필드시작
	public static final int ASCII_EXT_GSO = 11; // 그리기 개체, 표
	public static final int ASCII_EXT_KEYWORD_12 = 12; // 예약
	public static final int ASCII_EXT_KEYWORD_14 = 14; // 예약
	public static final int ASCII_EXT_HIDDEN_COMMENT = 15; // 숨은 설명
	public static final int ASCII_EXT_HEADER_FOOTER = 16; // 머리말, 꼬리말
	public static final int ASCII_EXT_FOOTNOTE = 17; // 각주, 미주
	public static final int ASCII_EXT_AUTO_NUMBERING = 18; // 자동번호
	public static final int ASCII_EXT_PAGE_CTRL = 21; // 페이지 컨트롤(감추기, 새 번호로 시작 등)
	public static final int ASCII_EXT_BOOKMARK = 22; // 책갈피, 찾아보기 표식
	public static final int ASCII_EXT_KERNING = 23; // 덧말, 글자 겹침
	
	// 전각 공백문자
	public static final int ASCII_FULL_WIDTH_SPACE = 12288;
	public static final int INLINE_CHAR_LEN = 8;
	public static final int EXT_CHAR_LEN = 8;
	public static final int PT_TO_BASESIZE = 100;
	public static final float CHAR_TO_PT = 4.5f; // 글자 to 포인트 => 정확한 수치는 아님. 대략적인 수치임.
	public static final int PAGE_FINISH = 3; // 마지막 페이지 이후 간격 

	public static final String TITLE = "title";
	public static final String BOJONG = "bojong";
	public static final String ROW_CNT = "rowCnt";
	public static final String ATTACHE_CONTENTS = "attacheContents";
	public static final String BASE_PRICE = "basePrice";
	public static final String TAG = "tag";
	public static final String JUSEOK = "juseok";
	public static final String PAY_CONDITION = "pay_condition";
	
	public static final String OROVER = "≥";
	public static final String ORUNDER = "≤";
	public static final String OVER = ">";
	public static final String UNDER = "<";
	public static final String EQAUL = "=";
	public static final String PLUS = "\\+";
	public static final String MINUS= "-";
	
	
	public static final String SecureMainTitle= "■";
	public static final String SecureTitle[] = {"가.", "나.", "다.", "라.","마."};
	public static final String SecureSubTitle[] = {"①", "②", "③", "④","⑤"};
	public static final String DEFAULT_PATH = "C:\\Temp\\document";
	public static final String JSON_UPLOAD_PATH = "C:\\Temp\\";
}
