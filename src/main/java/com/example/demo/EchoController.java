package com.example.demo;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
public class EchoController {

    private final RestTemplate restTemplate;

    public EchoController(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.rootUri("https://httpstat.us").build();
    }

    @GetMapping("/api/echo/{httpCode}")
    public ResponseEntity<String> echo(@PathVariable int httpCode) {
        try {
            ResponseEntity<Void> responseEntity = restTemplate.getForEntity("/{httpCode}", Void.class, httpCode);
            return buildResponse(responseEntity.getStatusCode());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return buildResponse(e.getStatusCode());
        }
    }

    private ResponseEntity<String> buildResponse(HttpStatus httpStatus) {
        return new ResponseEntity<>("HTTP Status " + httpStatus, httpStatus);
    }
}
