package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient {

    private final String serverUrl;

    @Autowired
    public StatsClient(@Value("${ewm-stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
        this.serverUrl = serverUrl;
    }

    public ResponseEntity<List<ViewStatsDto>> getStats(String start, String end, List<String> uris, Boolean unique) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );
        HttpEntity<ViewStatsDto> requestEntity = new HttpEntity<>(null, defaultHeaders());
        String path = serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";


        ResponseEntity<List<ViewStatsDto>> result = restTemplate.exchange(path, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<>() {
                }, parameters);
        if (result.getStatusCode().is2xxSuccessful()) {
            return result;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(result.getStatusCode());

        if (result.hasBody()) {
            return responseBuilder.body(result.getBody());
        }
        return responseBuilder.build();
    }

    public ResponseEntity<Object> addNewEndpointHit(EndpointHitDto endpointHit) {
        return post("/hit", null, endpointHit);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
