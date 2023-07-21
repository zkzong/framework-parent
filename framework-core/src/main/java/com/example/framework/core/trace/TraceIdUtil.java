package com.example.framework.core.trace;

import java.util.UUID;

/**
 * traceId生成工具类
 */
public class TraceIdUtil {
    public static String getTraceId() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
