package org.example.restaurantmanagement.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtils {

    // 至少32个字符
    private static final String SECRET =
            "restaurant-management-secret-key-2026";

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    // 2小时
    private static final long EXPIRE = 1000 * 60 * 60 * 2;

    /**
     * 生成Token
     */
    public static String createToken(Long id,
                                     String username,
                                     String role) {

        return Jwts.builder()
                .claim("id", id)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Token是否合法
     */
    public static boolean verify(String token) {

        try {

            parseToken(token);

            return true;

        } catch (Exception e) {

            return false;

        }
    }

}