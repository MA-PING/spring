package org.maping.maping.api.character.controller;
import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.maping.maping.api.character.dto.request.ApiKeyCheckRequest;
import org.maping.maping.api.character.dto.request.OcidRequest;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.api.character.dto.response.CharacterList;
import org.maping.maping.api.character.dto.response.CharacterListResponse;
import org.maping.maping.api.character.dto.response.CharacterResponse;
import org.maping.maping.api.character.service.CharacterServiceImpl;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@lombok.extern.slf4j.Slf4j
@Slf4j
@RestController
@Tag(name = "캐릭터 정보", description = "캐릭터 정보를 가져오는 API")
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterServiceImpl characterServiceImpl;
    private final JWTUtill jwtUtil;

    @Operation(summary = "캐릭터 정보", description = "캐릭터 정보를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("character")
    public BaseResponse<CharacterInfoDTO> getCharacterInfo(@RequestParam String characterName) {
        return new BaseResponse<>(HttpStatus.OK.value(), "캐릭터 정보를 가져오는데 성공하였습니다.", characterServiceImpl.getCharacterInfo(characterName));
    }

    @Operation(summary = "닉네임 자동완성", description = "닉네임 자동완성을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/autocomplete")
    public BaseResponse<List<AutocompleteResponse>> getAutocomplete(@RequestParam String characterName) {
        return new BaseResponse<>(HttpStatus.OK.value(), "자동완성울 가져오는데 성공하였습니다.", characterServiceImpl.getAutocomplete(characterName));
    }

    @Operation(summary = "api 용 캐릭터 리스트", description = "Api 로 캐릭터 리스트를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("character/apiList")
    public BaseResponse<CharacterListResponse> getApiCharacterList(@RequestBody OcidRequest apiKey) {
        return new BaseResponse<>(HttpStatus.OK.value(), "캐릭터 리스트를 가져오는데 성공하였습니다.", characterServiceImpl.getApiCharacterList(apiKey));
    }

    @Operation(summary = "로그인용 캐릭터 정보", description = "로그인으로 캐릭터 정보를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("character/list")
    public BaseResponse<CharacterListResponse> getCharacterList(HttpServletRequest request) {
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        log.info("userId: {}", userId);
        return new BaseResponse<>(HttpStatus.OK.value(), "캐릭터 정보를 가져오는데 성공하였습니다.", characterServiceImpl.getCharacterList(userId));
    }

    @Operation(summary = "캐락터 정보 새로고침", description = "캐락터 정보 새로고침하는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("character/refresh")
    public BaseResponse<CharacterInfoDTO> getRefreshCharacterInfo(@RequestParam String characterName) {
        return new BaseResponse<>(HttpStatus.OK.value(), "캐릭터 정보를 가져오는데 성공하였습니다.", characterServiceImpl.getRefreshCharacterInfo(characterName));
    }

    @Operation(summary = "api 테스트", description = "api 키를 검증하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("character/check")
    public BaseResponse<List<CharacterList>> getApiCheck(@RequestBody ApiKeyCheckRequest apiKey) {
        return new BaseResponse<>(HttpStatus.OK.value(), "api 키를 검증을 성공하였습니다.", characterServiceImpl.getApiCheck(apiKey.getApiKey()));
    }

    @Operation(summary = "캐릭터 리스트만", description = "Api 로 캐릭터 리스트만 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("character/onlyList")
    public BaseResponse<List<CharacterList>> getOnlyCharacterList(HttpServletRequest request) {
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        return new BaseResponse<>(HttpStatus.OK.value(), "캐릭터 리스트를 가져오는데 성공하였습니다.", characterServiceImpl.getList(userId));
    }
}
