package ru.practicum.stats.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.stats.dto.ViewStatsRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StatsClient {
    private final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String url, RestTemplateBuilder builder) {
        this.rest = builder
            .uriTemplateHandler(new DefaultUriBuilderFactory(url))
            .requestFactory(HttpComponentsClientHttpRequestFactory::new)
            .build();
    }

    public EndpointHitDto create(HttpServletRequest request) {
        EndpointHitDto endpointHitDto = new EndpointHitDto(
            "ewm-main-service",
            request.getRequestURI(),
            request.getRemoteAddr(),
            LocalDateTime.now()
        );
        log.info("client: POST запрос /hit: {}", endpointHitDto);
        return rest.postForEntity("/hit", new HttpEntity<>(endpointHitDto), EndpointHitDto.class).getBody();
    }

    public List<ViewStatDto> get(ViewStatsRequest request) {
        ResponseEntity<List<ViewStatDto>> response =
            rest.exchange("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                }, request.getStart(), request.getEnd(), request.getUris(), request.isUnique()
            );
        return response.getBody();
    }

    private <T> HttpEntity<T> getHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }
}