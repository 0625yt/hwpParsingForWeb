package com.parse.document;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hslf.util.SystemTimeUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;
import com.parse.document.common.HwpUtil;
import com.parse.document.common.Util;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.reader.HWPReader;

public abstract class FileDataExtractorHwp extends FileDataExtractor {

	private static final String FIND_FIRST_GAWN = "제1관목적및용어의정의";
	private static final String CATEGORY = "약관목차";

	public HWPFile[] getHWPFile(IProgressMonitor monitor, DataExtractContext context, String title) {

		String key = "HwpFileData.Hwp." + title; //$NON-NLS-1$
		Object object = context.get(key);
		if (object != null) {
			if (object instanceof HWPFile[]) {
				return (HWPFile[]) object;
			} else {
				return null;
			}
		}

		List<HWPFile> hwpFileList = new ArrayList<>();
		try {
			File[] files = downloads(monitor, context, title);
			if (files != null) {
				for (File file : files) {
					try (FileInputStream fis = new FileInputStream(file)) { // try-with-resources 구문으로 자동으로 close
						HWPFile hwpFile = HWPReader.fromInputStream(fis);
						if (hwpFile != null) { // Null 체크 추가
							hwpFileList.add(hwpFile);
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println(MessageFormat.format("\"{0}\" Hwp 문서 파싱 실패: {1}", title, e.getMessage()));
		}
		HWPFile[] hwpFiles = hwpFileList.toArray(new HWPFile[hwpFileList.size()]);

		// 각 파일의 HWPFile 배열을 같은 키로 context에 저장
		context.put(key, hwpFiles != null ? hwpFiles : new Object());

		return hwpFiles;
	}

	public void processDocument(IProgressMonitor monitor, DataExtractContext context, String title,
			DocumentExtractorHwp documentExtractor, boolean toJson) throws Exception {
		HWPFile[] hwpFiles = getHWPFile(monitor, context, title); // HWP 파일 배열을 가져옴

		if (hwpFiles != null) {

	//		for (int fileIndex = 0; fileIndex < hwpFiles.length; fileIndex++) {
				List<List<Paragraph>> allFilesParagraphs = new ArrayList<>();
			    
				HWPFile hwpFile = hwpFiles[0];
			    DataExtractContext fileContext = new DataExtractContext();
			    
			    if (hwpFile != null) {
			    	
			    	context.clear();
			        List<Paragraph> paragraphs = new ArrayList<>();

			        for (Section section : hwpFile.getBodyText().getSectionList()) {
			            for (int i = 0; i < section.getParagraphCount(); i++) {
			                Paragraph paragraph = section.getParagraph(i);
			                paragraphs.add(paragraph);
			            }
			        }
			        allFilesParagraphs.add(paragraphs); 
			        documentExtractor.extractElement(monitor, fileContext, title, allFilesParagraphs, false);

			        int progressPercentage = ((0 + 1) * 100) / hwpFiles.length;
			        System.out.println("Progress: " + progressPercentage + "% (" + (- + 1) + "/" + hwpFiles.length + ")");

			        if (toJson) {
						documentExtractor.processElementsFromContext(monitor, fileContext, title, false);
					}else {
						documentExtractor.printElement(fileContext, title);
					}
			    }
		//	}

		}
	}

	/**
	 * 다중 약관 일 때 처리하는 메서드 이전 문구가 "약관 목차"가 아닐 때의 "제1관 목적 및 용어의 정의"를 찾음 찾은 후 -3 또는 -2
	 * 번째에 있는 보종명을 가져와서 시작 부분을 설정 끝 부분은 "약관 목차"가 나올 때까지로 설정
	 * 
	 * @param monitor
	 * @param context
	 * @param title
	 * @param documentExtractor
	 * @param bojongName
	 * @throws Exception
	 */
	public void processDocumentEx(IProgressMonitor monitor, DataExtractContext context, String title,
			DocumentExtractorHwp documentExtractor, String bojongName, boolean toJson) throws Exception {

		// 시작 시간 측정
		long startTime = System.currentTimeMillis();

		HWPFile[] hwpFiles = getHWPFile(monitor, context, title);
		boolean multipleCheck = yackgawnCheck(hwpFiles, title);
		boolean input = false;

		if (multipleCheck && hwpFiles != null) {
			List<List<Paragraph>> allFilesParagraphs = new ArrayList<>();

			for (HWPFile hwpFile : hwpFiles) {
				if (hwpFile != null) {
					List<Paragraph> paragraphs = new ArrayList<>();

					for (Section section : hwpFile.getBodyText().getSectionList()) {
						for (int i = 0; i < section.getParagraphCount(); i++) {
							paragraphs.add(section.getParagraph(i));
						}
					}

					List<Paragraph> selectedParagraphs = new ArrayList<>();

					for (int i = 0; i < paragraphs.size(); i++) {
						String paragraphText = HwpUtil.extractParagraphText(paragraphs.get(i), i, title);
						String cleanedText = Util.replaceBlank(paragraphText);

						if (cleanedText.equals(FIND_FIRST_GAWN)) {
							if (i >= 3) {
								String precedingText = Util
										.replaceBlank(HwpUtil.extractParagraphText(paragraphs.get(i - 3), i, title));
								if (precedingText.isEmpty()) {
									precedingText = Util.replaceBlank(
											HwpUtil.extractParagraphText(paragraphs.get(i - 2), i, title));
								}

								String prevText = Util
										.replaceBlank(HwpUtil.extractParagraphText(paragraphs.get(i - 1), i, title));
								if (!CATEGORY.equals(prevText) && bojongName.contains(Util.returnName(precedingText))) {
									input = true;
								}
							}
						} else if (input && cleanedText.equals("약관목차")) {
							input = false;
						}

						if (input) {
							selectedParagraphs.add(paragraphs.get(i));
						}
					}

					allFilesParagraphs.add(selectedParagraphs);
				}
			}

			documentExtractor.extractElement(monitor, context, title + bojongName, allFilesParagraphs, true);
		} else {
			processDocument(monitor, context, title, documentExtractor, toJson);
		}

	}

	/**
	 * 약관목차가 2번 이상이면, 다중 약관으로 판단
	 * 
	 * @param hwpFiles
	 * @param title
	 * @return
	 * @throws Exception
	 */
	private boolean yackgawnCheck(HWPFile[] hwpFiles, String title) throws Exception {
		int count = 0;

		if (hwpFiles != null) {
			for (HWPFile hwpFile : hwpFiles) {
				if (hwpFile != null) {
					// HWP 파일의 각 섹션을 순회하며 문단을 수집
					for (Section section : hwpFile.getBodyText().getSectionList()) {
						for (int i = 0; i < section.getParagraphCount(); i++) {
							String paragraphText = HwpUtil.extractParagraphText(section.getParagraph(i), i, title);

							if (Util.replaceBlank(paragraphText).equals(CATEGORY)) {
								count++;
								if (count >= 2) {
									return true; // 약관목차가 2번 이상 발견되면 false 반환
								}
							}
						}
					}
				}
			}
		}

		return false; // 약관목차가 2번 미만이면 true 반환
	}

}
