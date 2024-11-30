package test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.parse.document.DataExtractContext;
import com.parse.document.common.Const;
import com.parse.document.DocumentExtractorHwp;
import com.parse.document.FileDataExtractorHwp;
import com.parse.document.common.parse.AbstractElement;

public class testAB extends FileDataExtractorHwp {

	private DocumentExtractorHwp documentExtractorHwp = new DocumentExtractorHwp();

	public static void main(String[] args) {

		IProgressMonitor monitor = new NullProgressMonitor();
		DataExtractContext context = new DataExtractContext();

		testAB testInstance = new testAB();
		testInstance.makeValue(monitor, context);
	}

	public Object makeValue(IProgressMonitor monitor, DataExtractContext context) {
		getHWPFile(monitor, context, Const.YACK_GUAN_NAME);
		try {
			processDocument(monitor, context, Const.YACK_GUAN_NAME, documentExtractorHwp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<AbstractElement[]> elementList = (List<AbstractElement[]>) context
				.get("Elements.Hwp." + Const.YACK_GUAN_NAME);

		if (elementList != null) {
			JSONArray jsonArray = new JSONArray();

			for (AbstractElement[] elementArray : elementList) {
				for (AbstractElement element : elementArray) {
					// AbstractElement를 JSON으로 변환하여 JSONArray에 추가
					JSONObject jsonObject = convertElementToJson(element);
					jsonArray.add(jsonObject);
				}
			}

			// JSON 배열을 세로 포맷으로 저장
			saveJsonToFile(jsonArray, "C:\\Temp\\output.json");
		} else {
			System.out.println("No elements found in the context.");
		}

		return null;
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

	public void saveJsonToFile(JSONArray jsonArray, String filePath) {
		try (FileWriter fileWriter = new FileWriter(filePath)) {
			for (Object obj : jsonArray) {
				JSONObject jsonObject = (JSONObject) obj;
				fileWriter.write(jsonObject.toJSONString());
				fileWriter.write("\n"); // 각 JSON 객체를 새로운 줄에 작성
			}
			System.out.println("JSON data has been saved to: " + filePath);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save JSON to file", e);
		}
	}
}
