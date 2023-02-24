package com.book.backend.utils;


import com.book.backend.common.JwtProperties;
import com.book.backend.pojo.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * JWT工具类
 *
 * @author xiaobaitiao
 *
 *
 */
@Component
public class JwtKit {
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 生成Token
     *
     * @param  user 自定义要存储的用户对象信息
     * @return string(Token)
     */
    public  <T> String generateToken(T user) {
        Map<String, Object> claims = new HashMap<String, Object>(10);
        claims.put("username", user.toString());
        claims.put("createdate", new Date());
        claims.put("id", System.currentTimeMillis());
        // 要存储的数据
        return Jwts.builder().addClaims(claims)
                // 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                // 加密算法和密钥
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact(); // 打包返回 3部分
    }

    public JwtKit() {
    }

    public JwtKit(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 校验Token是否合法
     *
     * @param token 要校验的Token
     * @return Claims (过期时间，用户信息，创建时间)
     */
    public  Claims parseJwtToken(String token) {
        Claims claims = null;
        // 根据哪个密钥解密
        claims = Jwts.parser().setSigningKey(jwtProperties.getSecret())
                // 设置要解析的Token
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}
