
# HwpParsingForWeb

`HwpParsingForWeb` 프로젝트는 한글(HWP) 문서를 파싱하고, 문서 내 데이터를 구조적으로 추출하여 웹 애플리케이션에서 활용할 수 있도록 변환하는 Java 기반의 도구입니다. 이 프로젝트는 HWP 파일을 읽고, 문서의 다양한 요소들(문단, 테이블, 섹션 등)을 파싱하여 JSON 형식으로 변환하거나 다른 형식으로 데이터를 처리할 수 있게 도와줍니다.

---

## 프로젝트 목표

HWP 파일은 한국에서 널리 사용되는 문서 형식으로, 웹 기반 애플리케이션에서 이를 처리하고 데이터를 활용하는 데 어려움이 있을 수 있습니다. **HwpParsingForWeb** 프로젝트는 HWP 파일을 웹에서 쉽게 사용할 수 있는 데이터 형식으로 변환하는 것을 목표로 합니다.

- **문서 구조 분석**: HWP 파일을 읽고 섹션, 문단, 테이블 등을 구조적으로 파싱.
- **데이터 추출 및 변환**: 파싱된 데이터를 JSON 형식으로 변환하여 웹 애플리케이션에서 활용.
- **웹 통합**: 추출된 데이터를 다른 시스템과 연동할 수 있도록 JSON, XML 등의 형식으로 출력.

---

## 기술 스택

- **Java**: 문서 파싱과 데이터 처리의 핵심 언어.
- **HwpLib (kr.dogfoot.hwplib)**: HWP 파일을 파싱하는 Java 라이브러리.
- **JSON.simple**: JSON 형식으로 데이터를 변환.
- **Eclipse / IntelliJ IDEA**: Java IDE에서 프로젝트 설정 및 실행.

---

## 프로젝트 구조

### 주요 클래스

1. **`DataExtractContext.java`**  
   문서 파싱 중 데이터를 관리하는 컨텍스트 클래스입니다. `Map<String, Object>`를 사용하여 데이터를 저장하고, 후속 작업에서 쉽게 접근할 수 있도록 합니다.

2. **`DocumentExtractorHwp.java`**  
   HWP 파일에서 문단, 테이블, 섹션 등의 요소를 추출하는 핵심 클래스입니다. 추출된 데이터를 `AbstractElement`로 변환하여 후속 처리할 수 있도록 합니다.

3. **`FileDataExtractorHwp.java`**  
   HWP 파일을 실제로 읽고, 이를 분석하여 데이터로 변환하는 클래스입니다. HWP 파일을 `FileInputStream`으로 열고 데이터를 추출합니다.

4. **`common` 폴더**  
   다양한 유틸리티 클래스와 공통 기능을 제공하는 파일들로 구성되어 있습니다. `HwpUtil`, `Util`, `Const` 등 데이터 처리와 관련된 여러 유틸리티 메소드들이 포함됩니다.

---

## 주요 기능

### 1. **HWP 파일 파싱**

HWP 문서에서 다양한 요소(문단, 테이블 등)를 추출하고 이를 웹 애플리케이션에서 사용할 수 있는 데이터 형식으로 변환합니다.

### 2. **문서 요소 추출**

HWP 파일에서 섹션, 문단, 테이블 등을 추출하여 구조적으로 변환합니다. `AbstractElement` 객체로 변환하여 후속 처리가 가능하게 만듭니다.

### 3. **JSON 형식으로 변환**

문서에서 추출된 데이터를 JSON 형식으로 변환하여 웹 애플리케이션에서 쉽게 활용할 수 있도록 제공합니다.

### 4. **진행 상황 모니터링**

`IProgressMonitor`를 사용하여 데이터 추출 작업의 진행 상황을 실시간으로 모니터링할 수 있습니다.

---

## 설치 방법

### 1. GitHub에서 프로젝트 클론하기

```bash
git clone https://github.com/0625yt/hwpParsingForWeb.git
```

### 2. 필요한 라이브러리 추가

프로젝트의 의존성 라이브러리를 설정해야 합니다. `pom.xml` 또는 `build.gradle`에 아래와 같은 의존성을 추가합니다.

```xml
<dependency>
    <groupId>kr.dogfoot</groupId>
    <artifactId>hwplib</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 3. IDE에서 프로젝트 열기

IntelliJ IDEA 또는 Eclipse에서 프로젝트를 열고, 필요한 라이브러리를 설정합니다.

---

## 사용법

### 1. HWP 파일 파싱 예제

```java
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
```

### 2. 추출된 데이터를 JSON 형식으로 변환

```java
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
```

---

## 기여 방법

이 프로젝트는 오픈소스로 제공되며, 여러분의 기여를 환영합니다! 기여 방법은 아래와 같습니다:

1. 프로젝트를 **Fork** 합니다.
2. 로컬에서 변경 사항을 적용하고, **Pull Request**를 보냅니다.

---

## 라이센스

이 프로젝트는 **MIT 라이센스** 하에 배포됩니다. 자세한 사항은 [MIT 라이센스 페이지](https://opensource.org/licenses/MIT)에서 확인할 수 있습니다.

---

### 프로젝트 포트폴리오로 사용하기

이 프로젝트는 Java 기반의 파일 파싱과 데이터 변환 기술을 보여주는 훌륭한 예시입니다. 이 프로젝트를 포트폴리오에 포함시키면, HWP 파일을 다루는 기술적 역량을 강조할 수 있습니다. 또한, 웹 애플리케이션에서의 데이터 활용 방식에 대해 깊은 이해를 보여줄 수 있습니다.
