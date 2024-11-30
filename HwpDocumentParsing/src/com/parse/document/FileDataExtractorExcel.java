package com.parse.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Util;

public abstract class FileDataExtractorExcel extends FileDataExtractor {
	protected static final String ROW_LAYOUT_KEY = "RowData";
	protected static final String COL_LAYOUT_KEY = "ColData";

	@SuppressWarnings("unchecked")
	public Map<Integer, List<String>> getExcelFile(IProgressMonitor monitor, DataExtractContext context, String key, String filePath [], String title,
			String sheetName, int startCol, int startRow) {

		Object object = context.get(key);
		if (object != null) {
			if (object instanceof List) {
				return (Map<Integer, List<String>>) object;
			} else {
				return null;
			}
		}

		Map<Integer, List<String>> excelData = null;
		try {
			File file = download(monitor, context, title, filePath);
			if (file == null) {
				throw new RuntimeException(MessageFormat.format("\"{0}\" 엑셀파일을 찾지 못하였습니다.!", "상품코드 LIST"));
			}

			excelData = extractDataFromExcel(monitor, context, file, key, sheetName, startCol, startRow);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format("\"{0}\" 엑셀파일 파싱 실패!", "상품코드 LIST"), e);
		}

		return excelData;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<String>> getExcelFileEx(IProgressMonitor monitor, DataExtractContext context, String key, String filePath [], String title,
			String sheetName, int[] startCol, int startRow) {

		Object object = context.get(key);
		if (object != null) {
			if (object instanceof List) {
				return (Map<Integer, List<String>>) object;
			} else {
				return null;
			}
		}

		Map<Integer, List<String>> excelData = null;
		try {
			File file = download(monitor, context, title, filePath);
			if (file == null) {
				throw new RuntimeException(MessageFormat.format("\"{0}\" 엑셀파일을 찾지 못하였습니다.!", "상품코드 LIST"));
			}

			excelData = extractDataFromExcelEx(monitor, context, file, key, sheetName, startCol, startRow);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(MessageFormat.format("\"{0}\" 엑셀파일 파싱 실패!", "상품코드 LIST"), e);
		}

		return excelData;
	}
	public Map<Integer, List<String>> extractDataFromExcelEx(IProgressMonitor monitor, DataExtractContext context,
			File file, String key, String sheetName, int[] startCol, int startRow) {
		// Row 단위로 데이터를 추출하여 context에 저장
		Map<Integer, List<String>> rowData = getRowDataFromSheetEx(file, sheetName, startCol, startRow);
		context.put(ROW_LAYOUT_KEY + "." + sheetName, rowData);

		// Col 단위로 데이터를 추출하여 context에 저장
		Map<Integer, List<String>> colData = getColDataFromSheetEx(file, sheetName, startCol, startRow);
		context.put(COL_LAYOUT_KEY + "." + sheetName, colData);

		return key.equals(ROW_LAYOUT_KEY) ? rowData : colData;
	}


	// Row 단위로 데이터를 읽어들이는 메소드
	private Map<Integer, List<String>> getRowDataFromSheetEx(File file, String sheetName, int[] startCol, int startRow) {
	    Map<Integer, List<String>> rowData = new HashMap<>();

	    try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null) {
	            throw new RuntimeException(sheetName + " 시트를 찾지 못했습니다.");
	        }

	        Map<String, String> mergedCells = getMergedCells(sheet,workbook);
	        for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	            Row row = sheet.getRow(rowIndex);
	            if (row != null) {
					List<String> rowValues = new ArrayList<>();

					for (int i = 0; i < startCol.length; i++) {

						if (startCol[i] < row.getLastCellNum()) {
							int colIndex = startCol[i];

							String cellKey = rowIndex + "," + colIndex;
							String cellValue;

							if (mergedCells.containsKey(cellKey)) {
								cellValue = mergedCells.get(cellKey);
							} else {
								Cell cell = row.getCell(colIndex);
								cellValue = getCellValueAsString(cell, workbook); // 수정된 메서드 호출
							}

							rowValues.add(cellValue);
						}
					}
					rowData.put(rowIndex, rowValues);
				}
			}
	    } catch (IOException e) {
	        throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
	    }
	    return rowData;
	}

	// Col 단위로 데이터를 읽어들이는 메소드
	private Map<Integer, List<String>> getColDataFromSheetEx(File file, String sheetName, int startCol, int startRow) {
	    Map<Integer, List<String>> colData = new HashMap<>();

	    try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null) {
	            throw new RuntimeException(sheetName + " 시트를 찾지 못했습니다.");
	        }

	        Map<String, String> mergedCells = getMergedCells(sheet,workbook);

	        for (int colIndex = startCol; colIndex < sheet.getRow(startRow).getLastCellNum(); colIndex++) {
	            List<String> colValues = new ArrayList<>();

	            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	                String cellKey = rowIndex + "," + colIndex;
	                String cellValue;

	                if (mergedCells.containsKey(cellKey)) {
	                    cellValue = mergedCells.get(cellKey);
	                } else {
	                    Row row = sheet.getRow(rowIndex);
	                    Cell cell = (row != null) ? row.getCell(colIndex) : null;
	                    cellValue = getCellValueAsString(cell, workbook); // 수정된 메서드 호출
	                }

	                colValues.add(Util.replaceBlank(cellValue));
	            }
	            colData.put(colIndex, colValues);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
	    }
	    return colData;
	}

	public Map<Integer, List<String>> extractDataFromExcel(IProgressMonitor monitor, DataExtractContext context,
			File file, String key, String sheetName, int startCol, int startRow) {
		// Row 단위로 데이터를 추출하여 context에 저장
		Map<Integer, List<String>> rowData = getRowDataFromSheet(file, sheetName, startCol, startRow);
		context.put(ROW_LAYOUT_KEY + "." + sheetName, rowData);

		// Col 단위로 데이터를 추출하여 context에 저장
		Map<Integer, List<String>> colData = getColDataFromSheet(file, sheetName, startCol, startRow);
		context.put(COL_LAYOUT_KEY + "." + sheetName, colData);

		return key.equals(ROW_LAYOUT_KEY) ? rowData : colData;
	}

	// Row 단위로 데이터를 읽어들이는 메소드
	private Map<Integer, List<String>> getRowDataFromSheet(File file, String sheetName, int startCol, int startRow) {
	    Map<Integer, List<String>> rowData = new HashMap<>();

	    try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null) {
	            throw new RuntimeException(sheetName + " 시트를 찾지 못했습니다.");
	        }

	        Map<String, String> mergedCells = getMergedCells(sheet,workbook);
	        for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	            Row row = sheet.getRow(rowIndex);
	            if (row != null) {
	                List<String> rowValues = new ArrayList<>();

	                for (int colIndex = startCol; colIndex < row.getLastCellNum(); colIndex++) {
	                    String cellKey = rowIndex + "," + colIndex;
	                    String cellValue;

	                    if (mergedCells.containsKey(cellKey)) {
	                        cellValue = mergedCells.get(cellKey);
	                    } else {
	                        Cell cell = row.getCell(colIndex);
	                        cellValue = getCellValueAsString(cell, workbook); // 수정된 메서드 호출
	                    }

	                    rowValues.add(cellValue);
	                }
	                rowData.put(rowIndex, rowValues);
	            }
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
	    }
	    return rowData;
	}

	// Col 단위로 데이터를 읽어들이는 메소드
	private Map<Integer, List<String>> getColDataFromSheet(File file, String sheetName, int startCol, int startRow) {
	    Map<Integer, List<String>> colData = new HashMap<>();

	    try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null) {
	            throw new RuntimeException(sheetName + " 시트를 찾지 못했습니다.");
	        }

	        Map<String, String> mergedCells = getMergedCells(sheet,workbook);

	        for (int colIndex = startCol; colIndex < sheet.getRow(startRow).getLastCellNum(); colIndex++) {
	            List<String> colValues = new ArrayList<>();

	            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	                String cellKey = rowIndex + "," + colIndex;
	                String cellValue;

	                if (mergedCells.containsKey(cellKey)) {
	                    cellValue = mergedCells.get(cellKey);
	                } else {
	                    Row row = sheet.getRow(rowIndex);
	                    Cell cell = (row != null) ? row.getCell(colIndex) : null;
	                    cellValue = getCellValueAsString(cell, workbook); // 수정된 메서드 호출
	                }

	                colValues.add(Util.replaceBlank(cellValue));
	            }
	            colData.put(colIndex, colValues);
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
	    }
	    return colData;
	}

	// Col 단위로 데이터를 읽어들이는 메소드
	private Map<Integer, List<String>> getColDataFromSheetEx(File file, String sheetName, int[] startCol, int startRow) {
	    Map<Integer, List<String>> colData = new HashMap<>();

	    try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
	        Sheet sheet = workbook.getSheet(sheetName);
	        if (sheet == null) {
	            throw new RuntimeException(sheetName + " 시트를 찾지 못했습니다.");
	        }

	        Map<String, String> mergedCells = getMergedCells(sheet, workbook);

	        // Loop through the specified column indices in startCol
	        for (int i = 0; i < startCol.length; i++) {
	            int colIndex = startCol[i];
	            List<String> colValues = new ArrayList<>();

	            // Loop through rows starting from startRow
	            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	                String cellKey = rowIndex + "," + colIndex;
	                String cellValue;

	                // Check if the cell is part of a merged cell
	                if (mergedCells.containsKey(cellKey)) {
	                    cellValue = mergedCells.get(cellKey);
	                } else {
	                    Row row = sheet.getRow(rowIndex);
	                    Cell cell = (row != null) ? row.getCell(colIndex) : null;
	                    cellValue = getCellValueAsString(cell, workbook); // 수정된 메서드 호출
	                }

	                colValues.add(Util.replaceBlank(cellValue)); // Add the cell value to the list
	            }

	            colData.put(colIndex, colValues); // Add the column values to the map
	        }
	    } catch (IOException e) {
	        throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
	    }
	    return colData;
	}
	
	// 병합 셀 정보를 가져오는 메소드
	private Map<String, String> getMergedCells(Sheet sheet, Workbook workbook) {
	    Map<String, String> mergedCells = new HashMap<>();

	    // 병합된 셀의 주소와 값을 맵에 저장
	    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
	        CellRangeAddress range = sheet.getMergedRegion(i);
	        int firstRow = range.getFirstRow();
	        int lastRow = range.getLastRow();
	        int firstCol = range.getFirstColumn();
	        int lastCol = range.getLastColumn();

	        // 병합된 첫 번째 셀의 값 가져오기 (수식 계산 포함)
	        String firstCellValue = getCellValueAsString(sheet.getRow(firstRow).getCell(firstCol), workbook);

	        // 병합된 범위의 모든 셀에 동일한 값 할당
	        for (int row = firstRow; row <= lastRow; row++) {
	            for (int col = firstCol; col <= lastCol; col++) {
	                mergedCells.put(row + "," + col, firstCellValue);
	            }
	        }
	    }
	    return mergedCells;
	}

	// 셀 값을 문자열로 변환하는 헬퍼 메소드 (수식 결과값 포함)
	private String getCellValueAsString(Cell cell, Workbook workbook) {
	    if (cell == null) {
	        return "";
	    }

	    // 수식 계산을 위한 FormulaEvaluator 생성
	    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
	    DataFormatter formatter = new DataFormatter();  // 표시 형식 유지

	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case NUMERIC:
	            // 숫자 셀의 값을 형식화하여 반환 (110.0000001 -> 110 로 변환)
	            return formatter.formatCellValue(cell);
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            // 수식으로 계산 된 값 반환(+W15 -> 14)
	            return formatter.formatCellValue(cell, evaluator);
	        case BLANK:
	            return "";
	        default:
	            return "허용되지 않은 Cell Type >>>>> " + cell.getCellType().toString();
	    }
	}
}
