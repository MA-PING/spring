package org.maping.maping.external.nexon;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.external.nexon.dto.character.*;
import org.maping.maping.external.nexon.dto.character.ability.CharacterAbilityDTO;
import org.maping.maping.external.nexon.dto.character.android.CharacterAndroidEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.cashItem.CharacterCashItemEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.itemEquipment.CharacterItemEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterHexaMatrixDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterHexaMatrixStatDTO;
import org.maping.maping.external.nexon.dto.character.matrix.CharacterVMatrixDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterLinkSkillDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterSkillDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterHyperStatDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterStatDto;
import org.maping.maping.external.nexon.dto.character.symbol.CharacterSymbolEquipmentDTO;
import org.maping.maping.external.nexon.dto.notice.*;
import org.maping.maping.external.nexon.dto.union.UnionArtifactDTO;
import org.maping.maping.external.nexon.dto.union.UnionDTO;
import org.maping.maping.external.nexon.dto.union.UnionRaiderDTO;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.model.search.CharacterSearchJpaEntity;
import org.maping.maping.repository.search.CharacterSearchRepository;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


@Slf4j
@RestController
@Component
@Service
public class NEXONUtils {

    public final String Key;
    private final CharacterSearchRepository characterSearchRepository;
    private final BlockingQueue<CharacterQDTO> jsonQueue = new LinkedBlockingQueue<>();

    public NEXONUtils(@Value("${spring.nexon.key}") String key, CharacterSearchRepository characterSearchRepository) {
        this.Key = key;
        this.characterSearchRepository = characterSearchRepository;
    }

    // 캐릭터 이름을 통해 ocid를 가져오는 API
    public CharacterDTO getOcid(@NonNull String characterName) {
        log.info("getOcid: {}", characterName);
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/id")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("character_name", characterName).build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                    })
                    .body(CharacterDTO.class);
        }catch (HttpClientErrorException e) {
            log.error("Error response: {}", e.getResponseBodyAsString());
            throw e;
        }
    }

    // ocid를 통해 캐릭터의 기본 정보를 가져오는 API
    public CharacterBasicDTO getCharacterBasic(@NonNull String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/basic")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterBasicDTO.class);
    }

    // API 키를 통해 캐릭터 리스트를 가져오는 API
    public CharacterListDto getCharacterList(@NotBlank String apiKey) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/list")
                .defaultHeader("x-nxopen-api-key", apiKey)
                .build();

        return restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterListDto.class);
    }

    // ocid를 통해 캐릭터의 스탯을 가져오는 API
    public CharacterStatDto getCharacterStat(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/stat")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterStatDto.class);
    }

    // ocid를 통해 캐릭터의 하이퍼스탯을 가져오는 API
    public CharacterHyperStatDTO getCharacterHyperStat(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/hyper-stat")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterHyperStatDTO.class);
    }

    // ocid를 통해 캐릭터의 능력치를 가져오는 API
    public CharacterAbilityDTO getCharacterAbility(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/ability")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterAbilityDTO.class);
    }

    // ocid를 통해 캐릭터의 아이템 정보를 가져오는 API
    public CharacterItemEquipmentDTO getCharacterItemEquip(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/item-equipment")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterItemEquipmentDTO.class);
    }

    public CharacterCashItemEquipmentDTO getCharacterCashItemEquip(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/cashitem-equipment")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterCashItemEquipmentDTO.class);
    }

    // ocid를 통해 캐릭터의 심볼 정보를 가져오는 API
    public CharacterSymbolEquipmentDTO getCharacterSymbolEquipment(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/symbol-equipment")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterSymbolEquipmentDTO.class);
    }

    // ocid를 통해 캐릭터의 스킬 정보를 가져오는 API
    public CharacterSkillDTO getCharacterSkill5(@NotBlank String ocid, int skillGrade) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/skill")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ocid", ocid)
                        .queryParam("character_skill_grade", skillGrade)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterSkillDTO.class);
    }

    // ocid를 통해 캐릭터의 링크 스킬 정보를 가져오는 API
    public CharacterLinkSkillDTO getCharacterLinkSkill(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/link-skill")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterLinkSkillDTO.class);
    }

    // ocid를 통해 캐릭터의 V매트릭스 정보를 가져오는 API
    public CharacterVMatrixDTO getCharacterVmatrix(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/vmatrix")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterVMatrixDTO.class);
    }

    // ocid를 통해 캐릭터의 HEXA 매트릭스 정보를 가져오는 API
    public CharacterHexaMatrixDTO getCharacterHexamatrix(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/hexamatrix")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterHexaMatrixDTO.class);
    }

    // ocid를 통해 캐릭터의 HEXA 매트릭스 스탯 정보를 가져오는 API
    public CharacterHexaMatrixStatDTO getCharacterHexamatrixStat(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/hexamatrix-stat")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterHexaMatrixStatDTO.class);
    }

    // ocid를 통해 안드로이드 장비 정보를 가져오는 API
    public CharacterAndroidEquipmentDTO getCharacterAndroid(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/character/android-equipment")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("ocid", ocid).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(CharacterAndroidEquipmentDTO.class);
    }

    // ocid를 통해 캐릭터의 유니온 정보를 가져오는 API
    public UnionDTO getUnion(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/user/union")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ocid", ocid)
                        .queryParam("union_id", 1)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(UnionDTO.class);
    }

    // ocid를 통해 캐릭터의 유니온 레이더 정보를 가져오는 API
    public UnionRaiderDTO getUnionRaider(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/user/union-raider")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ocid", ocid)
                        .queryParam("union_id", 1)
                        .queryParam("raider_id", 1)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(UnionRaiderDTO.class);
    }

    // ocid를 통해 캐릭터의 유니온 아티팩트 정보를 가져오는 API
    public UnionArtifactDTO getUnionArtifact(@NotBlank String ocid) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/user/union-artifact")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("ocid", ocid)
                        .queryParam("union_id", 1)
                        .queryParam("artifact_id", 1)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(UnionArtifactDTO.class);
    }

    // 공지사항 리스트를 가져오는 API
    public NoticeListDTO getNoticeList() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(NoticeListDTO.class);
    }

    // 공지사항 상세 정보를 가져오는 API
    public NoticeDetailDTO getNoticeDetail(int noticeId) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice/detail")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("notice_id", noticeId).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(NoticeDetailDTO.class);
    }

    // 공지사항 업데이트 리스트를 가져오는 API
    public NoticeUpdateListDTO getNoticeUpdateList() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice-update")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(NoticeUpdateListDTO.class);
    }

    // 공지사항 업데이트 상세 정보를 가져오는 API
    public NoticeDetailDTO getNoticeUpdateDetail(long noticeId) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice-update/detail")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("notice_id", noticeId).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(NoticeDetailDTO.class);
    }

    // 진행 중 이벤트 리스트를 가져오는 API
    public EventNoticeListDTO getNoticeEventList() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice-event")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(EventNoticeListDTO.class);
    }

    // 진행 중 이벤트 상세 정보를 가져오는 API
    public EventNoticeDetailDTO getNoticeEventDetail(long noticeId) {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://open.api.nexon.com/maplestory/v1/notice-event/detail")
                .defaultHeader("x-nxopen-api-key", Key)
                .build();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("notice_id", noticeId).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new CustomException(ErrorCode.Forbidden, (response.getStatusCode() + response.getHeaders().toString()));
                })
                .body(EventNoticeDetailDTO.class);
    }

    // 캐릭터 이름을 통해 캐릭터의 기본 정보를 가져오는 API
    public CharacterInfoDTO getCharacterInfo(String ocid, boolean search) {
        CharacterInfoDTO characterInfo = new CharacterInfoDTO();
        log.info("getCharacterInfo: {}", ocid);
        characterInfo.setOcid(ocid);
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() -> {
            characterInfo.setBasic(getCharacterBasic(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setStat(getCharacterStat(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setHyperStat(getCharacterHyperStat(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setAbility(getCharacterAbility(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setItemEquipment(getCharacterItemEquip(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setSymbolEquipment(getCharacterSymbolEquipment(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setAndroidEquipment(getCharacterAndroid(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setSkill5(getCharacterSkill5(ocid, 5));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setSkill6(getCharacterSkill5(ocid, 6));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setLinkSkill(getCharacterLinkSkill(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setVMatrix(getCharacterVmatrix(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setHexaMatrix(getCharacterHexamatrix(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setHexaMatrixStat(getCharacterHexamatrixStat(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setUnion(getUnion(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setUnionRaider(getUnionRaider(ocid));
            return null;
        });
        tasks.add(() -> {
            characterInfo.setUnionArtifact(getUnionArtifact(ocid));
            return null;
        });

        // ScheduledExecutorService를 사용하여 호출 실행
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        for (int i = 0; i < tasks.size(); i += 4) {
            int start = i;
            int end = Math.min(i + 4, tasks.size());

            scheduler.schedule(() -> {
                for (int j = start; j < end; j++) {
                    try {
                        tasks.get(j).call();
                    } catch (Exception e) {
                        log.error("Error executing task: {}", e.getMessage());
                    }
                }
            }, (i / 4), TimeUnit.SECONDS);
        }

        scheduler.shutdown();
        try {
            scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if(search){
            new Thread(() -> {
                try {
                    jsonQueue.put(getCharacterQDTO(characterInfo.getOcid(), characterInfo.getBasic()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

//        CompletableFuture<CharacterBasicDTO> basic = CompletableFuture.supplyAsync(() -> getCharacterBasic(ocid));
//        CompletableFuture<CharacterStatDto> stat = CompletableFuture.supplyAsync(() -> getCharacterStat(ocid));
//        CompletableFuture<CharacterHyperStatDTO> hyperStat = CompletableFuture.supplyAsync(() -> getCharacterHyperStat(ocid));
//        CompletableFuture<CharacterAbilityDTO> ability = CompletableFuture.supplyAsync(() -> getCharacterAbility(ocid));
//        CompletableFuture<CharacterItemEquipmentDTO> itemEquipment = CompletableFuture.supplyAsync(() -> getCharacterItemEquip(ocid));
//        CompletableFuture<CharacterSymbolEquipmentDTO> symbolEquipment = CompletableFuture.supplyAsync(() -> getCharacterSymbolEquipment(ocid));
//        CompletableFuture<CharacterSkillDTO> skill5 = CompletableFuture.supplyAsync(() -> getCharacterSkill5(ocid, 5));
//        CompletableFuture<CharacterSkillDTO> skill6 = CompletableFuture.supplyAsync(() -> getCharacterSkill5(ocid, 6));
//        CompletableFuture<CharacterLinkSkillDTO> linkSkill = CompletableFuture.supplyAsync(() -> getCharacterLinkSkill(ocid));
//        CompletableFuture<CharacterVMatrixDTO> vMatrix = CompletableFuture.supplyAsync(() -> getCharacterVmatrix(ocid));
//        CompletableFuture<CharacterHexaMatrixDTO> hexaMatrix = CompletableFuture.supplyAsync(() -> getCharacterHexamatrix(ocid));
//        CompletableFuture<CharacterHexaMatrixStatDTO> hexaMatrixStat = CompletableFuture.supplyAsync(() -> getCharacterHexamatrixStat(ocid));
//        CompletableFuture<UnionDTO> union = CompletableFuture.supplyAsync(() -> getUnion(ocid));
//        CompletableFuture<UnionRaiderDTO> unionRaider = CompletableFuture.supplyAsync(() -> getUnionRaider(ocid));
//        CompletableFuture<UnionArtifactDTO> unionArtifact = CompletableFuture.supplyAsync(() -> getUnionArtifact(ocid));

//        characterInfo.setOcid(ocid);
//        characterInfo.setBasic(getCharacterBasic(ocid));
//        characterInfo.setStat(getCharacterStat(ocid));
//        characterInfo.setHyperStat(getCharacterHyperStat(ocid));
//        characterInfo.setAbility(getCharacterAbility(ocid));
//        characterInfo.setItemEquipment(getCharacterItemEquip(ocid));
//        characterInfo.setSymbolEquipment(getCharacterSymbolEquipment(ocid));
//        characterInfo.setSkill5(getCharacterSkill5(ocid, 5));
//        characterInfo.setSkill6(getCharacterSkill5(ocid, 6));
//        characterInfo.setLinkSkill(getCharacterLinkSkill(ocid));
//        characterInfo.setVMatrix(getCharacterVmatrix(ocid));
//        characterInfo.setHexaMatrix(getCharacterHexamatrix(ocid));
//        characterInfo.setHexaMatrixStat(getCharacterHexamatrixStat(ocid));
//        characterInfo.setUnion(getUnion(ocid));
//        characterInfo.setUnionRaider(getUnionRaider(ocid));
//        characterInfo.setUnionArtifact(getUnionArtifact(ocid));

//        characterInfo.setBasic(basic.join());
//        if(search){
//            new Thread(() -> {
//                try {
//                    jsonQueue.put(basic);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }).start();
//        }
//
//        characterInfo.setStat(stat.join());
//        characterInfo.setHyperStat(hyperStat.join());
//        characterInfo.setAbility(ability.join());
//        characterInfo.setItemEquipment(itemEquipment.join());
//        characterInfo.setSymbolEquipment(symbolEquipment.join());
//        characterInfo.setSkill5(skill5.join());
//        characterInfo.setSkill6(skill6.join());
//        characterInfo.setLinkSkill(linkSkill.join());
//        characterInfo.setVMatrix(vMatrix.join());
//        characterInfo.setHexaMatrix(hexaMatrix.join());
//        characterInfo.setHexaMatrixStat(hexaMatrixStat.join());
//        characterInfo.setUnion(union.join());
//        characterInfo.setUnionRaider(unionRaider.join());
//        characterInfo.setUnionArtifact(unionArtifact.join());

        if(characterInfo.getBasic() == null) {
            throw new CustomException(ErrorCode.NotFound, "캐릭터 정보를 찾을 수 없습니다.");
        }{
            return characterInfo;
        }
    }

    private CharacterQDTO getCharacterQDTO(String ocid, CharacterBasicDTO basic) {
        CharacterQDTO characterQDTO = new CharacterQDTO();
        characterQDTO.setOcid(ocid);
        characterQDTO.setBasic(basic);
        return characterQDTO;
    }

    public void setCharacterInfo(CharacterQDTO characterInfo) {
        String characterName = characterInfo.getBasic().getCharacterName();
        Optional<CharacterSearchJpaEntity> characterSearch = characterSearchRepository.findByCharacterName(characterName);

        CharacterSearchJpaEntity entity;
        if (characterSearch.isPresent()) {
            entity = CharacterSearchJpaEntity.builder()
                    .id(characterSearch.get().getId())
                    .ocid(characterSearch.get().getOcid())
                    .characterName(characterInfo.getBasic().getCharacterName())
                    .characterLevel(characterInfo.getBasic().getCharacterLevel())
                    .worldName(characterInfo.getBasic().getWorldName())
                    .characterClass(characterInfo.getBasic().getCharacterClass())
                    .image(characterInfo.getBasic().getCharacterImage())
                    .guild(characterInfo.getBasic().getCharacterGuildName())
                    .jaso(separateJaso(characterInfo.getBasic().getCharacterName()))
                    .count(characterSearch.get().getCount() + 1)
                    .createdAt(characterSearch.get().getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            entity = CharacterSearchJpaEntity.builder()
                    .ocid(characterInfo.getOcid())
                    .characterName(characterInfo.getBasic().getCharacterName())
                    .characterLevel(characterInfo.getBasic().getCharacterLevel())
                    .worldName(characterInfo.getBasic().getWorldName())
                    .characterClass(characterInfo.getBasic().getCharacterClass())
                    .image(characterInfo.getBasic().getCharacterImage())
                    .jaso(separateJaso(characterInfo.getBasic().getCharacterName()))
                    .count(1)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
        characterSearchRepository.save(entity);
    }

    // 호환용 자모 배열 (초성, 중성, 종성)
    private static final char[] CHOSUNG = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    private static final char[] JUNGSUNG = {'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'};
    private static final char[] JONGSUNG = {' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public String separateJaso(String input) {
        List<String> result = new ArrayList<>();

        for (char ch : input.toCharArray()) {
            // 1. 한글 음절인 경우 (가 ~ 힣)
            if (ch >= 0xAC00 && ch <= 0xD7A3) {
                result.addAll(decomposeHangul(ch));
            }
            // 2. 이미 분리된 한글 자모인 경우 (ㄱ, ㅏ 등)
            else if (ch >= 0x3130 && ch <= 0x318F) {
                result.add(String.valueOf(ch)); // 그대로 추가
            }
            // 3. 한글이 아닌 경우
            else {
                result.add(String.valueOf(ch)); // 그대로 추가
            }
        }
        return String.join(",", result);
    }

    /**
     * 한글 음절을 초성, 중성, 종성으로 분해합니다.
     * @param hangul 분해할 한글 음절
     * @return 호환용 자모가 담긴 리스트
     */
    private static List<String> decomposeHangul(char hangul) {
        List<String> jasoList = new ArrayList<>();
        int base = hangul - 0xAC00;

        int choIndex = base / (21 * 28);
        int jungIndex = (base % (21 * 28)) / 28;
        int jongIndex = base % 28;

        // 초성 추가
        jasoList.add(String.valueOf(CHOSUNG[choIndex]));
        // 중성 추가
        jasoList.add(String.valueOf(JUNGSUNG[jungIndex]));

        // 종성이 있는 경우에만 추가
        if (jongIndex > 0) {
            jasoList.add(String.valueOf(JONGSUNG[jongIndex]));
        }

        return jasoList;
    }

    @Scheduled(fixedDelay = 1000 * 60) //15분 1000 * 60 * 15 , 30초 1000 * 30
    public void setCharacterSearch() {
        while (!jsonQueue.isEmpty()) {
            CharacterQDTO basic = jsonQueue.poll();
            if (basic != null) {
                setCharacterInfo(basic);
                log.info("캐릭터 검색 테이블 삽입: {}", basic.getBasic().getCharacterName());
            }
        }
    }

    public String basicString(CharacterBasicDTO basic) {

        return "캐릭터 이름: " + basic.getCharacterName() + "\n" +
                "캐릭터 레벨: " + basic.getCharacterLevel() + "\n" +
                "월드 이름: " + basic.getWorldName() + "\n" +
                "직업 이름: " + basic.getCharacterClass();
    }

    public String statString(CharacterStatDto stat) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < stat.getFinalStat().size(); i++){
            result.append(stat.getFinalStat().get(i).getStatName()).append(": ").append(stat.getFinalStat().get(i).getStatValue()).append(",\n");
        }
        return result.toString();
    }

    public String itemString(CharacterItemEquipmentDTO itemEquipment) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < itemEquipment.getItemEquipment().size(); i++){
            result.append(itemEquipment.getItemEquipment().get(i)).append(",\n");
        }
        return result.toString();
    }

    public String unionString(UnionDTO union, UnionRaiderDTO unionRaiderDTO) {
        StringBuilder result = new StringBuilder();

        result.append(union.getUnionGrade()).append(", ").append(union.getUnionLevel()).append(",\n");
        result.append(unionRaiderDTO.getUnionRaiderStat()).append(",\n");
        result.append(unionRaiderDTO.getUnionOccupiedStat()).append(",\n");

        for(int i = 0; i < unionRaiderDTO.getUnionBlock().size(); i++) {
            result.append(unionRaiderDTO.getUnionBlock().get(i).getBlockClass()).append(", ").append(unionRaiderDTO.getUnionBlock().get(i).getBlockLevel()).append(",\n");
        }


        return result.toString();
    }

    public String artifactString(UnionArtifactDTO unionArtifact) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < unionArtifact.getUnionArtifactEffect().size(); i++){
            result.append(unionArtifact.getUnionArtifactEffect().get(i).getName()).append(": ").append(unionArtifact.getUnionArtifactEffect().get(i).getLevel()).append(",\n");
        }
        for(int i = 0; i < unionArtifact.getUnionArtifactCrystal().size(); i++) {
            result.append(unionArtifact.getUnionArtifactCrystal().get(i).getName()).append(": {").append(unionArtifact.getUnionArtifactCrystal().get(i).getLevel()).append(", ")
                    .append(unionArtifact.getUnionArtifactCrystal().get(i).getDateExpire()).append("}\n");
        }
        return result.toString();
    }

    public String skillString(CharacterSkillDTO skill5, CharacterSkillDTO skill6, CharacterLinkSkillDTO linkSkill) {
        StringBuilder result = new StringBuilder();
        result.append("5차 스킬\n");
        for(int i = 0; i < skill5.getCharacterSkill().size(); i++){
            result.append(skill5.getCharacterSkill().get(i).getSkillName()).append(": {").append(skill5.getCharacterSkill().get(i).getSkillLevel()).append(",")
                    .append(skill5.getCharacterSkill().get(i).getSkillEffect()).append("}\n");
        }
        result.append("6차 스킬\n");
        for(int i = 0; i < skill6.getCharacterSkill().size(); i++){
            result.append(skill6.getCharacterSkill().get(i).getSkillName()).append(": {").append(skill6.getCharacterSkill().get(i).getSkillLevel()).append(",")
                    .append(skill6.getCharacterSkill().get(i).getSkillEffect()).append("}\n");
        }
        result.append("링크 스킬\n");
        for(int i = 0; i < linkSkill.getCharacterLinkSkill().size(); i++){
            result.append(linkSkill.getCharacterLinkSkill().get(i).getSkillName()).append(": {").append(linkSkill.getCharacterLinkSkill().get(i).getSkillLevel()).append(",")
                    .append(linkSkill.getCharacterLinkSkill().get(i).getSkillEffect()).append("}\n");
        }
        return result.toString();
    }

    public String symbolString(CharacterSymbolEquipmentDTO symbolEquipment) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < symbolEquipment.getSymbol().size(); i++){
            result.append(symbolEquipment.getSymbol().get(i).getSymbolName()).append(": ").append(symbolEquipment.getSymbol().get(i).getSymbolLevel()).append(",\n");
        }
        return result.toString();
    }


//    public FavoriteResponse favoriteResponse(Long userId){
//
//        return FavoriteResponse.builder()
//                .
//    }

}
