package com.stockkingdom.config;

import com.p6spy.engine.spy.P6SpyOptions;
import com.stockkingdom.common.util.P6SpyCustomFormatter;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy(false)
public class P6SpyConfig {

    @PostConstruct
    public void setLogMessageFormat() {
        // 전역 p6spy 옵션에 방금 만든 커스텀 포맷터 클래스 명을 등록
        P6SpyOptions.getActiveInstance().setLogMessageFormat(P6SpyCustomFormatter.class.getName());
    }
}