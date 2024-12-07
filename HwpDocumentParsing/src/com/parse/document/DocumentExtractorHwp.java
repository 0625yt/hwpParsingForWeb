
package com.parse.document;

import kr.dogfoot.hwplib.object.bodytext.control.Control;
import kr.dogfoot.hwplib.object.bodytext.control.ControlColumnDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ControlEquation;
import kr.dogfoot.hwplib.object.bodytext.control.ControlFooter;
import kr.dogfoot.hwplib.object.bodytext.control.ControlNewNumber;
import kr.dogfoot.hwplib.object.bodytext.control.ControlOverlappingLetter;
import kr.dogfoot.hwplib.object.bodytext.control.ControlPageHide;
import kr.dogfoot.hwplib.object.bodytext.control.ControlPageNumberPosition;
import kr.dogfoot.hwplib.object.bodytext.control.ControlSectionDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ControlTable;
import kr.dogfoot.hwplib.object.bodytext.control.gso.GsoControl;
import kr.dogfoot.hwplib.object.bodytext.control.table.Cell;
import kr.dogfoot.hwplib.object.bodytext.control.table.Row;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPChar;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharControlExtend;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharControlInline;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.HWPCharNormal;
import kr.dogfoot.hwplib.object.etc.HWPString;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.parse.document.common.Const;
import com.parse.document.common.HwpUtil;
import com.parse.document.common.Util;
import com.parse.document.common.enums.ContentsType;
import com.parse.document.common.enums.ElementType;
import com.parse.document.common.factory.ElementFactory;
import com.parse.document.common.factory.ElementFactoryYegyu;
import com.parse.document.common.factory.ElementFactorySabang;
import com.parse.document.common.factory.ElementFactorySanbang;
import com.parse.document.common.parse.AbstractElement;
import com.parse.document.common.parse.CellElement;
import com.parse.document.common.parse.Outline;
import com.parse.document.common.parse.ParagraphElement;
import com.parse.document.common.parse.RowElement;
import com.parse.document.common.parse.TableData;
import com.parse.document.common.parse.TableElement;

/**
 * HWP 파일에서 문서를 추출하고 파싱하는 클래스입니다. HWP 파일의 섹션, 문단, 테이블 등의 요소를 파싱하여
 * AbstractElement로 변환합니다.
 * 
 * @author YTHONG
 */
public class DocumentExtractorHwp {

	private ElementFactory elementFactory; // ElementFactory 객체를 관리하는 변수
	private List<TableData> tableDataList = new ArrayList<>(); // TableData 객체 리스트를 저장할 변수 추가

	/**
	 * 문서별 ElementFactory 객체를 가져오는 메서드
	 * 
	 * @param doctype
	 * @return
	 */
	protected ElementFactory getElementFactory(String doctype) {
		/**
		 * 0830 문서형태별로 나누기
		 */

		if (doctype.equals(Const.SABANG_NAME)) {
			elementFactory = new ElementFactorySabang();
			return elementFactory;
		} else if (doctype.equals(Const.SANBANG_NAME)) {
			elementFactory = new ElementFactorySanbang();
			return elementFactory;

		} else if (doctype.equals(Const.YE_GYU_NAME)) {
			elementFactory = new ElementFactoryYegyu();
			return elementFactory;

		} else {

			elementFactory = new ElementFactory(); // ElementFactory가 null인 경우 새로 생성
		}

		return elementFactory;
	}

	/**
	 * 주어진 HWP 파일을 파싱하여 AbstractElement 리스트로 반환합니다.
	 * @param multipleCheck 
	 *
	 * @param filename 파싱할 HWP 파일의 경로
	 * @return 추출된 AbstractElement 리스트
	 * @throws Exception 파일을 읽지 못하거나 파싱할 수 없는 경우 예외 발생
	 */
	public List<AbstractElement[]> extractElement(IProgressMonitor monitor, DataExtractContext context, String title,
	        List<List<Paragraph>> allFilesParagraphs, boolean multipleCheck) throws Exception {
	    ArrayList<AbstractElement[]> fileElementArrays = new ArrayList<>();

	    for (List<Paragraph> paragraphs : allFilesParagraphs) {
	        int elementSequence = 1; // 각 파일의 처리 시작 시 elementSequence를 1로 초기화
	        ArrayList<AbstractElement> elementList = new ArrayList<>(); // 이 리스트에 요소를 계속 추가

	        /**
	         * 각 문단을 순회하며 createElementFromParagraph에서 반환된 요소들을 elementList에 추가
	         */
	        for (Paragraph paragraph : paragraphs) {
	            List<AbstractElement> elements = createElementFromParagraph(paragraph, elementSequence, title);
	            elementList.addAll(elements); // 반환된 elements 리스트를 전체 elementList에 추가
	            elementSequence += elements.size(); // 반환된 요소 수만큼 시퀀스를 증가시킴
	        }

	        // 요소들로 계층 구조를 빌드
	        buildHierarchyForElements(elementList);

	        // 계층화된 AbstractElement들을 배열로 변환하여 fileElementArrays에 추가
	        AbstractElement[] elementsArray = elementList.toArray(new AbstractElement[elementList.size()]);
	        fileElementArrays.add(elementsArray);
	    }

	    
	    // 같은 키로 모든 파일에 대한 AbstractElement 배열 리스트를 context에 저장
	    context.put("Elements.Hwp." + title, fileElementArrays);
	    
	    if (!multipleCheck) {
	        makeOutlines(context, title, fileElementArrays);
	    } else {
	        makeMultipleOutlines(context, title, fileElementArrays);
	    }

	    return fileElementArrays;
	}
	
	/**
	 * 파싱 결과를 Json 파일로 업로드
	 * @param monitor
	 * @param context
	 * @param title
	 * @param check
	 */
	public void processElementsFromContext(IProgressMonitor monitor, DataExtractContext context, String title, boolean check) {

	    List<AbstractElement[]> elementArrays = (List<AbstractElement[]>) context.get("Elements.Hwp." + title);

	    if (elementArrays == null || elementArrays.isEmpty()) {
	        System.out.println("No elements found in context for title: " + title);
	        return;
	    }

	    int fileIndex = 1; // For creating unique file names

	    for (AbstractElement[] elementsArray : elementArrays) {
	        JSONArray jsonArray = new JSONArray();

	        for (AbstractElement element : elementsArray) {
	            JSONObject jsonObject = convertElementToJson(element); // Convert to JSON
	            jsonArray.add(jsonObject);
	        }

	        // Save each array as a JSON file
	        String uniqueFileName = generateUniqueFileName("output_" + title + "_elements" + fileIndex, "json");
	        saveJsonToFile(jsonArray, Const.JSON_UPLOAD_PATH + uniqueFileName);
	        fileIndex++;
	    }
	    
	}
	
	public void printElement(DataExtractContext context, String title) {
		List<AbstractElement[]> elementArrays = (List<AbstractElement[]>) context.get("Elements.Hwp." + title);

		for (AbstractElement[] elementsArray : elementArrays) {
			for (AbstractElement element : elementsArray) {
				printElementRecursive(element, 0);
			}
		}

	}
	
	private void printElementRecursive(AbstractElement element, int depth) {
		if (element == null) {
			return;
		}

		// 들여쓰기 및 현재 요소 정보 출력
		String indent = "  ".repeat(depth);
		//System.out.println(indent + "[Depth " + depth + "] Element:");
		System.out.println(indent + "  Text: " + element.getText());
		//System.out.println(indent + "  No: " + element.getNo());
		//System.out.println(
		//		indent + "  ContentsType: " + (element.getContentsType() != null ? element.getContentsType() : "null"));
		//System.out.println(
		//		indent + "  ElementType: " + (element.getElementType() != null ? element.getElementType() : "null"));

		// 자식 요소 재귀적으로 출력
		for (AbstractElement child : element.getChildren()) {
			printElementRecursive(child, depth + 1);
		}
	}
	
	public JSONObject convertElementToJson(AbstractElement element) {
	    JSONObject jsonObject = new JSONObject();
	    jsonObject.put("no", element.getNo());
	    jsonObject.put("contentsType", element.getContentsType() != null ? element.getContentsType().toString() : null);
	    jsonObject.put("children", getChildrenAsJsonArray(element));
	    jsonObject.put("text", element.getText());
	    jsonObject.put("elementType", element.getElementType() != null ? element.getElementType().toString() : null);
	    return jsonObject;
	}

	private JSONArray getChildrenAsJsonArray(AbstractElement element) {
	    JSONArray childrenArray = new JSONArray();
	    for (AbstractElement child : element.getChildren()) {
	        childrenArray.add(convertElementToJson(child));
	    }
	    return childrenArray;
	}
	
	private String generateUniqueFileName(String baseName, String extension) {
		String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    String uuid = UUID.randomUUID().toString();
	    return String.format("%s_%s_%s.%s", baseName, timestamp, uuid, extension);
	}
	
	public void saveJsonToFile(JSONArray jsonArray, String filePath) {
	    try (FileWriter fileWriter = new FileWriter(filePath)) {
	        fileWriter.write(jsonArray.toJSONString());
	        System.out.println("JSON data has been saved to: " + filePath);
		} catch (IOException e) {
	        throw new RuntimeException("Failed to save JSON to file", e);
	    }
	}

	/**
	 * AbstractElement 리스트에 대해 계층 구조를 빌드하는 메서드
	 */
	private void buildHierarchyForElements(ArrayList<AbstractElement> elementList) {
	    if (elementList == null || elementList.isEmpty()) {
	        return;
	    }

	    // 트리 구조로 계층을 빌드
	    ParagraphElement root = new ParagraphElement(ElementType.TEXT);
	    ElementFactory elementFactory = new ElementFactory(); // ElementFactory를 사용하여 요소 생성

	    elementFactory.buildHierarchy(root, elementList); // root를 기준으로 계층 구조 빌드

	    // 기존 리스트 비우기
	    elementList.clear();

	    // 새 계층 구조의 자식 요소들을 elementList에 추가
	    elementList.addAll(root.getChildren());
	}
	
	/**
	 * 목차 정보를 생성, 기초서류 문서의 기본 목차 정보를 건너 뛰고, 본문의 내용을 기반으로 목차 생성
	 * 
	 * @param context
	 * @param title
	 * @param elementsList
	 */
	public void makeOutlines(DataExtractContext context, String title, ArrayList<AbstractElement[]> elementsList) {
		ArrayList<Outline[]> fileOutlineArrays = new ArrayList<>();

		for (int fileIndex = 0; fileIndex < elementsList.size(); fileIndex++) {
			AbstractElement[] elements = elementsList.get(fileIndex);
			if (elements == null || elements.length == 0) {
				continue;
			}

			Outline[] outlineArray = new Outline[elements.length];
			int outlineIndex = 0;
			boolean outlineCheck = false;
			boolean secondGwanFound = false;

			for (AbstractElement ele : elements) {
				if (ele != null) {
					ContentsType type = ele.getContentsType();

					if (!outlineCheck) {
						// 첫 번째 관(GWAN) 또는 편(PYUN)이 발견되면 outlineCheck 활성화
						if (type == ContentsType.GWAN && Util.findFirstGwan(ele)) {
							outlineCheck = true;
						}
					} else {
						// 두 번째 관(GWAN)이 발견되면 secondGwanFound 활성화
						if (type == ContentsType.GWAN && Util.findFirstGwan(ele)) {
							secondGwanFound = true;
						}
						// 두 번째 관(GWAN)이 발견되고 관(GWAN), 편(PYUN), 또는 조(JO)가 나타나면 새 outline 생성
						if (secondGwanFound && isOutlineType(type)) {
							outlineArray[outlineIndex++] = new Outline(ele); // 새로운 Outline 객체 생성 및 배열에 추가
						} else if (type == ContentsType.PYUN) {
							outlineArray[outlineIndex++] = new Outline(ele); // PYUN에 대한 Outline 객체 생성 및 배열에 추가
						}
					}
				}
			}
			Outline[] finalOutlineArray = new Outline[outlineIndex];
			System.arraycopy(outlineArray, 0, finalOutlineArray, 0, outlineIndex);

			fileOutlineArrays.add(finalOutlineArray);
		}

		context.put("Outlines.Hwp." + title, fileOutlineArrays);
	}
	
	/**
	 * 목차 정보를 생성, 기초서류 문서의 기본 목차 정보를 건너 뛰고, 본문의 내용을 기반으로 목차 생성
	 * 
	 * @param context
	 * @param title
	 * @param elementsList
	 */
	public void makeMultipleOutlines(DataExtractContext context, String title, ArrayList<AbstractElement[]> elementsList) {
		ArrayList<Outline[]> fileOutlineArrays = new ArrayList<>();

		for (int fileIndex = 0; fileIndex < elementsList.size(); fileIndex++) {
			AbstractElement[] elements = elementsList.get(fileIndex);
			if (elements == null || elements.length == 0) {
				continue;
			}

			Outline[] outlineArray = new Outline[elements.length];
			int outlineIndex = 0;
		
			for (AbstractElement ele : elements) {
				if (ele != null) {
					ContentsType type = ele.getContentsType();
					if (isOutlineType(type)) {
						outlineArray[outlineIndex++] = new Outline(ele); // 새로운 Outline 객체 생성 및 배열에 추가
					}
				}
			}
			
			Outline[] finalOutlineArray = new Outline[outlineIndex];
			System.arraycopy(outlineArray, 0, finalOutlineArray, 0, outlineIndex);
			fileOutlineArrays.add(finalOutlineArray);
		}

		context.put("Outlines.Hwp." + title, fileOutlineArrays);
	}

	private boolean isOutlineType(ContentsType type) {
		return type == ContentsType.GWAN || type == ContentsType.JO || type == ContentsType.HANG
				|| type == ContentsType.HO || type == ContentsType.MOK || type == ContentsType.ATTACHED;
	}

	/**
	 * 주어진 문단에서 AbstractElement를 생성합니다.
	 *
	 * @param paragraph       HWP 문단 객체
	 * @param elementSequence 현재 요소의 시퀀스 번호
	 * @return 생성된 AbstractElement
	 * @throws Exception 문단 파싱 중 예외 발생 시
	 */
	private ArrayList<AbstractElement> createElementFromParagraph(Paragraph paragraph, int elementSequence, String docType) {
		AbstractElement TableElement = null;
		AbstractElement ParaElement = null;
		StringBuilder paragraphText = new StringBuilder();
		boolean alreadyExecuted = false;
		ArrayList<AbstractElement> elements = new ArrayList<>();
		if (paragraph.getText() != null) {
			List<HWPChar> charList = paragraph.getText().getCharList();
			List<Control> controlList = paragraph.getControlList();

			try {
				for (HWPChar hwpChar : charList) {
					if (hwpChar instanceof HWPCharNormal) {
						paragraphText.append(((HWPCharNormal) hwpChar).getCh());
					} else if (hwpChar instanceof HWPCharControlExtend) {
						Control control = controlList.get(0);
						if (control instanceof ControlTable) {
							TableElement = generateTableDataWithMergedCells((ControlTable) control, elementSequence,docType);
							alreadyExecuted = true;
						} else if (control instanceof ControlEquation) {
							ControlEquation ce = (ControlEquation) control;
							String sce = HwpUtil.getEquationScript(ce);
							paragraphText.append(sce);
						} else if (control instanceof GsoControl) {
							// GsoControl 처리
							String extractedText = HwpUtil.processingGsoControl((GsoControl) control);
							if (!extractedText.isEmpty()) {
								ParaElement = createParagraphElement(extractedText, elementSequence, docType);
							}
							alreadyExecuted = true;
						}
						else if (control instanceof ControlOverlappingLetter) {
							ControlOverlappingLetter co = (ControlOverlappingLetter) control;
							ArrayList<HWPString> l = co.getHeader().getOverlappingLetterList();
							if (l != null) {
								for (HWPString s : l) {
									if (s.getBytes() != null) {
										paragraphText.append(s.toUTF16LEString());
									}
								}
							}
						} else if (control instanceof ControlSectionDefine) {
							paragraphText.append(HwpUtil.processSectionDefine((ControlSectionDefine) control));
						} else if (control instanceof ControlColumnDefine) {
							paragraphText.append(HwpUtil.processColumnDefine((ControlColumnDefine) control));
						} else if (control instanceof ControlPageHide) {
							paragraphText.append(HwpUtil.processPageHide((ControlPageHide) control));
						} else if (control instanceof ControlPageNumberPosition) {
							paragraphText.append(HwpUtil.processPageNumberPosition((ControlPageNumberPosition) control));
						} else if (control instanceof ControlNewNumber) {
							paragraphText.append(HwpUtil.processControlNewNumber((ControlNewNumber) control));
						} else if (control instanceof ControlFooter) {
							for (Paragraph a : ((ControlFooter) control).getParagraphList()) {
							  //  System.out.println("Paragraph Text: " + a.getNormalString());
							    
							    for (Control ctrl : a.getControlList()) { // 문단 내 컨트롤을 확인
							      //  System.out.println("Control Type: " + ctrl.getType());

							        if (ctrl instanceof ControlTable) {
							         //   System.out.println("Found a ControlTable!");
							            // 테이블 내용 출력
							            ControlTable table = (ControlTable) ctrl;
							            for (Row row : table.getRowList()) {
							                for (Cell cell : row.getCellList()) {
							                    for (Paragraph cellParagraph : cell.getParagraphList()) {
							                      //  System.out.println("Cell Text: " + cellParagraph.getNormalString());
							                    }
							                }
							            }
							        }
							    }
							}
						
							paragraphText.append(HwpUtil.processControlFooter((ControlFooter) control));
						} else {
							// 처리되지 않은 Control 타입을 로그로 출력
							System.out.println("Control Class: " + control.getClass().getName());
							//element = createParagraphElement(paragraphText.toString(), elementSequence, docType);
							TableElement = generateTableDataWithMergedCells((ControlTable) control, elementSequence, docType);
							alreadyExecuted = true;
						}
					} else if (hwpChar instanceof HWPCharControlInline) {
						if (hwpChar.getCode() == Const.ASCII_TAB) {
							paragraphText.append("\t");
						}
					}

					if (!alreadyExecuted) {
						ParaElement = createParagraphElement(paragraphText.toString(), elementSequence, docType);
					}
				}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {

			}

		}

	    // TableElement와 ParaElement가 모두 존재하면 둘 다 리스트에 추가
	    if (ParaElement != null && ParaElement.getText().length() > 0) {
	        elements.add(ParaElement);
	    }
	    
	    if (TableElement != null && TableElement.getText().length() > 0) {
	    	if(elements.size()>0) {
	    		TableElement.setNo(elementSequence+1);
	    		elements.add(TableElement);
	    	}else {
	    		elements.add(TableElement);
	    	}
	    	
	    }
	    return elements;
	}
	
	/**
	 * 주어진 문단 텍스트로부터 ParagraphElement를 생성합니다.
	 *
	 * @param paragraphText 문단의 텍스트
	 * @param sequence      요소의 시퀀스 번호
	 * @return 생성된 ParagraphElement
	 */
	private AbstractElement createParagraphElement(String paragraphText, int sequence, String doctype) {
		return getElementFactory(doctype).createParagraphElement(paragraphText, sequence);
	}

	/**
	 * 주어진 테이블로부터 병합된 셀들을 처리 후 TableElement를 생성합니다.
	 * 
	 * ※ 병합 구조 처리 방법 설명
	 * ============================
	 *     구분         | 지급사유 | 지급금액 |
	 * 유형1 | 유형 2| ~~~~  | ~~~~~ |
	 *=============================
	 *
	 *	 위 테이블 형식을 아래와 같이 변환 (병합 해제)
	 *
	 *=============================
	 * 구분  |  구분   | 지급사유 | 지급금액 |
	 * 유형 1| 유형2 | ~~~~  | ~~~~  |
 	 * ============================
 	 * 
	 * @param table    HWP 테이블 객체
	 * @param sequence 요소의 시퀀스 번호
	 * @param docType  문서 타입
	 * @return 생성된 TableElement
	 */
	public TableElement generateTableDataWithMergedCells(ControlTable table, int sequence, String docType) {

	    int maxRows = table.getRowList().size(); // Calculate maxRows
	    int maxColumns = 0;
	    for (Row row : table.getRowList()) {
	        maxColumns = Math.max(maxColumns, row.getCellList().size()); 
	    }
	    
	    String[][] arrayData = new String[maxRows][maxColumns];
	    String[][] mergeType = new String[maxRows][maxColumns];
	    String title = "";

	    TableData tableData = new TableData(maxRows, maxColumns, sequence, title);

	    for (int r = 0; r < table.getRowList().size(); r++) {
	        Row row = table.getRowList().get(r);
	        RowElement rowElement = getElementFactory(docType).createRowElement();
	        rowElement.setRowIndex(r);

	        for (int c = 0; c < row.getCellList().size(); c++) {
	            Cell cell = row.getCellList().get(c);
	            StringBuilder cellText = new StringBuilder();

	            for (int i = 0; i < cell.getParagraphList().getParagraphCount(); i++) {
	                try {
	                	cellText.append(HwpUtil.extractParagraphText(cell.getParagraphList().getParagraph(i)));
	                    if (docType.equals(Const.YE_GYU_NAME)) {
	                        cellText.append(HwpUtil.extractParagraphText(cell.getParagraphList().getParagraph(i)) + "\n");
	                    } else {
	                        cellText.append(HwpUtil.extractParagraphText(cell.getParagraphList().getParagraph(i)));
	                    }
	                } catch (UnsupportedEncodingException e) {
	                    e.printStackTrace();
	                }
	            }

	            int rowIndex = cell.getListHeader().getRowIndex();
	            int colIndex = cell.getListHeader().getColIndex();
	            int rowSpan = cell.getListHeader().getRowSpan();
	            int colSpan = cell.getListHeader().getColSpan();
	            String text = cellText.toString().trim();


	            for (int j = 0; j < cell.getParagraphList().getParagraphCount(); j++) {
	                Paragraph paragraph = cell.getParagraphList().getParagraph(j);
	                if (paragraph != null && paragraph.getControlList() != null && !paragraph.getControlList().isEmpty()) {
	                    Control control = paragraph.getControlList().get(0); // Get the first control
	                    if (control instanceof ControlTable) {
	                        tableData.addControl(control);
	                        continue;
	                    }
	                }
	            }
	            if (rowIndex >= maxRows) {
	            } else if (colIndex >= maxColumns) {
	                arrayData[rowIndex][maxColumns - 1] = text;
	                mergeType[rowIndex][maxColumns - 1] = rowSpan + "|" + colSpan;
	            } else {
	                arrayData[rowIndex][colIndex] = text;
	                mergeType[rowIndex][colIndex] = rowSpan + "|" + colSpan;

	                tableData.setData(rowIndex, colIndex, text);
	                tableData.setSequence(sequence);
	                tableData.setMergeInfo(rowIndex, colIndex, rowSpan + "|" + colSpan);

	                for (int rMerge = rowIndex; rMerge < rowIndex + rowSpan; rMerge++) {
	                    for (int cMerge = colIndex; cMerge < colIndex + colSpan; cMerge++) {
	                        if (rMerge < maxRows && cMerge < maxColumns) {
	                            arrayData[rMerge][cMerge] = text;
	                            mergeType[rMerge][cMerge] = rowSpan + "|" + colSpan;
	                        }
	                    }
	                }
	            }

	        }
	    }

		String[][] mergedArrayData = inputData(inputType(mergeType), arrayData);

		for (int r = 0; r < mergedArrayData.length; r++) {
			for (int c = 0; c < mergedArrayData[r].length; c++) {
				if (mergedArrayData[r][c] != null) {
					tableData.setData(r, c, mergedArrayData[r][c]);
				}
			}
		}

		tableDataList.add(tableData);

	    return populateTableElementsFromTableData(tableData, docType, sequence);
	}

	/**
	 * 병합 처리를 진행 한  2차원 배열인 TableData의 값으로 TableElement 생성
	 * @param tableData
	 * @param docType
	 * @param sequence
	 * @return
	 */
	public TableElement populateTableElementsFromTableData(TableData tableData, String docType, int sequence) {

		TableElement tableElement = getElementFactory(docType).createTableElement();

		int[] length = tableData.getLength();

		for (int r = 0; r < length[0]; r++) {
			RowElement rowElement = getElementFactory(docType).createRowElement();
			rowElement.setRowIndex(r);

			for (int c = 0; c < length[1]; c++) {
				CellElement cellElement = getElementFactory(docType).createCellElement();
				cellElement.setRowIndex(r);
				cellElement.setColumnIndex(c);
				String cellData = tableData.getData(r, c);

				if (cellData != null && !cellData.trim().isEmpty()) {
					rowElement.addCell(cellElement);
				}
				AbstractElement cellContentElement = createParagraphElement(cellData, sequence, docType);
				if (cellContentElement != null) {
					cellElement.addElement(cellContentElement); // Add text content to the CellElement
				}
				if (tableData.getControls() != null) {
					for (Control control : tableData.getControls()) {
						cellElement.addControl(control);
					}
				}
			}

			tableElement.addRow(rowElement);
		}
		//tableData.getTitles();
		tableElement.setNo(sequence);
		tableElement.setControls(tableData.getControls());
		return tableElement;
	}
	
	/**
	 * 병합이 된 Null 값을 가진 셀들에 적절한 값을 채워 넣음
	 * 
	 * @param megerType
	 * @return
	 */
	public String[][] inputType(String[][] megerType) {
		String rowValue = "";
		String colValue = "";
		for (int i = 0; i < megerType.length; i++) {
			for (int j = 0; j < megerType[i].length; j++) {
				if (megerType[i][j] != null) {
					String[] type = megerType[i][j].split(Pattern.quote("|"));
					rowValue = type[0];
					colValue = type[1];
				}
				if (megerType[i][j] == null) {
					if (j == 0) {
						String[] preRow = megerType[i - 1][j].split(Pattern.quote("|"));
						rowValue = preRow[0];
					}
					if (i > 0 && j > 0) {
						String[] preRow = megerType[i - 1][j].split(Pattern.quote("|"));
						String[] preCol = megerType[i][j - 1].split(Pattern.quote("|"));
						rowValue = Integer.parseInt(preCol[0]) > Integer.parseInt(preRow[0]) ? preCol[0] : preRow[0];
					}
					megerType[i][j] = rowValue + "|" + colValue;
				}
			}
		}
		return deepCopy(megerType);
	}

	/**
	 * 병합이 된 Null 값을 가진 셀에 값을 할당
	 * 
	 * @param megerType
	 * @param data
	 * @return
	 */
	public String[][] inputData(String[][] megerType, String[][] data) {
		boolean rowSpan = false;
		boolean colSpan = false;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {

				String[] type = megerType[i][j].split(Pattern.quote("|"));
				rowSpan = Integer.parseInt(type[0]) > 1 ? true : false;
				colSpan = Integer.parseInt(type[1]) > 1 ? true : false;

				if (null == data[i][j]) {
					if (rowSpan) {
						if (i == 0) {
							data[i][j] = data[i][j - 1]; // 첫 행의 경우, 이전 열의 값을 채움
						} else {
							data[i][j] = data[i - 1][j]; // 이전 행의 값을 채움
						}
					} // End if ( rowSpan )

					if (colSpan) {
						if (j == 0) {
							data[i][j] = data[i - 1][j]; // 첫 열의 경우, 이전 행의 값을 채움
						} else {
							data[i][j] = data[i][j - 1]; // 이전 열의 값을 채움
						}
					} // End if ( colSpan )
				} // End if ( null == data[i][j])
			} // End for (int j = 0; j < data[i].length; j++)
		} // End for (int i = 0; i < data.length; i++)
		return deepCopy(data);
	}

	/**
	 * 2차원 배열의 깊은 복사를 수행
	 * 
	 * @param array
	 * @return
	 */
	public String[][] deepCopy(String[][] array) {
		int rows = array.length;
		String[][] newArray = new String[rows][];
		for (int i = 0; i < rows; i++) {
			newArray[i] = Arrays.copyOf(array[i], array[i].length);
		}
		return newArray;
	}

}