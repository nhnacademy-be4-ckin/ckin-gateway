package store.ckin.gateway.util;

import com.auth0.jwt.JWT;

/**
 * JWT 관련 로직을 처리하는 클래스 입니다.
 *
 * @author : jinwoolee
 * @version : 2024. 03. 07.
 */
public class JwtUtil {
    public static final String AUTHORIZATION_SCHEME_BEARER = "Bearer ";

    public static final String REFRESH_TOKEN_SUBJECT = "ckin_refresh_token";

    public static String getUuid(String token) {
        return JWT.decode(token)
                .getClaim("uuid")
                .asString();
    }
}
