package com.acervo.api.client;

import com.acervo.api.dto.RegionalApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegionalApiClient {

    private final RestClient regionalApiRestClient;

    public List<RegionalApiResponseDTO> fetchRegionais() {
        // log.info("regionais da API externa");

        try {
            List<RegionalApiResponseDTO> regionais = regionalApiRestClient
                    .get()
                    .uri("/v1/regionais")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<RegionalApiResponseDTO>>() {
                    });

            // log.info("sucesso {} regionais", regionais != null ? regionais.size() : 0);
            return regionais != null ? regionais : List.of();

        } catch (Exception e) {
            // log.error("Error fetching regionais from external API", e);
            throw new RuntimeException("Falha ao buscar regionais: " + e.getMessage(), e);
        }
    }
}
