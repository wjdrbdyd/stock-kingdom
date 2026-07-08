# Getting Started
rm -rf out
rm -rf build/generated
rm -rf src/main/generated

- 큐파일 재생성
- ./gradlew clean compileJava

  - 타임리프 일괄변경 
      - <script src="(.*?)"></script>
        -> <script th:src="@{$1}"></script>
    
      - <c:out\s+value=["'](.*?)["']\s*/?>
        -> [[$1]]
      - <c:if\s+test=['"]\$\{(.*?)\}['"]\s*>
        -> <th:block th:if="\${$1}">
      - </th:block> -> </th:block>

a태그 변경 
onclick="location\.href='([^']+)'"
th:href="@{$1}"