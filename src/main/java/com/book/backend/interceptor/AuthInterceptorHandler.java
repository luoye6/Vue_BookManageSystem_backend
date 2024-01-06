package com.book.backend.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.book.backend.constant.Constant;
import com.book.backend.common.JwtProperties;
import com.book.backend.utils.JwtKit;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author 程序员小白条
 */
public class AuthInterceptorHandler implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtKit jwtKit;

    /**
     * 前置拦截器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        HandlerMethod handlerMethod=(HandlerMethod)handler;
        //判断如果请求的类是swagger的控制器，直接通行。
        if(("springfox.documentation.swagger.web.ApiResourceController").equals(handlerMethod.getBean().getClass().getName())){
            return  true;
        }

        if ((Constant.OPTIONS).equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        // 获取到JWT的Token
        String jwtToken = request.getHeader(jwtProperties.getTokenHeader());
        // 截取中间payload部分 +1是Bearer + 空格(1)
        String payloadToken = null;
        // 创建json对象
        JSONObject jsonObject = new JSONObject();

        if (jwtToken != null) {
            payloadToken = jwtToken.substring(jwtProperties.getTokenHead().length() + 1);
        }
        if (payloadToken != null && (!(Constant.NULL).equals(payloadToken))) {
            // 解析Token，获取Claims = Map
            Claims claims = null;
            try {
                claims = jwtKit.parseJwtToken(payloadToken);
            } catch (Exception e) {
                //token过期会捕捉到异常
                jsonObject.put("status", 401);
                jsonObject.put("msg", "登录过期,请重新登录");
                String json1 = jsonObject.toJSONString();
                renderJson(response, json1);
            }
            return claims != null;
            // 获取payload中的报文，
        }
        // 如果token不存在
        jsonObject.put("status", 401);
        jsonObject.put("msg", "登录非法");
        String json2 = jsonObject.toJSONString();
        renderJson(response, json2);

        return false;
    }

    private void renderJson(HttpServletResponse response, String json) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.print(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
