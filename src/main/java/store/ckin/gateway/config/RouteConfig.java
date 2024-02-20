package store.ckin.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.ckin.gateway.properties.ServerInfoProperties;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {
    private final ServerInfoProperties serverInfoProperties;
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("ckin-auth", r -> r.path("/auth/**")
                        .uri(serverInfoProperties.getAuthUri()))
                .route("ckin-coupon", r -> r.path("/coupon/**")
                        .uri(serverInfoProperties.getCouponUri()))
                .route("ckin-api", r -> r.path("/api/**")
                        .uri(serverInfoProperties.getApiUri()))
                .build();
    }
}
