package store.ckin.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import store.ckin.gateway.util.JwtUtil;

import java.util.Map;
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

    @RequiredArgsConstructor
    public static class Config {
        private final RedisTemplate<String, Object> redisTemplate;
    }

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
                return rejectByUnauthorized(response);
            }

            String accessToken = Objects.requireNonNull(
                    request.getHeaders()
                            .getFirst(HEADER_AUTHORIZATION))
                    .replace(JwtUtil.AUTHORIZATION_SCHEME_BEARER, "");

            if (!JwtUtil.isValidate(accessToken)) {
                return rejectByUnauthorized(response);
            }

            String uuid = JwtUtil.getUuid(accessToken);
            String memberId = getMemberId(config, uuid);

            Map<String, String> pathVariables =
                    exchange.getAttribute(ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

            if(Objects.isNull(pathVariables)) {
                return rejectByUnauthorized(response);
            }

            String memberIdVariable = pathVariables.get("memberId");

            if (memberIdVariable.isEmpty()) {
                return rejectByUnauthorized(response);
            }

            if(!memberId.equals(memberIdVariable)) {
                return rejectByUnauthorized(response);
            }

            return chain.filter(exchange);
        });
    }

    private static Mono<Void> rejectByUnauthorized(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.setComplete();
    }

    private String getMemberId(Config config, String uuid) {
        return (String) Objects.requireNonNull(
                config.redisTemplate
                        .opsForHash()
                        .get(uuid, "id"));
    }
}
