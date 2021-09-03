package com.zkzong.framework.core.annotation.impl;

import com.zkzong.framework.core.annotation.ApiVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Slf4j
public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    //private static final Map<HandlerMethod, RequestMappingInfo> HANDLER_METHOD_REQUEST_MAPPING_INFO_MAP = Maps.newHashMap();

    /* _________________________________
     *
     * 解决 PathVariable 效率低问题
     * _________________________________
     */
    //@Override
    //protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
    //    HandlerMethod handlerMethod = super.createHandlerMethod(handler, method);
    //    HANDLER_METHOD_REQUEST_MAPPING_INFO_MAP.put(handlerMethod, mapping);
    //    super.registerHandlerMethod(handler, method, mapping);
    //}

    //@Override
    //protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
    //    String mappingName = request.getHeader("x-mapping-name");
    //    if (StringUtils.isBlank(mappingName)) {
    //        return super.lookupHandlerMethod(lookupPath, request);
    //    }
    //
    //    log.debug("启用自定义 mapping > {}", mappingName);
    //    List<HandlerMethod> handlerMethods = super.getHandlerMethodsForMappingName(mappingName);
    //    if (CollectionUtils.isEmpty(handlerMethods)) {
    //        throw new RuntimeException();
    //    }
    //    if (handlerMethods.size() > 1) {
    //        throw new RuntimeException();
    //    }
    //
    //    HandlerMethod handlerMethod = handlerMethods.get(0);
    //    // 根据处理方法查找RequestMappingInfo, 用于解析路径url中的参数
    //    RequestMappingInfo requestMappingInfo = HANDLER_METHOD_REQUEST_MAPPING_INFO_MAP.get(handlerMethod);
    //    if (requestMappingInfo == null) {
    //        throw new RuntimeException();
    //    }
    //    super.handleMatch(requestMappingInfo, lookupPath, request);
    //    return handlerMethod;
    //}


    /* _________________________________
     *
     * 加入版本号
     * _________________________________
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
        if (info == null) {
            return null;
        }

        return createApiVersionInfo(method, handlerType, info);
    }

    private RequestMappingInfo createApiVersionInfo(Method method, Class<?> handlerType, RequestMappingInfo info) {
        ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);
        if (methodAnnotation != null) {
            RequestCondition<?> methodCondition = getCustomMethodCondition(method);

            info = createApiVersionInfo(methodAnnotation, methodCondition).combine(info);
        } else {
            ApiVersion typeAnnotation = AnnotationUtils.findAnnotation(handlerType, ApiVersion.class);
            if (typeAnnotation != null) {
                RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);

                info = createApiVersionInfo(typeAnnotation, typeCondition).combine(info);
            }
        }
        log.info("get mapping {}", info);
        return info;
    }

    private RequestMappingInfo createApiVersionInfo(ApiVersion annotation, RequestCondition<?> customCondition) {
        int[] values = annotation.value();
        String[] patterns = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            patterns[i] = "v" + values[i];
        }

        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(), useSuffixPatternMatch(),
                        useTrailingSlashMatch(), getFileExtensions()),
                new RequestMethodsRequestCondition(), new ParamsRequestCondition(),
                new HeadersRequestCondition(), new ConsumesRequestCondition(),
                new ProducesRequestCondition(), customCondition);
    }

}
