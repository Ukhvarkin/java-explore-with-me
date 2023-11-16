package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private final RestTemplate rest;

    public StatsClient(@Value("${server.url}") String baseUrl, RestTemplateBuilder templateBuilder) {
        this.rest = templateBuilder
            .uriTemplateHandler(new DefaultUriBuilderFactory(baseUrl))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build();
    }

    public void create(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
            "ewm-main-service",
            request.getRequestURI(),
            request.getRemoteAddr(),
            LocalDateTime.now()
        );
        rest.postForEntity("/hit", new HttpEntity<>(endpointHitDto), EndpointHitDto.class);
        log.info("client: POST запрос /hit: {}", endpointHitDto);
    }

    public ResponseEntity<List<ViewStatDto>> get(String start, String end, String[] uris, boolean unique) {
        log.info("client: GET запрос /stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
            HttpMethod.GET, getHttpEntity(), new ParameterizedTypeReference<>() {
            }, start, end, uris, unique);
    }

    private <T> HttpEntity<T> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }
}