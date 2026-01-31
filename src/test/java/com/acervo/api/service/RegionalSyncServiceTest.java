package com.acervo.api.service;

import com.acervo.api.client.RegionalApiClient;
import com.acervo.api.domain.Regional;
import com.acervo.api.dto.RegionalApiResponseDTO;
import com.acervo.api.dto.SyncResultDTO;
import com.acervo.api.repository.RegionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalSyncServiceTest {

        @Mock
        private RegionalApiClient regionalApiClient;

        @Mock
        private RegionalRepository regionalRepository;

        @InjectMocks
        private RegionalSyncService regionalSyncService;

        @Test
        void syncRegionais_WithNewRecords_ShouldInsertThem() {
                // Given - API retorna 2 regionais, banco vazio
                List<RegionalApiResponseDTO> apiData = List.of(
                                new RegionalApiResponseDTO(101L, "Cuiabá"),
                                new RegionalApiResponseDTO(102L, "Várzea Grande"));

                when(regionalApiClient.fetchRegionais()).thenReturn(apiData);
                when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of());
                when(regionalRepository.inactivateNotInExternalIds(any())).thenReturn(0);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(2);
                assertThat(result.updated()).isEqualTo(0);
                assertThat(result.inactivated()).isEqualTo(0);

                // Verifica que 2 regionais foram salvos
                ArgumentCaptor<Regional> regionalCaptor = ArgumentCaptor.forClass(Regional.class);
                verify(regionalRepository, times(2)).save(regionalCaptor.capture());

                List<Regional> savedRegionais = regionalCaptor.getAllValues();
                assertThat(savedRegionais).hasSize(2);
                assertThat(savedRegionais.get(0).getNome()).isEqualTo("Cuiabá");
                assertThat(savedRegionais.get(0).getExternalId()).isEqualTo(101L);
                assertThat(savedRegionais.get(0).getAtivo()).isTrue();
        }

        @Test
        void syncRegionais_WithModifiedName_ShouldInactivateOldAndCreateNew() {
                // Given - API retorna nome modificado
                List<RegionalApiResponseDTO> apiData = List.of(
                                new RegionalApiResponseDTO(101L, "Cuiabá - Centro") // Nome mudou
                );

                Regional existingRegional = Regional.builder()
                                .id(1L)
                                .externalId(101L)
                                .nome("Cuiabá") // Nome antigo
                                .ativo(true)
                                .build();

                when(regionalApiClient.fetchRegionais()).thenReturn(apiData);
                when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(existingRegional));
                when(regionalRepository.inactivateByExternalId(101L)).thenReturn(1);
                when(regionalRepository.inactivateNotInExternalIds(any())).thenReturn(0);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(0); // Nenhum registro novo
                assertThat(result.updated()).isEqualTo(1); // VG atualizado (nome mudou)
                assertThat(result.inactivated()).isEqualTo(0);

                // Verifica criação do novo
                ArgumentCaptor<Regional> regionalCaptor = ArgumentCaptor.forClass(Regional.class);
                verify(regionalRepository).save(regionalCaptor.capture());

                Regional newRegional = regionalCaptor.getValue();
                assertThat(newRegional.getNome()).isEqualTo("Cuiabá - Centro");
                assertThat(newRegional.getAtivo()).isTrue();
        }

        @Test
        void syncRegionais_WithSameName_ShouldNotUpdate() {
                // Given - API retorna mesmo nome
                List<RegionalApiResponseDTO> apiData = List.of(
                                new RegionalApiResponseDTO(101L, "Cuiabá"));

                Regional existingRegional = Regional.builder()
                                .id(1L)
                                .externalId(101L)
                                .nome("Cuiabá")
                                .ativo(true)
                                .build();

                when(regionalApiClient.fetchRegionais()).thenReturn(apiData);
                when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(existingRegional));
                when(regionalRepository.inactivateNotInExternalIds(any())).thenReturn(0);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(0);
                assertThat(result.updated()).isEqualTo(0);
                assertThat(result.inactivated()).isEqualTo(0);

                // Não deve salvar nada
                verify(regionalRepository, never()).save(any());
        }

        @Test
        void syncRegionais_WithRemovedRecords_ShouldInactivateThem() {
                // Given - API não retorna mais o ID 105
                List<RegionalApiResponseDTO> apiData = List.of(
                                new RegionalApiResponseDTO(101L, "Cuiabá"));

                Regional existingCuiaba = Regional.builder()
                                .id(1L)
                                .externalId(101L)
                                .nome("Cuiabá")
                                .ativo(true)
                                .build();

                Regional orphanedRegional = Regional.builder()
                                .id(2L)
                                .externalId(105L)
                                .nome("Regional Removida")
                                .ativo(true)
                                .build();

                when(regionalApiClient.fetchRegionais()).thenReturn(apiData);
                when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(existingCuiaba, orphanedRegional));
                when(regionalRepository.inactivateNotInExternalIds(List.of(101L))).thenReturn(1);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(0);
                assertThat(result.updated()).isEqualTo(0);
                assertThat(result.inactivated()).isEqualTo(1);

                verify(regionalRepository).inactivateNotInExternalIds(List.of(101L));
        }

        @Test
        void syncRegionais_WithEmptyApiResponse_ShouldInactivateAll() {
                // Given - API retorna lista vazia
                when(regionalApiClient.fetchRegionais()).thenReturn(List.of());
                when(regionalRepository.inactivateNotInExternalIds(List.of(-1L))).thenReturn(2);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(0);
                assertThat(result.updated()).isEqualTo(0);
                assertThat(result.inactivated()).isEqualTo(2);

                verify(regionalRepository).inactivateNotInExternalIds(List.of(-1L));
        }

        @Test
        void syncRegionais_WithMixedChanges_ShouldHandleCorrectly() {
                // Given - Cenário complexo: 1 novo, 1 modificado, 1 removido, 1 sem mudança
                List<RegionalApiResponseDTO> apiData = List.of(
                                new RegionalApiResponseDTO(101L, "Cuiabá"), // Sem mudança
                                new RegionalApiResponseDTO(102L, "VG - Atualizado"), // Nome mudou
                                new RegionalApiResponseDTO(103L, "Rondonópolis") // Novo
                // 104 não vem mais na API (será inativado)
                );

                Regional cuiaba = Regional.builder()
                                .id(1L)
                                .externalId(101L)
                                .nome("Cuiabá")
                                .ativo(true)
                                .build();

                Regional vg = Regional.builder()
                                .id(2L)
                                .externalId(102L)
                                .nome("Várzea Grande")
                                .ativo(true)
                                .build();

                Regional sinop = Regional.builder()
                                .id(3L)
                                .externalId(104L)
                                .nome("Sinop")
                                .ativo(true)
                                .build();

                when(regionalApiClient.fetchRegionais()).thenReturn(apiData);
                when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(cuiaba, vg, sinop));
                when(regionalRepository.inactivateByExternalId(102L)).thenReturn(1);
                when(regionalRepository.inactivateNotInExternalIds(List.of(101L, 102L, 103L))).thenReturn(1);

                // When
                SyncResultDTO result = regionalSyncService.syncRegionais();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.inserted()).isEqualTo(1); // Rondonópolis (novo)
                assertThat(result.updated()).isEqualTo(1); // VG (nome mudou)
                assertThat(result.inactivated()).isEqualTo(1); // Sinop (removido da API)

                verify(regionalRepository, times(2)).save(any(Regional.class));
        }
}
