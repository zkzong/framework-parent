package io.code.framework.core.traceid.interceptor;

import io.code.framework.core.traceid.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(Constants.TRACE_ID, MDC.get(Constants.TRACE_ID));
    }

}
