package org.maping.maping.api.social.controller;

import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maping.maping.api.auth.dto.request.PasswordRequest;
import org.maping.maping.api.social.dto.request.AddFavoriteRequest;
import org.maping.maping.api.social.dto.request.DeleteFavoriteRequest;
import org.maping.maping.api.social.dto.response.FavoriteCharacterResponse;
import org.maping.maping.api.social.service.SocialService;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.common.utills.jwt.dto.JwtDto;
import org.maping.maping.exceptions.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.mail.MessagingException;
import org.maping.maping.common.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@RestController
@Tag(name = "Social", description = "Social API")
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class SocialController {
    private static  final Logger logger = LoggerFactory.getLogger(SocialController.class);
    private final SocialService socialService;
    private final JWTUtill jwtUtill;

    @Operation(summary = "즐겨찾기 추가", description = "사용자의 즐겨찾기를 추가하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/favorite/post")
    public ResponseEntity<BaseResponse> addFavorite(HttpServletRequest request,@Valid @RequestBody AddFavoriteRequest requestDto) {
        Long userId = Long.parseLong(jwtUtill.getUserId(request));
        socialService.addFavorite(userId, requestDto);
        return ResponseEntity.ok(new BaseResponse<>(200, "즐겨찾기 추가 완료", null));
    }

    @Operation(summary = "즐겨찾기 보기", description = "사용자의 즐겨찾기를 보는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/favorite")
    public BaseResponse<List<FavoriteCharacterResponse>> getUserFavorites(HttpServletRequest request) {
        Long userId = Long.parseLong(jwtUtill.getUserId(request));
        List<FavoriteCharacterResponse> response = socialService.getFavorites(userId);
        return new BaseResponse<>(HttpStatus.OK.value(), "즐겨찾기 캐릭터 조회 성공", response);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "사용자의 즐겨찾기를 삭제하는 API")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/favorite/withdraw")
    public ResponseEntity<BaseResponse> deleteFavorite(HttpServletRequest request, @Valid @RequestBody DeleteFavoriteRequest requestDto) {
        Long userId = Long.parseLong(jwtUtill.getUserId(request));
        socialService.deleteFavorite(userId, requestDto);
        return ResponseEntity.ok(new BaseResponse<>(200, "즐겨찾기 삭제 완료", null));
    }
}
