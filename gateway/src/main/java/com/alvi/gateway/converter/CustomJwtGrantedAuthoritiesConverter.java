package com.alvi.gateway.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String CLAIM_NAME = "scopes";
    private static final String AUTHORITY_ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<String> scopes = jwt.getClaimAsStringList(CLAIM_NAME);

        if (scopes != null && !scopes.isEmpty()) {
            Set<GrantedAuthority> authorities = new HashSet<>();
            for (String scope : scopes) {
                authorities.add(new SimpleGrantedAuthority(AUTHORITY_ROLE_PREFIX + scope));
            }
            return authorities;
        }

        return Collections.emptyList();
    }
}
