package com.example.interceptor;

import com.example.context.BaseContext;
import com.example.util.JwtClaimsConstant;
import com.example.util.JwtProperties;
import com.example.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        log.info("请求校验:{}", jwtProperties.getUserTokenName());
        //1、从请求头中获取令牌
        String authorizationHeader = request.getHeader(jwtProperties.getUserTokenName());

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 截取掉 "Bearer " 前缀
            String jwtToken = authorizationHeader.substring("Bearer ".length()).trim();

            //2、校验令牌
            try {
                Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), jwtToken);
                String userId = claims.get(JwtClaimsConstant.USER_ID).toString();
                log.info("当前用户id：{}", userId);
                BaseContext.setCurrentId(userId);
                return true;
            } catch (MalformedJwtException ex) {
                log.error("JWT格式错误", ex);
                response.setStatus(400); // 对于格式错误，通常返回400而不是401
                return false;
            } catch (Exception ex) {
                log.error("JWT解析时发生未知错误", ex);
                response.setStatus(500); // 对于未知错误，通常返回500
                return false;
            }
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
