package com.stockkingdom.common.util;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;

import java.util.Arrays;
import java.util.Locale;

public class P6SpyCustomFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        // 1. STATEMENT 카테고리인 경우 하이버네이트 포맷터로 SQL 줄바꿈 처리
        if (Category.STATEMENT.getName().equals(category)) {
            String trimmedSql = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSql.startsWith("create") || trimmedSql.startsWith("alter") || trimmedSql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
        }

        // 2. 패키지명을 제외한 간결한 호출 위치 추출
        String callStackInfo = getCallStackInfo();

        // 3. 확실한 여러 줄 출력을 위해 시스템 라인 세퍼레이터 사용
        String n = System.lineSeparator();
        return n + "[JPA Query Triggered From]: " + callStackInfo +
                n + "[Execution Time]: " + elapsed + " ms" +
                n + "[SQL]:" + n + sql +
                n + "------------------------------------------------------------";
    }

    private String getCallStackInfo() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                // ⚠️ 본인의 실제 루트 패키지명(예: com.ktds)으로 변경하세요!
                .filter(element -> element.getClassName().startsWith("jpa.conversion.jpamgr")
                        && !element.getClassName().contains("P6Spy")
                        && !element.getClassName().contains("Proxy"))
                .findFirst()
                .map(element -> {
                    // com.ktds.aicc.service.UserService -> UserService 로 축소
                    String fullClassName = element.getClassName();
                    String simpleClassName = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);

                    // 축소된 클래스명.메서드명(파일명:라인수) 형태로 가공
                    return String.format("%s.%s(%s:%d)",
                            simpleClassName,
                            element.getMethodName(),
                            element.getFileName(),
                            element.getLineNumber());
                })
                .orElse("Unknown Source");
    }
}