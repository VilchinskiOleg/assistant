package com.alvi.anki.rest.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/anki")
public class TestRest {

    @GetMapping("/test")
    @PreAuthorize("hasRole('TEST')")
    public Mono<String> getAllItems() {
        return Mono.just("OK");
    }
}
