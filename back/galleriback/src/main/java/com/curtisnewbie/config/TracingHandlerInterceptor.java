package com.curtisnewbie.config;

import com.curtisnewbie.module.tracing.common.TracingConstants;
import com.curtisnewbie.module.tracing.filter.TracingHandlerInterceptorBase;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * HandlerInterceptor for setting trace_id
 *
 * @author yongjie.zhuang
 */
public class TracingHandlerInterceptor extends TracingHandlerInterceptorBase implements HandlerInterceptor {

    public TracingHandlerInterceptor() {
        super(TracingConstants.PRINCIPAL_TRACE_ID_FIELD);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Principal principal = request.getUserPrincipal();
        return doPreHandle(principal);
    }
}
