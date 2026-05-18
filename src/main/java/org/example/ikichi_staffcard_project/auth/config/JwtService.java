package org.example.ikichi_staffcard_project.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret; // 共通鍵

    @Value("${jwt.expiration:86400000}")
    private long expiration;

//    鍵をbase64デコード（複合する）
    private SecretKey secretKey(){
        byte[] decodeKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String generateToken(String staffId, String role){
        return Jwts.builder()
                .subject(staffId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey(), Jwts.SIG.HS256)
                .compact();
    }

//    JWTトークンからユーザー名を取得
    public String getUsernameFromToken(String token){
        return getClaimsFromToken(token).getSubject();
    }

//    JWTトークンから有効期限を取得
    public Date getExpirationDateFromToken(String token){
        return getClaimsFromToken(token).getExpiration();
    }

//    JWTトークンをパースしてクレームを取得
    private Claims getClaimsFromToken(String token){
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    トークンの有効期限のチェック
    public Boolean isTokenExpired(String token){
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

//    トークンの検証
    public Boolean validateToken(String token, String username){
        String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
}
