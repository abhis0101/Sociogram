package com.sociogram.client;

import com.sociogram.dto.EndpointHitDto;
import com.sociogram.dto.ViewStatsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ViewStatsClient {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${stats-server.url}")
    private String absoluteUrl;
    private final WebClient webClient = WebClient.builder()
            .baseUrl(absoluteUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();


    public void addHit(EndpointHitDto endpointHitDto) {
        System.out.println(absoluteUrl);
        restTemplate.postForLocation(absoluteUrl + "/hit", endpointHitDto);
    }


    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        StringBuilder urisToSend = new StringBuilder();
        for (String uri : uris) {
            urisToSend.append(uri).append(",");
        }
        ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                absoluteUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                },
                start, end, urisToSend.toString(), unique);

        return response.getBody();
    }
}