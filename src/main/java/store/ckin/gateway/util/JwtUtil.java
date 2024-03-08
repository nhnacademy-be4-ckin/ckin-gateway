package store.ckin.gateway.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 관련 로직을 처리하는 클래스 입니다.
 *
 * @author : jinwoolee
 * @version : 2024. 03. 07.
 */
@Slf4j
public class JwtUtil {
    private static final String SECRET_KEY = "ckin";

    public static final String AUTHORIZATION_SCHEME_BEARER = "Bearer ";

    /**
     * 토큰으로부터 UUID 를 추출하는 메서드 입니다.
     *
     * @param token JWT
     * @return UUID
     */
    public static String getUuid(String token) {
        return JWT.decode(token)
                .getClaim("uuid")
                .asString();
    }

    /**
     * 토큰 유효성을 검증하는 메서드 입니다.
     *
     * @param token Token
     * @return 유효성 검증 여부
     */
    public static boolean isValidate(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(SECRET_KEY)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            log.error("{} :  Token Validation failed", ex.getClass().getName());
            return false;
        }
    }
}
