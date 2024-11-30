package com.parse.document.common;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlColumnDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ControlEndnote;
import kr.dogfoot.hwplib.object.bodytext.control.ControlEquation;
import kr.dogfoot.hwplib.object.bodytext.control.ControlFooter;
import kr.dogfoot.hwplib.object.bodytext.control.ControlFootnote;
import kr.dogfoot.hwplib.object.bodytext.control.ControlHeader;
import kr.dogfoot.hwplib.object.bodytext.control.ControlHiddenComment;
import kr.dogfoot.hwplib.object.bodytext.control.ControlNewNumber;
import kr.dogfoot.hwplib.object.bodytext.control.ControlOverlappingLetter;
import kr.dogfoot.hwplib.object.bodytext.control.ControlPageHide;
import kr.dogfoot.hwplib.object.bodytext.control.ControlPageNumberPosition;
import kr.dogfoot.hwplib.object.bodytext.control.ControlSectionDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.ControlType;
import kr.dogfoot.hwplib.object.bodytext.control.equation.EQEdit;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlArc;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlContainer;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlCurve;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlEllipse;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlPolygon;
import kr.dogfoot.hwplib.object.bodytext.control.gso.ControlRectangle;
import kr.dogfoot.hwplib.object.bodytext.control.gso.GsoControl;
import kr.dogfoot.hwplib.object.bodytext.control.gso.GsoControlType;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPChar;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharControlChar;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharControlExtend;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharControlInline;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharNormal;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.object.etc.HWPString;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.parse.document.DocumentExtractorHwp;
import com.parse.document.FileDataExtractorHwp;

public class HwpUtil extends FileDataExtractorHwp{

    /**
     * HWP 문단의 텍스트를 추출하는 메서드입니다.
     *
     * @param paragraph HWP 문단 객체
     * @return 문단의 텍스트를 반환
     * @throws Exception 
     */
    public static String getParagraphText(Paragraph paragraph) throws Exception {
        StringBuilder buffer = new StringBuilder();

        if (paragraph.getText() != null) {
            List<HWPChar> charList = paragraph.getText().getCharList();
            int controlIndex = -1;
            List<Control> controlList = paragraph.getControlList();

            for (int i = 0; i < charList.size(); i++) {
                HWPChar hwpChar = charList.get(i);

                if (hwpChar instanceof HWPCharNormal) {
                    buffer.append(((HWPCharNormal) hwpChar).getCh());
                } else if (hwpChar instanceof HWPCharControlChar) {
                    if (hwpChar.getCode() == 0x20 || hwpChar.getCode() == 0xA0) {
                        buffer.append(" ");
                    }
                } else if (hwpChar instanceof HWPCharControlExtend) {
                    controlIndex++;
                    if (controlIndex < controlList.size()) {
                        Control control = controlList.get(controlIndex);
                        if (control instanceof ControlEquation) {
                            ControlEquation equation = (ControlEquation) control;
                            buffer.append(equation.getEQEdit().getScript().toUTF16LEString());
                        } else if (control instanceof ControlTable) {
                        }
                    }
                } else if (hwpChar instanceof HWPCharControlInline && hwpChar.getCode() == 0x09) {
                    buffer.append("\t");
                }
            }
        }
        return buffer.toString().trim();
    }

    /**
     * HWP 파일에서 셀에 포함된 모든 문단의 텍스트를 추출하는 메서드입니다.
     *
     * @param cell HWP 셀 객체
     * @return 셀의 모든 문단 텍스트를 연결한 문자열을 반환
     * @throws Exception 
     */
    public static String getCellText(Cell cell) throws Exception {
        StringBuilder cellText = new StringBuilder();
        for (int i = 0; i < cell.getParagraphList().getParagraphCount(); i++) {
            Paragraph paragraph = cell.getParagraphList().getParagraph(i);
            cellText.append(getParagraphText(paragraph)).append("\n");
        }
        return cellText.toString().trim();
    }

    /**
     * 주어진 테이블에서 모든 셀의 텍스트를 추출하는 메서드입니다.
     *
     * @param table HWP 테이블 객체
     * @return 테이블의 모든 셀 텍스트를 반환
     * @throws Exception 
     */
    public static List<String> extractTableText(ControlTable table) throws Exception {
        List<String> tableTextList = new ArrayList<>();
        for (Row row : table.getRowList()) {
            for (Cell cell : row.getCellList()) {
                String cellText = getCellText(cell);
                tableTextList.add(cellText);
            }
        }
        return tableTextList;
    }
	/**
	 * Control 객체의 타입에 따라 텍스트를 처리하여 반환
	 * 
	 * @param control
	 * @return
	 * @throws Exception
	 */
	public static String processingControl(Control control) throws Exception {
		StringBuilder extractedText = new StringBuilder();

		// Control 타입에 따라 텍스트 처리
		if (control.getType() == ControlType.Header) {
			extractedText.append(processingParagraph(((ControlHeader) control).getParagraphList().getParagraphs()));
		} else if (control.getType() == ControlType.Footer) {
			extractedText.append(processingParagraph(((ControlFooter) control).getParagraphList().getParagraphs()));
		} else if (control.getType() == ControlType.Endnote) {
			extractedText.append(processingParagraph(((ControlEndnote) control).getParagraphList().getParagraphs()));
		} else if (control.getType() == ControlType.Footnote) {
			extractedText.append(processingParagraph(((ControlFootnote) control).getParagraphList().getParagraphs()));
		} else if (control.getType() == ControlType.HiddenComment) {
			extractedText.append(processingParagraph(((ControlHiddenComment) control).getParagraphList().getParagraphs()));
		} else if (control.getType() == ControlType.Table) {
			ControlTable controlTable = (ControlTable) control;
			for (Row row : controlTable.getRowList()) {
				for (Cell cell : row.getCellList()) {
					extractedText.append(processingParagraph(cell.getParagraphList().getParagraphs()));
				}
			}
		} else if (control.getType() == ControlType.Gso) {
			extractedText.append(processingGsoControl((GsoControl) control));
		} 

		return extractedText.toString(); // 처리된 텍스트 반환
	}

	/**
	 * GsoControl 객체의 타입에 따라 텍스트를 처리하여 반환
	 * 
	 * @param control
	 * @return
	 * @throws Exception
	 */
	public static String processingGsoControl(GsoControl control) throws Exception {
		StringBuilder extractedText = new StringBuilder();

		// 캡션이 있는 경우 캡션 텍스트 처리
		if (control.getCaption() != null) {
			extractedText.append(processingParagraph(control.getCaption().getParagraphList().getParagraphs()));
		}

		// GsoControl 타입에 따라 텍스트 처리
		if (control.getGsoType() == GsoControlType.Arc) {
			if (((ControlArc) control).getTextBox() != null) {
				extractedText.append(
						processingParagraph(((ControlArc) control).getTextBox().getParagraphList().getParagraphs()));
			}
		} else if (control.getGsoType() == GsoControlType.Curve) {
			if (((ControlCurve) control).getTextBox() != null) {
				extractedText.append(
						processingParagraph(((ControlCurve) control).getTextBox().getParagraphList().getParagraphs()));
			}
		} else if (control.getGsoType() == GsoControlType.Ellipse) {
			if (((ControlEllipse) control).getTextBox() != null) {
				extractedText.append(processingParagraph(
						((ControlEllipse) control).getTextBox().getParagraphList().getParagraphs()));
			}
		} else if (control.getGsoType() == GsoControlType.Polygon) {
			if (((ControlPolygon) control).getTextBox() != null) {
				extractedText.append(processingParagraph(
						((ControlPolygon) control).getTextBox().getParagraphList().getParagraphs()));
			}
		} else if (control.getGsoType() == GsoControlType.Rectangle) {
			if (((ControlRectangle) control).getTextBox() != null) {
				extractedText.append(processingParagraph(
						((ControlRectangle) control).getTextBox().getParagraphList().getParagraphs()));
			}
		} else if (control.getGsoType() == GsoControlType.Container) {
			ControlContainer controlContainer = (ControlContainer) control;
			for (GsoControl child : controlContainer.getChildControlList()) {
				extractedText.append(processingGsoControl(child)); // 자식 GsoControl 처리
			}
		}

		return extractedText.toString(); // 처리된 텍스트 반환
	}
	/**
	 * 주어진 문단에서 텍스트를 추출하여 반환합니다.
	 *
	 * @param paragraph HWP 문단 객체
	 * @return 추출된 문단 텍스트
	 * @throws UnsupportedEncodingException 인코딩 예외 발생 시
	 */
	public static String extractParagraphText(Paragraph paragraph,int index , String title) throws UnsupportedEncodingException {
	    StringBuilder paragraphBuffer = new StringBuilder();
	    ParaText paraText = paragraph.getText();
	    List<Control> controlList = paragraph.getControlList();
	    DocumentExtractorHwp docExt = new DocumentExtractorHwp();
	    if (paraText != null) {
	        for (HWPChar hwpChar : paraText.getCharList()) {
	            try {
	                if (hwpChar instanceof HWPCharNormal) {
	                    paragraphBuffer.append(((HWPCharNormal) hwpChar).getCh());
	                } else if (hwpChar instanceof HWPCharControlChar) {
	                    switch (hwpChar.getCode()) {
	                        case Const.ASCII_KEEP_WORD_SPACE:
	                        case Const.ASCII_FIXED_WIDTH_SPACE:
	                            paragraphBuffer.append((char) hwpChar.getCode());
	                            break;
	                    }
	                } else if (hwpChar instanceof HWPCharControlExtend) {
	                    for (Control control : controlList) {
	                        if (control instanceof ControlTable) {
	                            String tableText = docExt.generateTableDataWithMergedCells((ControlTable) control, index, title).getOnlyText(); // 예시 함수
	                            paragraphBuffer.append(tableText);
	                        } else if (control instanceof ControlEquation) {
	                            ControlEquation ce = (ControlEquation) control;
	                            String equationText = HwpUtil.getEquationScript(ce);
	                            paragraphBuffer.append(equationText);
	                        } else if (control instanceof GsoControl) {
	                            String extractedText = HwpUtil.processingGsoControl((GsoControl) control);
	                            if (!extractedText.isEmpty()) {
	                                paragraphBuffer.append(extractedText);
	                            }
	                        } else if (control instanceof ControlOverlappingLetter) {
	                            ControlOverlappingLetter co = (ControlOverlappingLetter) control;
	                            ArrayList<HWPString> overlappingLetters = co.getHeader().getOverlappingLetterList();
	                            if (overlappingLetters != null) {
	                                for (HWPString s : overlappingLetters) {
	                                    if (s.getBytes() != null) {
	                                        paragraphBuffer.append(s.toUTF16LEString());
	                                    }
	                                }
	                            }
	                        } else if (control instanceof ControlSectionDefine) {
	                            paragraphBuffer.append(HwpUtil.processSectionDefine((ControlSectionDefine) control));
	                        } else if (control instanceof ControlColumnDefine) {
	                            paragraphBuffer.append(HwpUtil.processColumnDefine((ControlColumnDefine) control));
	                        } else if (control instanceof ControlPageHide) {
	                            paragraphBuffer.append(HwpUtil.processPageHide((ControlPageHide) control));
	                        } else if (control instanceof ControlPageNumberPosition) {
	                            paragraphBuffer.append(HwpUtil.processPageNumberPosition((ControlPageNumberPosition) control));
	                        } else if (control instanceof ControlNewNumber) {
	                            paragraphBuffer.append(HwpUtil.processControlNewNumber((ControlNewNumber) control));
	                        } else {
	                           // System.out.println("Unhandled Control Type: " + control.getType());
	                        }
	                    }
	                } else if (hwpChar instanceof HWPCharControlInline) {
	                    if (hwpChar.getCode() == Const.ASCII_TAB) {
	                        paragraphBuffer.append("\t");
	                    }
	                }
	            } catch (Exception e) {
	                System.err.printf("Character Error: %s, Exception: %s%n", hwpChar.toString(), e.toString());
	            }
	        }
	    }

	    return paragraphBuffer.toString();
	}
	
	public static String extractParagraphText(Paragraph paragraph) throws UnsupportedEncodingException {
	    StringBuilder paragraphBuffer = new StringBuilder(); // 문단 텍스트를 저장할 StringBuilder
	    ParaText paraText = paragraph.getText(); // 문단 텍스트 객체 가져오기
	    List<Control> controlList = paragraph.getControlList(); // 문단에 있는 컨트롤 리스트 가져오기

	    if (paraText != null) {
	        for (HWPChar hwpChar : paraText.getCharList()) { // 문단의 각 문자에 대해 반복
	            try {
	                if (hwpChar instanceof HWPCharNormal) {
	                    // 일반 문자일 경우 텍스트에 추가
	                    paragraphBuffer.append(((HWPCharNormal) hwpChar).getCh());
	                } else if (hwpChar instanceof HWPCharControlChar) {
	                    // 특정 제어 문자인 경우 처리
	                    switch (hwpChar.getCode()) {
	                        case Const.ASCII_KEEP_WORD_SPACE: // 단어 간격 유지 문자
	                        case Const.ASCII_FIXED_WIDTH_SPACE: // 고정 폭 공백 문자
	                            paragraphBuffer.append((char) hwpChar.getCode());
	                            break;
	                    }
	                } else if (hwpChar instanceof HWPCharControlExtend) {
	                    // 수식, 이미지 등 컨트롤 확장 문자가 있을 때
	                    for (Control control : controlList) {
	                        if (control instanceof ControlEquation) {
	                            // 수식을 찾으면 수식 스크립트를 추출하여 추가
	                            ControlEquation ce = (ControlEquation) control;
	                            String equationText = HwpUtil.getEquationScript(ce);
	                            paragraphBuffer.append(equationText);
	                        }
	                    }
	                }
	            } catch (Exception e) {
	                // 오류 발생 시 처리
	                System.err.printf("Character Error: %s, Exception: %s%n", hwpChar.toString(), e.toString());
	            }
	        }
	    }

	    return paragraphBuffer.toString(); // 추출된 문단 텍스트 반환
	}
	/**
	 * Paragraph 배열의 텍스트를 처리하여 하나의 문자열로 반환
	 * 
	 * @param paragraphs
	 * @return
	 * @throws Exception
	 */
	public static String processingParagraph(Paragraph[] paragraphs) throws Exception {
		StringBuilder paragraphText = new StringBuilder();

		if (paragraphs != null) {
			for (Paragraph paragraph : paragraphs) {
				paragraphText.append(extractParagraphText(paragraph)).append("\n");
			}
		}

		return paragraphText.toString(); // 처리된 문단 텍스트 반환
	}
    
	public static ControlSectionDefine getSectionDefineControl(HWPFile hwpFile) {
		Section s = hwpFile.getBodyText().getSectionList().get(0);
		return getSectionDefineControl(s);
	}
	
	public static ControlSectionDefine getSectionDefineControl(Section s) {
		Paragraph firstParagraph = s.getParagraph(0);
		
		ArrayList<Control> controlList = firstParagraph.getControlList();
		if(controlList != null) {
			for(Control control : controlList) {
				if(control instanceof ControlSectionDefine) {
					ControlSectionDefine def = (ControlSectionDefine)control;
					return def;
				}
			}
		}
		return null;
	}

	public static String getEquationScript(ControlEquation ce) {
		if(ce == null) {
			return "";
		}
		
		EQEdit eq = ce.getEQEdit();
		if(eq == null) {
			return "";
		}
		HWPString hwps = eq.getScript();
		return hwps != null ? hwps.getBytes() != null ? hwps.toUTF16LEString() : "" : "";
	}
	
	public static Object processSectionDefine(ControlSectionDefine control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processColumnDefine(ControlColumnDefine control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processPageHide(ControlPageHide control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processPageNumberPosition(ControlPageNumberPosition control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processControlNewNumber(ControlNewNumber control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processControlFootnote(ControlFootnote control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processControlEndnote(ControlEndnote control) {
		// TODO Auto-generated method stub
		return "";
	}

	public static Object processControlFooter(ControlFooter control) {
		// TODO Auto-generated method stub
		return "";
	}
	
	public static Object processFooter(ControlFooter control) {
		// TODO Auto-generated method stub
		return "";
	}
	
	public static void makeNewElement() {
		
	}
}
