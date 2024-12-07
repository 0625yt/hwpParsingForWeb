package test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;
import com.parse.document.DocumentExtractorHwp;
import com.parse.document.FileDataExtractorHwp;
import com.parse.document.common.parse.AbstractElement;

public class ParsingOutput extends FileDataExtractorHwp {

	private DocumentExtractorHwp documentExtractorHwp = new DocumentExtractorHwp();
	private boolean toJson = false;

	public static void main(String[] args) {
		IProgressMonitor monitor = new NullProgressMonitor();
		DataExtractContext context = new DataExtractContext();

		ParsingOutput testInstance = new ParsingOutput();
		testInstance.makeValue(monitor, context, testInstance.toJson);
	}

	public void makeValue(IProgressMonitor monitor, DataExtractContext context, boolean toJson) {
		getHWPFile(monitor, context, Const.YACK_GUAN_NAME);
		try {
			processDocument(monitor, context, Const.YACK_GUAN_NAME, documentExtractorHwp, toJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
