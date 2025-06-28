package org.maping.maping.api.character.service;

import lombok.extern.slf4j.Slf4j;
import org.maping.maping.api.character.dto.request.OcidRequest;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.api.character.dto.response.CharacterList;
import org.maping.maping.api.character.dto.response.CharacterListResponse;
import org.maping.maping.api.character.dto.response.CharacterResponse;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.character.*;
import org.maping.maping.external.nexon.dto.union.UnionRankingDTO;
import org.maping.maping.external.nexon.dto.union.UnionRankingList;
import org.maping.maping.model.user.UserApiJpaEntity;
import org.maping.maping.repository.search.CharacterSearchRepository;
import org.maping.maping.repository.user.UserApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    public CharacterServiceImpl(NEXONUtils nexonUtils, UserApiRepository userApiRepository, CharacterSearchRepository characterSearchRepository) {
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
        log.info(jaso);
        return CharacterSearchRepository.findByJaso(jaso).stream().map(characterConverter::convert).collect(Collectors.toList());
    }
    public String getMainCharacter(List<CharacterListAccountCharacterDTO> characterList) {
        CharacterListAccountCharacterDTO main = characterList.getFirst();
        for (CharacterListAccountCharacterDTO character : characterList) {
            if (main.getCharacterLevel() < character.getCharacterLevel()) {
                main = character;
            }
        }
        return main.getOcid();
    }
    @Override
    public CharacterListResponse getApiCharacterList(OcidRequest apiKey) {
        log.info(apiKey.getApiKey());
        String ocid = null;
        CharacterListDto characterListDto = nexonUtils.getCharacterList(apiKey.getApiKey());
        if(characterListDto == null || characterListDto.getAccountList() == null || characterListDto.getAccountList().isEmpty()) {
            return null;
        }
        if (characterListDto.getAccountList() == null || characterListDto.getAccountList().isEmpty()) {
            throw new CustomException(ErrorCode.BadRequest, "유효하지 않은 API 키입니다.");
        }
        List<CharacterList> characterList = convertCharacterList(characterListDto.getAccountList().getFirst().getCharacterList());
        UnionRankingList ranking = nexonUtils.getUnionRanking(characterList.getFirst().getOcid());
        if (ranking == null || ranking.getRanking() == null || ranking.getRanking().isEmpty()) {
            ocid = getMainCharacter(characterListDto.getAccountList().getFirst().getCharacterList());

        }else{
            String name = ranking.getRanking().getFirst().getCharacterName();
            for (CharacterList c : characterList) {
                if (c.getCharacterName().equals(name)) {
                    ocid = c.getOcid();
                    break;
                }
            }
        }
        CharacterInfoDTO characterInfoDTO = nexonUtils.getCharacterInfo(ocid, false);

        CharacterListResponse characterListResponse = new CharacterListResponse();
        characterListResponse.setCharacterList(convertCharacterList(characterList, ocid));
        characterListResponse.setCharacterInfo(characterInfoDTO);
        return characterListResponse;
    }
    public List<CharacterList> convertCharacterList(List<CharacterListAccountCharacterDTO> characterList) {
        return characterList.stream()
                .map(character -> {
                    // 캐릭터 기본 정보를 가져옵니다. 에러 발생 시 빈 DTO를 반환합니다.
                    CharacterBasicDTO characterBasicDTO = nexonUtils.getCharacterBasic(character.getOcid());

                    if (characterBasicDTO == null || characterBasicDTO.getCharacterImage() == null || characterBasicDTO.getCharacterImage().isEmpty()) {
                        log.warn("기본 정보가 비어있거나 유효하지 않아 캐릭터를 건너뜁니다 (OCID: {}, 이름: {}).",
                                character.getOcid(), character.getCharacterName());
                        return null; // 유효하지 않은 경우 null 반환
                    }

                    // 유효한 정보인 경우에만 CharacterList 객체 생성 및 정보 설정
                    CharacterList characterResponse = new CharacterList();
                    characterResponse.setOcid(character.getOcid());
                    characterResponse.setCharacterName(character.getCharacterName());
                    characterResponse.setWorldName(character.getWorldName());
                    characterResponse.setCharacterClass(character.getCharacterClass());
                    characterResponse.setCharacterLevel(character.getCharacterLevel());
                    characterResponse.setCharacterImage(characterBasicDTO.getCharacterImage());
                    characterResponse.setGuildName(characterBasicDTO.getCharacterGuildName());
                    characterResponse.setMainCharacter(false);
                    return characterResponse;
                })
                .filter(Objects::nonNull) // map 단계에서 null이 된 요소를 제거합니다.
                .collect(Collectors.toList());
    }
    public List<CharacterList> convertCharacterList(List<CharacterList> characterList, String mainOcid) {
        return characterList.stream()
                .map(character -> {

                    // 유효한 정보인 경우에만 CharacterList 객체 생성 및 정보 설정
                    CharacterList characterResponse = new CharacterList();
                    characterResponse.setOcid(character.getOcid());
                    characterResponse.setCharacterName(character.getCharacterName());
                    characterResponse.setWorldName(character.getWorldName());
                    characterResponse.setCharacterClass(character.getCharacterClass());
                    characterResponse.setCharacterLevel(character.getCharacterLevel());
                    characterResponse.setCharacterImage(character.getCharacterImage());
                    characterResponse.setGuildName(character.getGuildName());
                    characterResponse.setMainCharacter(character.getOcid().equals(mainOcid)); // 메인 캐릭터 여부 설정
                    return characterResponse;
                })
                .collect(Collectors.toList());
    }
    @Override
    public CharacterListResponse getCharacterList(Long userId) {
        String ocid = "";
        Optional<UserApiJpaEntity> userApiJpaEntity = UserApiRepository.findById(userId);
        UserApiJpaEntity user = userApiJpaEntity.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저입니다."));
        CharacterListDto characterListDto = nexonUtils.getCharacterList(user.getUserApiInfo());
        if(characterListDto == null || characterListDto.getAccountList() == null || characterListDto.getAccountList().isEmpty()) {
            return null;
        }
        List<CharacterList> characterList = convertCharacterList(characterListDto.getAccountList().getFirst().getCharacterList());
        UnionRankingList ranking = nexonUtils.getUnionRanking(characterList.getFirst().getOcid());
        if (ranking == null || ranking.getRanking() == null || ranking.getRanking().isEmpty()) {
            ocid = getMainCharacter(characterListDto.getAccountList().getFirst().getCharacterList());

        }else{
            String name = ranking.getRanking().getFirst().getCharacterName();
            for (CharacterList c : characterList) {
                if (c.getCharacterName().equals(name)) {
                    ocid = c.getOcid();
                    break;
                }
            }
        }
        CharacterInfoDTO characterInfoDTO = nexonUtils.getCharacterInfo(ocid, false);
        CharacterListResponse characterListResponse = new CharacterListResponse();
        characterListResponse.setCharacterList(convertCharacterList(characterList, ocid));
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
    public List<CharacterList> getApiCheck(String apiKey) {
        String ocid = null;
        if(apiKey == null || apiKey.trim().isEmpty()) {
            throw new CustomException(ErrorCode.Forbidden, "API 키를 입력해주세요.");
        }

        CharacterListDto characterListDto = nexonUtils.getCharacterList(apiKey);
        if(characterListDto == null || characterListDto.getAccountList() == null || characterListDto.getAccountList().isEmpty()) {
            return null;
        }
        List<CharacterList> characterList = convertCharacterList(characterListDto.getAccountList().getFirst().getCharacterList());
        UnionRankingList ranking = nexonUtils.getUnionRanking(characterList.getFirst().getOcid());
        if (ranking == null || ranking.getRanking() == null || ranking.getRanking().isEmpty()) {
            ocid = getMainCharacter(characterListDto.getAccountList().getFirst().getCharacterList());

        }else{
            String name = ranking.getRanking().getFirst().getCharacterName();
            for (CharacterList c : characterList) {
                if (c.getCharacterName().equals(name)) {
                    ocid = c.getOcid();
                    break;
                }
            }
        }
        return convertCharacterList(characterList, ocid);
    }
}
