package com.acervo.api.service;

import com.acervo.api.client.RegionalApiClient;
import com.acervo.api.domain.Regional;
import com.acervo.api.dto.RegionalApiResponseDTO;
import com.acervo.api.dto.SyncResultDTO;
import com.acervo.api.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalSyncService {

    private final RegionalApiClient regionalApiClient;
    private final RegionalRepository regionalRepository;

    @Transactional
    public SyncResultDTO syncRegionais() {
        // log.info("regional sync");

        int inserted = 0;
        int updated = 0;
        int inactivated = 0;

        try {
            // 1. Busca os dados na API
            List<RegionalApiResponseDTO> externalRegionais = regionalApiClient.fetchRegionais();

            if (externalRegionais.isEmpty()) {
                // log.info("Se API retornar vazia inativa tudo. -{} registros", count);
                int count = regionalRepository.inactivateNotInExternalIds(List.of(-1L)); // Trick: use impossible ID
                return new SyncResultDTO(0, 0, count,
                        "SYNC retornou vazio, todos os registros foram inativados");
            }

            // 2. carrega local em map
            Map<Long, Regional> localRegionaisMap = regionalRepository.findAllByAtivoTrue()
                    .stream()
                    .filter(r -> r.getExternalId() != null)
                    .collect(Collectors.toMap(Regional::getExternalId, r -> r));

            // 3. each para cada registro da api
            for (RegionalApiResponseDTO externalRegional : externalRegionais) {
                Regional localRegional = localRegionaisMap.get(externalRegional.id());

                if (localRegional == null) {
                    // INSERT (se não existir)
                    Regional newRegional = Regional.builder()
                            .externalId(externalRegional.id())
                            .nome(externalRegional.nome())
                            .ativo(true)
                            .build();
                    regionalRepository.save(newRegional);
                    inserted++;

                } else if (!localRegional.getNome().equals(externalRegional.nome())) {
                    // UPDATE: existe mas c outro nome
                    // desativa antigo
                    regionalRepository.inactivateByExternalId(externalRegional.id());

                    // Cria com novo nome
                    Regional updatedRegional = Regional.builder()
                            .externalId(externalRegional.id())
                            .nome(externalRegional.nome())
                            .ativo(true)
                            .build();
                    regionalRepository.save(updatedRegional);
                    updated++;
                }
                // se existir e não mudar nada, não faz nada
            }

            // 4. Inativa registros sem referência (registros locais não na lista externa)
            List<Long> externalIds = externalRegionais.stream()
                    .map(RegionalApiResponseDTO::id)
                    .toList();

            int orphanedCount = regionalRepository.inactivateNotInExternalIds(externalIds);
            inactivated += orphanedCount;

            String message = String.format("SYNC REGIONAIS: %d inseridos, %d atualizados, %d inativados",
                    inserted, updated, inactivated);

            return new SyncResultDTO(inserted, updated, inactivated, message);

        } catch (Exception e) {
            throw new RuntimeException("Falha no sync: " + e.getMessage(), e);
        }
    }
}
