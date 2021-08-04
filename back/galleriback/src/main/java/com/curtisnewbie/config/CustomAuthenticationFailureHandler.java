package com.curtisnewbie.config;

import com.curtisnewbie.module.auth.config.AuthenticationFailureHandlerExtender;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author yongjie.zhuang
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandlerExtender {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.sendRedirect("/login");
    }
}
