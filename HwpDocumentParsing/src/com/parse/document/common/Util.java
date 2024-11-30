
package com.parse.document.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;
import com.parse.document.common.parse.AbstractElement;
import com.parse.document.common.parse.CellElement;
import com.parse.document.common.parse.Outline;
import com.parse.document.common.parse.RowElement;
import com.parse.document.common.parse.TableElement;

public class Util {


	/**
	 * Percent를 삭제하고 100으로 나눈 값을 String 형으로 반환
	 * @param percentage
	 * @return
	 */
    public static String convertPercentageToString(String percentage) {
            // %를 제거하고 숫자 부분을 가져옴
            String cleanPercentage = percentage.replace("%", "").trim();
            
            // BigDecimal로 변환하여 정확한 계산 수행
            BigDecimal bd = new BigDecimal(cleanPercentage);
            bd = bd.divide(BigDecimal.valueOf(100)); // 100으로 나누어 소수로 변환
            bd = bd.stripTrailingZeros(); // 불필요한 소수점 이하 자리를 제거

            return bd.toPlainString(); // 지수 표기법 없이 문자열로 반환
        }
    
	/**
	 * Double 형태를 String 형으로 반환
	 * @param percentage
	 * @return
	 */
    public static String convertDoubleToString(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        
        bd = bd.setScale(10, RoundingMode.HALF_UP); 
        
        bd = bd.stripTrailingZeros();
        
        return bd.toPlainString();
    }
    
	/**
	 * 특정 키워드를 topLevel에서 찾아 인덱스를 반환
	 */
	public static int findStartIndex(List<String> topLevel, String keyword) {
		for (int i = 0; i < topLevel.size(); i++) {
			if (keyword.equals(topLevel.get(i).trim())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 특정 키워드를 midleLevel에서 찾아 인덱스를 반환
	 */
	public static int findIndex(List<String> findLevel, int startIndex, String keyword) {
		for (int i = startIndex; i < findLevel.size(); i++) {
			if (keyword.equals(findLevel.get(i).trim())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * lowLevel 데이터에서 특정 인덱스의 값을 추출하여 리스트로 반환
	 */
	public static List<String> extractValuesFromLowLevel(List<List<String>> lowLevel, int foundMidIndex) {
		List<String> values = new ArrayList<>();
		for (List<String> row : lowLevel) {
			if (foundMidIndex < row.size()) {
				String value = row.get(foundMidIndex).trim();
				values.add(value.isEmpty() ? "" : value);
			}
		}
		return values;
	}

	/**
	 * 주어진 문자열에서 두 키워드 사이에 위치한 문자를 추출합니다.
	 * 
	 * @param text         대상 문자열
	 * @param startKeyword 시작 키워드 (문자 앞부분에 위치)
	 * @param endKeyword   종료 키워드 (문자 뒷부분에 위치)
	 * @return 추출된 숫자
	 */
	public static String utilityBetweenKeywordsSearch(String text, String startKeyword, String endKeyword) {
		// 정규식 패턴: startKeyword와 endKeyword 사이에 있는 모든 문자열을 추출
		String pattern = Pattern.quote(startKeyword) + "\\s*(.*?)\\s*" + Pattern.quote(endKeyword);
		String betweenKeyword = "";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(text);
		
		// 매칭된 문자열이 있으면 반환, 없으면 예외 발생
		if (m.find()) {
			betweenKeyword = m.group(1).trim(); // 매칭된 부분 추출 후 양쪽 공백 제거
		}
		
		return betweenKeyword;
	}

	/**
	 * 문자열중 15% 이런거있으면 15추출
	 * 
	 * @param text
	 * @return %옆 숫자
	 */
	public static String utilityPercentageValueSearch(String text) {
		// 정규 표현식: 소수점이 있을 수도 있고 없을 수도 있는 숫자를 추출
		String regex = "([0-9]+(?:\\.[0-9]+)?)%"; // 숫자 부분에 소수점이 있을 수도 있고 없을 수도 있음
		String parcentValue = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		// 매칭되는 값을 찾으면 반환
		if (matcher.find()) {
			parcentValue = matcher.group(1); // 첫 번째 그룹(숫자) 추출
		}

		return parcentValue;
	}
	

	/**
	 * "제1관"을 Element에서 찾을 시 True 반환
	 * 
	 * @param ele
	 * @return
	 */
	public static boolean findFirstGwan(AbstractElement ele) {
		String text = ele.getText();

		return text.contains("제1관");
	}

	/**
	 * "제1관"을 Element에서 찾을 시 True 반환
	 * 
	 * @param ele
	 * @return
	 */
	public static boolean findFirstPyun(AbstractElement ele) {
		String text = ele.getText();

		return text.contains("제1편");
	}

	/**
	 * 검색 시 입력값 검증
	 *
	 * @param abs          AbstractElement 리스트
	 * @param keywordArray 키워드 배열
	 * @return
	 */
	private static boolean validateInputs(ArrayList<AbstractElement> abs, String[] keywordArray) {
		return abs == null || keywordArray == null || keywordArray.length == 0;
	}

	private static boolean validateInputs(String[] keywordArray) {
		return keywordArray == null || keywordArray.length == 0;
	}

	/**
	 * 문자열의 공백을 제거하여 반환
	 *
	 * @param value 입력 문자열
	 * @return 공백이 제거된 문자열
	 */
	public static String replaceBlank(String value) {
		return (value == null || value.isEmpty()) ? ""
				: value.replaceAll("\\s+", "").replace("\r\n", "").replace("\n", "");
	}

	/**
	 * 문자열의 마지막 단어를 제거하여 반환
	 * 
	 * @param str
	 * @return
	 */
	public static String removeLastChar(String str) {
		String removestr = replaceBlank(str);
		return removestr.length() > 1 ? removestr.substring(0, removestr.length() - 1) : removestr;
	}

	/**
	 * Rule01에서 만들어진 보종리스트를 입력하여 보종명 반환
	 * 
	 * @param key
	 * @param bogongList
	 * @return
	 */
	public static String getBojongName(String key, List<Map<String, String>> bogongList) {
		String returnValue = "";
		for (Map<String, String> bogongName : bogongList) {
			if (null != bogongName.get(key)) {
				returnValue = bogongName.get(key);
				break;
			}

		}
		return returnValue;
	}

	/**
	 * 분수로되어있는 문자열있으면 분자 출력
	 * 
	 * @param fraction
	 * @return
	 */
	public static String extractBunja(String fraction) {
		// "/"를 기준으로 문자열을 분할
		String[] parts = fraction.split("/");
		// 분자를 정수로 변환하여 반환
		return parts[0].trim();
	}

	/**
	 * 분수로되어있는 문자열있으면 분모 출력
	 * 
	 * @param fraction
	 * @return
	 */
	public static String extractBunmo(String fraction) {
		// "/"를 기준으로 문자열을 분할
		String[] parts = fraction.split("/");
		// 분모에서 쉼표 제거 후 정수로 변환하여 반환
		return parts[1].replace(",", "").trim();
	}

	/**
	 * 퍼센트있으면 숫자로바꿔줌
	 * 
	 * @param percentage
	 * @return
	 */
	public static BigDecimal convertPercentageToDecimal(String percentage) {
		
	     // '%'의 위치를 찾음
        	int percentIndex = percentage.indexOf('%');
        
        // '%'까지의 부분 문자열을 추출
         percentage = percentage.substring(0, percentIndex + 1);
         
          percentage = percentage.replaceAll("[^0-9.%]", "");
         
         BigDecimal value = new BigDecimal(percentage.replace("%", "").trim());
   	     return value.divide(BigDecimal.valueOf(100));
       
		 
	}

	// 퍼센트 값을 BigDecimal로 변환하는 메서드
	public static String convertPercent(String cellText) {
		if (cellText != null && cellText.endsWith("%")) {
			BigDecimal percentValue = new BigDecimal(cellText.replaceAll("%", "").trim()).divide(new BigDecimal(100));
			return percentValue.toPlainString();
		}
		return cellText;
	}

	// "+"가 포함된 퍼센트 수식을 변환하는 메서드
	public static String convertFormula(String cellText) {
		if (cellText != null && cellText.endsWith("%") && cellText.contains("+")) {
			String[] splitWords = cellText.split("\\+");
			StringBuilder convertedFormula = new StringBuilder();

			for (int i = 0; i < splitWords.length; i++) {
				BigDecimal value = new BigDecimal(splitWords[i].replaceAll("%", "").trim()).divide(new BigDecimal(100));
				if (i > 0) {
					convertedFormula.append(" + ");
				}
				convertedFormula.append(value.toPlainString());
			}
			return convertedFormula.toString();
		}
		return cellText;
	}

		// 테이블의 데이터를 처리하는 공통 메서드
	public static String[][] processTableElements(ArrayList<AbstractElement> tableElements, boolean handleFormula) {
		    List<String[]> tempArrayList = new ArrayList<>();

		    for (AbstractElement element : tableElements) {
		        if (element instanceof TableElement) {
		            TableElement table = (TableElement) element;
		            RowElement[] rows = table.getRows();

		            for (RowElement row : rows) {
		                String[] rowData = new String[row.getCells().length - 1];

		                for (int colIndex = 1; colIndex < row.getCells().length; colIndex++) {
		                    String cellText = row.getCells()[colIndex].getText();

		                    if (handleFormula && cellText != null && cellText.contains("+")) {
		                        rowData[colIndex - 1] = convertFormula(cellText);
		                    } else {
		                        rowData[colIndex - 1] = convertPercent(cellText);
		                    }
		                }
		                tempArrayList.add(rowData);
		            }
		        }
		    }

		    String[][] resultArray = new String[tempArrayList.size()][];
		    for (int i = 0; i < tempArrayList.size(); i++) {
		        resultArray[i] = tempArrayList.get(i);
		    }

		    return resultArray;
		}
		
	public static String intToStr(int change) {
		return String.valueOf(change);
	}
	
	
	/**
	 * %가 붙어있는 숫자와  그뒤에있는 숫자를 곱하는 유틸
	 * 4.0% TIMES Min ( n , 20 )
	 */
	  public static BigDecimal multiplyPercentAndNumber(String input) {
	        // 정규 표현식을 이용하여 퍼센트 값과 숫자 값을 추출
	        Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)%.*?(\\d+\\.?\\d*)");
	        Matcher matcher = pattern.matcher(input);

	        if (matcher.find()) {
	            // 퍼센트 값 추출 후 % 제거하고 BigDecimal로 변환
	            BigDecimal percentage = new BigDecimal(matcher.group(1)).divide(BigDecimal.valueOf(100));
	            // 숫자 값 추출 후 BigDecimal로 변환
	            BigDecimal number = new BigDecimal(matcher.group(2));

	            // 두 값 곱하기
	            BigDecimal result = percentage.multiply(number);

	            // 원하는 자리수로 반올림 (소수점 2자리)
	            result = result.setScale(2, BigDecimal.ROUND_HALF_UP);

	            return result;
	        } else {
	            throw new IllegalArgumentException("Input string does not match the expected pattern.");
	        }
	    }
	
	/**
	 * 특정 키워드를 topLevel에서 찾아 인덱스를 반환
	 */
	public static int findStartTopLevelIndex(List<String> topLevel, String keyword) {
		for (int i = 0; i < topLevel.size(); i++) {
			if (keyword.equals(topLevel.get(i).trim())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ( ~ ) 안에 있는 단어 추출
	 * 
	 * @param bojongName
	 * @return
	 */
	public static String returnName(String bojongName) {
		Pattern pattern = Pattern.compile(Const.BOJONG_NAME_REGEX);

		Matcher matcher = pattern.matcher(bojongName);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}
	
    // 특정 키워드까지 문자열을 잘라내는 메소드
    public static String utilityUntilKeyword(String input, String keyword) {
        int index = input.indexOf(keyword);
        
        // 키워드가 존재할 경우에만 해당 부분까지 잘라냅니다.
        if (index != -1) {
            return input.substring(0, index + keyword.length()); // 키워드 포함하여 자르기
        }
        return input; // 키워드가 없으면 원본 문자열 반환
    }
    

}
