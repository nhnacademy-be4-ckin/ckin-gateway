package store.ckin.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import store.ckin.gateway.util.JwtUtil;

import java.util.Objects;

/**
 * Gateway 에 오는 요청들을 처리하는 Filter 클래스 입니다.
 *
 * @author : jinwoolee
 * @version : 2024. 03. 07.
 */
@Slf4j
@Component
public class CustomGatewayFilter extends AbstractGatewayFilterFactory<CustomGatewayFilter.Config> {
    public static final String HEADER_AUTHORIZATION = "Authorization";

    public CustomGatewayFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (request.getHeaders()
                    .containsKey(HEADER_AUTHORIZATION)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            String accessToken = Objects.requireNonNull(
                    request.getHeaders()
                            .getFirst(HEADER_AUTHORIZATION))
                    .replace(JwtUtil.AUTHORIZATION_SCHEME_BEARER, "");

            // TODO: accessToken 이 인증되면 다음 필터로 아니면 401

            return chain.filter(exchange);
        });
    }

    public static class Config {
        // TODO: 필요한 설정들 추가
    }
}
