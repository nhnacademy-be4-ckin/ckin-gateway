package store.ckin.gateway.config;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import store.ckin.gateway.filter.CustomGatewayFilter;
import store.ckin.gateway.properties.ServerInfoProperties;

/**
 * Gateway 설정을 하는 클래스 입니다.
 *
 * @author : jinwoolee
 * @version : 2024. 03. 07.
 */
@Configuration
@RequiredArgsConstructor
public class RouteConfig {
    private final ServerInfoProperties serverInfoProperties;

    private final CustomGatewayFilter customGatewayFilter;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Custom route locator route locator.
     *
     * @param routeLocatorBuilder RouteLocatorBuilder
     * @return RouteLocator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("ckin-auth", r -> r.path("/auth/**")
                        .uri(serverInfoProperties.getAuthUri()))
                .route("ckin-coupon", r -> r.path("/coupon/**")
                        .filters(authFilter())
                        .uri(serverInfoProperties.getCouponUri()))
                .route("ckin-api", r -> r.path("/api/**")
                        .uri(serverInfoProperties.getApiUri()))
                .route("ckin-api", r -> r.path("/api/member/**")
                        .filters(authFilter())
                        .uri(serverInfoProperties.getApiUri()))
                .route("ckin-api", r -> r.path("/api/admin/**")
                        .filters(authFilter())
                        .uri(serverInfoProperties.getApiUri()))
                .build();
    }

    private Function<GatewayFilterSpec, UriSpec> authFilter() {
        return function -> function.filter(
                customGatewayFilter.apply(new CustomGatewayFilter.Config(redisTemplate))
        );
    }
}
