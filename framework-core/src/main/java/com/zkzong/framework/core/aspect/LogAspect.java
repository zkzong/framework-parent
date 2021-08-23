package com.zkzong.framework.core.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @Author: zong
 * @Date: 2021/8/2
 */
//@Aspect
//@Component
@Slf4j
public class LogAspect {

    @Autowired
    private Environment environment;

    /**
     * 使用annotation方式
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void log() {
    }

    /**
     * 使用execution方式
     */
    //@Pointcut("execution(public * com..*.controller.*.*(..)))")
    //public void log() {
    //}
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) throws UnknownHostException {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        //log.info("URL : " + request.getRequestURL().toString());
        //log.info("URI : " + request.getRequestURI());
        //log.info("HTTP_METHOD : " + request.getMethod());
        //log.info("IP : " + request.getRemoteAddr());
        //log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));

        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
        final String port = environment.getProperty("server.port");
        log.info("SOURCE IP : " + request.getRemoteAddr() + " | TARGET IP : " + hostAddress + ":" + port + " | URI : " + request.getRequestURI() + " | " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + " | ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "log()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        log.info("RESPONSE : " + ret);
    }

}
