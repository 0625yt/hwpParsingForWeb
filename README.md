HwpParsingForWeb
HwpParsingForWeb 프로젝트는 한글(HWP) 문서의 데이터를 파싱하고, 이를 웹 애플리케이션에서 활용할 수 있도록 변환하는 Java 기반의 도구입니다. 이 프로젝트는 HWP 문서의 구조적 요소(예: 문단, 테이블, 섹션 등)를 추출하여 JSON 형식으로 변환하거나, 다른 형식으로 데이터를 처리할 수 있게 돕습니다. HWP 파일의 데이터를 웹 애플리케이션에서 사용하려는 목적에 유용하게 활용될 수 있습니다.

기술 스택 및 구성
Java: 이 프로젝트는 Java로 개발되었으며, HWP 파일을 파싱하는 데 사용됩니다.

HwpLib (kr.dogfoot.hwplib): HWP 파일 형식을 읽고 처리하는 라이브러리입니다. HWP 파일의 구조적 요소들을 파싱하고, 이를 객체로 변환하는 데 사용됩니다.

JSON.simple: 데이터 처리 후 JSON 형식으로 변환하는 데 사용됩니다.

Eclipse / IntelliJ IDEA: Java IDE에서 프로젝트를 설정하고 실행할 수 있습니다.

프로젝트 구조
1. DataExtractContext.java
이 클래스는 문서 파싱 중에 필요한 데이터를 저장하고 관리하는 역할을 합니다. Map<String, Object> 자료구조를 이용하여 데이터를 저장하고, 키를 사용하여 데이터를 추출하는 메소드들이 포함되어 있습니다.

주로 데이터를 임시로 저장하고, 후속 작업에서 쉽게 접근할 수 있도록 도와주는 컨텍스트 클래스입니다.

2. DocumentExtractorHwp.java
이 클래스는 HWP 문서에서 문단, 테이블, 섹션 등과 같은 구조적 요소를 추출하는 핵심적인 클래스로, 문서의 각 요소를 파싱하여 AbstractElement 객체로 변환합니다.

HwpLib 라이브러리를 활용하여 각 문서의 컨트롤들을 읽고, 필요한 데이터 형식으로 변환합니다.

이 클래스의 주요 기능은 HWP 파일을 읽고, 문서의 구조적 요소들을 처리하여 추상화된 형태로 변환하는 것입니다.

3. FileDataExtractorHwp.java
이 클래스는 HWP 파일을 실제로 읽고 데이터를 추출하는 기능을 제공합니다. HWP 파일을 FileInputStream으로 열고, 이를 분석하여 DataExtractContext에 데이터를 저장합니다.

Pattern과 Matcher를 사용하여 정규 표현식으로 텍스트 데이터를 필터링하거나 추출하는 작업을 수행합니다.

HWP 파일을 파싱하는 데 중요한 역할을 하며, 파일의 섹션, 문단, 테이블 등을 처리하는 작업을 합니다.

4. common 폴더
common 폴더는 다양한 유틸리티 클래스와 공통된 기능을 제공하는 파일들로 구성되어 있습니다.

Const.java, HwpUtil.java, Util.java 등 다양한 유틸리티 클래스들이 포함되어 있으며, 이 클래스들은 데이터 처리, 파일 관리, 추출된 데이터를 변환하는 데 필요한 여러 공통적인 작업을 수행합니다.

주요 기능
1. HWP 파일 파싱
HWP 문서를 로드하고, 문서 내 다양한 섹션(페이지, 문단, 테이블 등)을 파싱하여 필요한 데이터만을 추출할 수 있습니다.

추출된 데이터는 후속 작업에서 사용할 수 있도록 구조적으로 변환됩니다.

2. 문서 요소 추출
문서에서 다양한 요소를 추출하여 AbstractElement 형식으로 반환합니다.

예를 들어, ParagraphElement, TableElement, RowElement, CellElement 등의 클래스를 사용하여 문서 내 문단 및 테이블을 구조적으로 추출할 수 있습니다.

3. JSON 형식으로 변환
추출된 데이터를 JSON 형식으로 변환하여 웹 애플리케이션에서 활용할 수 있도록 합니다.

JSON 형식으로 변환된 데이터는 웹에서 쉽게 활용할 수 있으며, 다른 시스템과의 연동을 용이하게 합니다.

4. 진행 상황 모니터링
IProgressMonitor를 사용하여 파싱 작업의 진행 상황을 모니터링할 수 있습니다.

긴 작업을 진행할 때 사용자는 작업 진행률을 확인할 수 있습니다.

설치 방법
1. GitHub에서 프로젝트 클론하기
bash
복사
git clone https://github.com/0625yt/hwpParsingForWeb.git
2. 필요한 라이브러리 설치
이 프로젝트는 kr.dogfoot.hwplib 라이브러리를 사용하므로, Maven이나 Gradle을 통해 이 라이브러리를 포함해야 합니다. pom.xml 또는 build.gradle 파일에 다음 의존성을 추가합니다.

xml
복사
<dependency>
    <groupId>kr.dogfoot</groupId>
    <artifactId>hwplib</artifactId>
    <version>1.0.1</version>
</dependency>
3. Java IDE에서 프로젝트 열기
IntelliJ IDEA나 Eclipse와 같은 Java IDE에서 프로젝트를 열고, 필요한 라이브러리를 설정합니다.

사용법
1. HWP 파일 파싱 예제
java
복사
import com.parse.document.FileDataExtractorHwp;

public class HwpParsingExample {
    public static void main(String[] args) {
        // HWP 파일 경로 지정
        String hwpFilePath = "example.hwp";
        
        // HWP 데이터 추출기 생성
        FileDataExtractorHwp extractor = new FileDataExtractorHwp();
        
        // 데이터 추출
        extractor.extractData(hwpFilePath);
        
        // 추출된 데이터를 처리하는 로직 추가
        // 예: 추출된 데이터를 JSON 형식으로 변환 후 출력
    }
}
2. 추출된 데이터를 JSON 형식으로 변환
추출된 데이터를 JSONObject 형태로 변환하여, 웹 애플리케이션에서 쉽게 사용할 수 있도록 합니다.

java
복사
import org.json.simple.JSONObject;

public class DataToJsonExample {
    public static void main(String[] args) {
        // 데이터를 JSON 객체로 변환
        JSONObject dataJson = new JSONObject();
        dataJson.put("key", "value");
        
        // 변환된 JSON 출력
        System.out.println(dataJson.toJSONString());
    }
}
기여
이 프로젝트는 오픈소스로 제공됩니다. 프로젝트에 기여하고 싶으신 분들은 아래 방법에 따라 기여할 수 있습니다:

이 프로젝트를 Fork 합니다.

변경 사항을 로컬에서 적용한 후 Pull Request를 보내주세요.

라이센스
이 프로젝트는 MIT 라이센스를 따릅니다. 자세한 사항은 MIT 라이센스 페이지에서 확인할 수 있습니다.
