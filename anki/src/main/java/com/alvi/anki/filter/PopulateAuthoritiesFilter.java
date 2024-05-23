package com.alvi.anki.filter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Expects that Authentication step was skipped and SecurityContext wasn't created
 * because of permitAll() security configuration.
 */
public class PopulateAuthoritiesFilter implements WebFilter {

    private static final String AUTHORITIES_HEADER_NAME = "Authorities";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        List<String> authorities = exchange.getRequest().getHeaders().get(AUTHORITIES_HEADER_NAME);

        if (isNotEmpty(authorities)) {
            SecurityContextImpl securityContext = new SecurityContextImpl();
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    null,
                    null,
                    authorities.stream()
                            .flatMap(it -> Stream.of(it.split(",")).map(String::trim))
                            .map(SimpleGrantedAuthority::new)
                            .collect(toList())
            );
            securityContext.setAuthentication(auth);

            // Populate Security Context the same way as AuthenticationWebFilter :
            return NoOpServerSecurityContextRepository.getInstance().save(exchange, securityContext)
                    .then(chain.filter(exchange))
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
        }

        return chain.filter(exchange);
    }
}
