package org.maping.maping.api.character.service;

import lombok.extern.slf4j.Slf4j;
import org.maping.maping.api.character.dto.request.OcidRequest;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.api.character.dto.response.CharacterListResponse;
import org.maping.maping.api.character.dto.response.CharacterResponse;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.character.CharacterBasicDTO;
import org.maping.maping.external.nexon.dto.character.CharacterDTO;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;
import org.maping.maping.external.nexon.dto.character.CharacterListDto;
import org.maping.maping.model.user.UserApiJpaEntity;
import org.maping.maping.repository.search.CharacterSearchRepository;
import org.maping.maping.repository.user.UserApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CharacterServiceImpl implements CharacterService {
    @Autowired
    private final NEXONUtils nexonUtils;
    private final UserApiRepository UserApiRepository;
    private final CharacterSearchRepository CharacterSearchRepository;
    @Autowired
    private CharacterConverter characterConverter;


    public CharacterServiceImpl(NEXONUtils nexonUtils, org.maping.maping.repository.user.UserApiRepository userApiRepository, CharacterSearchRepository characterSearchRepository) {
        this.nexonUtils = nexonUtils;
        UserApiRepository = userApiRepository;
        CharacterSearchRepository = characterSearchRepository;
    }

    public CharacterInfoDTO getCharacterInfo(String characterName) {
        if(characterName == null || characterName.trim().isEmpty()) {
            throw new CustomException(ErrorCode.Forbidden, "캐릭터 이름을 입력해주세요.");
        }
        CharacterDTO characterDto = nexonUtils.getOcid(characterName);
        String ocid = characterDto.getOcid();
        log.info("service ocid: {}", ocid);

        if (ocid == null || ocid.trim().isEmpty()) {
            throw new CustomException(ErrorCode.BadRequest, "유효하지 않은 ocid 입니다.");
        }
        return nexonUtils.getCharacterInfo(ocid, true);
    }

    @Override
    public List<AutocompleteResponse> getAutocomplete(String characterName) {
        if(characterName == null || characterName.trim().isEmpty()) {
            throw new CustomException(ErrorCode.Forbidden, "캐릭터 이름을 입력해주세요.");
        }
        String jaso = nexonUtils.separateJaso(characterName);
        log.info("service jaso: {}", jaso);
        return CharacterSearchRepository.findByJaso(jaso).stream().map(characterConverter::convert).collect(Collectors.toList());
    }

    @Override
    public CharacterListResponse getApiCharacterList(OcidRequest apiKey) {
        log.info(apiKey.getApiKey());
        CharacterListDto characterListDto = nexonUtils.getCharacterList(apiKey.getApiKey());
        CharacterInfoDTO characterInfoDTO = nexonUtils.getCharacterInfo(characterListDto.getAccountList().getFirst().getCharacterList().getFirst().getOcid(), false);
        CharacterListResponse characterListResponse = new CharacterListResponse();
        characterListResponse.setCharacterList(characterListDto);
        characterListResponse.setCharacterInfo(characterInfoDTO);
        return characterListResponse;
    }

    @Override
    public CharacterListResponse getCharacterList(Long userId) {
        Optional<UserApiJpaEntity> userApiJpaEntity = UserApiRepository.findById(userId);
        UserApiJpaEntity user = userApiJpaEntity.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));
        CharacterListDto characterListDto = nexonUtils.getCharacterList(user.getUserApiInfo());
        CharacterInfoDTO characterInfoDTO = nexonUtils.getCharacterInfo(characterListDto.getAccountList().getFirst().getCharacterList().getFirst().getOcid(), false);
        CharacterListResponse characterListResponse = new CharacterListResponse();
        characterListResponse.setCharacterList(characterListDto);
        characterListResponse.setCharacterInfo(characterInfoDTO);
        return characterListResponse;
    }

    @Override
    public CharacterInfoDTO getRefreshCharacterInfo(String characterName) {
        if(characterName == null || characterName.trim().isEmpty()) {
            throw new CustomException(ErrorCode.Forbidden, "캐릭터 이름을 입력해주세요.");
        }
        CharacterDTO characterDto = nexonUtils.getOcid(characterName);
        String ocid = characterDto.getOcid();
        log.info("service ocid: {}", ocid);

        if (ocid == null || ocid.trim().isEmpty()) {
            throw new CustomException(ErrorCode.BadRequest, "유효하지 않은 ocid 입니다.");
        }
        return nexonUtils.getCharacterInfo(ocid, true);
    }

    @Override
    public CharacterResponse getApiCheck(String apiKey) {
        if(apiKey == null || apiKey.trim().isEmpty()) {
            throw new CustomException(ErrorCode.Forbidden, "API 키를 입력해주세요.");
        }
        CharacterListDto characterListDto = nexonUtils.getCharacterList(apiKey);
        CharacterBasicDTO characterBasicDTO = nexonUtils.getCharacterBasic(characterListDto.getAccountList().getFirst().getCharacterList().getFirst().getOcid());

        return CharacterResponse.builder()
                .characterName(characterBasicDTO.getCharacterName())
                .worldName(characterBasicDTO.getWorldName())
                .worldImage(nexonUtils.getWorldImgUrl(characterBasicDTO.getWorldName()))
                .characterClass(characterBasicDTO.getCharacterClass())
                .characterLevel(characterBasicDTO.getCharacterLevel())
                .characterGuildName(characterBasicDTO.getCharacterGuildName())
                .characterImage(characterBasicDTO.getCharacterImage())
                .build();
    }
}
