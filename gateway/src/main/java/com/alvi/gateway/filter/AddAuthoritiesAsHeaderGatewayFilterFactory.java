package com.alvi.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
public class AddAuthoritiesAsHeaderGatewayFilterFactory
        extends AbstractGatewayFilterFactory<AddAuthoritiesAsHeaderGatewayFilterFactory.NameValueConfig> {

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
                    .map(SecurityContext::getAuthentication)
                    .flatMap(authentication -> {
                        if (nonNull(authentication) && authentication.isAuthenticated()) {

                            String authoritiesHeaderValue = authentication.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .collect(Collectors.joining(","));

                            if (isNotBlank(authoritiesHeaderValue)){
                                exchange.getRequest()
                                        .mutate()
                                        .headers(httpHeaders -> httpHeaders.add(config.name(), authoritiesHeaderValue));
                                log.info("Added Authorities= {} as a Header= {} for request to uri= {}",
                                        authoritiesHeaderValue, config.name(), exchange.getRequest().getURI());
                            }
                        }

                        return chain.filter(exchange);
                    });
    }

    public record NameValueConfig(String name) {}
}
