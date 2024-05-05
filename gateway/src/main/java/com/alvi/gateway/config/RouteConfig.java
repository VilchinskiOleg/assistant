package com.alvi.gateway.config;

import com.alvi.gateway.filter.AddAuthoritiesAsHeaderGatewayFilterFactory;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.factory.AddRequestHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private static final String ANKI_PATH_PATTERN = "/api/anki/**";
    private static final String AUTHORITIES_HEADER_NAME = "Authorities";

    @Resource
    private AddAuthoritiesAsHeaderGatewayFilterFactory filterFactory;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("firstServiceId", predicateSpec -> predicateSpec
                        .path(ANKI_PATH_PATTERN)
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(
                                filterFactory.apply(new AddAuthoritiesAsHeaderGatewayFilterFactory.NameValueConfig(AUTHORITIES_HEADER_NAME)))
                        )
                        .uri("http://localhost:8100"))
                .route("secondServiceId", predicateSpec -> predicateSpec
                        .path("web-crawler/api/**")
                        .uri("http://localhost:8200"))
                .build();
    }
}
