package org.maping.maping.api.ai.controller;
import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpException;
import org.maping.maping.api.ai.dto.request.AiAdviceRequest;
import org.maping.maping.api.ai.dto.request.AiChatRequest;
import org.maping.maping.api.ai.dto.response.AiChatHistoryDetailResponse;
import org.maping.maping.api.ai.dto.response.AiChatResponse;
import org.maping.maping.api.ai.dto.response.AiHistoryResponse;
import org.maping.maping.api.ai.dto.response.NoticeSummaryResponse;
import org.maping.maping.api.ai.service.AiServiceImpl;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.common.utills.jwt.JWTUtill;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.external.nexon.dto.notice.NoticeListDTO;
import org.maping.maping.external.nexon.dto.notice.NoticeUpdateListDTO;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@lombok.extern.slf4j.Slf4j
@Slf4j
@RestController
@Tag(name = "캐릭터 정보", description = "캐릭터 정보를 가져오는 API")
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiServiceImpl aiServiceImpl;
    private final JWTUtill jwtUtil;

    @Operation(summary = "스텟 맞춤 훈수", description = "GEMINI 스텟 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "stat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAiStat(HttpServletRequest request,
                                          @RequestBody AiAdviceRequest requestDTO){
        if(jwtUtil.getUserId(request) == null) {
            throw new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiStat(requestDTO.getOcid());
    }

    @Operation(summary = "장비 맞춤 훈수", description = "GEMINI 장비 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "item", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getAiEquip(HttpServletRequest request,
                                           @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            throw  new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiItem(requestDTO.getOcid());
    }

    @Operation(summary = "유니온 맞춤 훈수", description = "GEMINI 유니온 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "union", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public  Flux<String> getAiUnion(HttpServletRequest request,
                                           @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            throw  new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiUnion(requestDTO.getOcid());
    }

    @Operation(summary = "아티펙트 맞춤 훈수", description = "GEMINI 아티펙트 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "artifact", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public  Flux<String> getAiArtifact(HttpServletRequest request,
                                              @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            throw  new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiArtifact(requestDTO.getOcid());
    }

    @Operation(summary = "스킬 맞춤 훈수", description = "GEMINI 스킬 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "skill", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public  Flux<String> getAiSkill(HttpServletRequest request,
                                           @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            throw  new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiSkill(requestDTO.getOcid());
    }

    @Operation(summary = "심볼 맞춤 훈수", description = "GEMINI 심볼 맞춤 훈수를 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "symbol", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public  Flux<String> getAiSymbol(HttpServletRequest request,
                                            @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            throw  new CustomException(ErrorCode.BadRequest, "로그인이 필요합니다.");
        }
        return aiServiceImpl.getAiSymbol(requestDTO.getOcid());
    }

    @Operation(summary = "패치노트 요약", description = "GEMINI 패치노트 요약을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("notice")
    public BaseResponse<List<NoticeSummaryResponse>> getNoticeSummary() {
        return new BaseResponse<>(HttpStatus.OK.value(), "패치노트 요약을 가져오는데 성공하였습니다.", aiServiceImpl.getNoticeSummary());
    }

    @Operation(summary = "비로그인 챗봇 대화", description = "GEMINI 챗봇 대화하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("chat/guest")
    public BaseResponse<String> getChat(@RequestBody AiChatRequest requestDTO) throws HttpException, IOException {
        log.info(String.valueOf(requestDTO.getChatId()), requestDTO.getOcid(), requestDTO.getText());
        return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 대화 중", aiServiceImpl.getGuestChat(requestDTO.getChatId(),requestDTO.getCharacterName(),requestDTO.getType(), requestDTO.getOcid(), requestDTO.getText()));
    }

    @Operation(summary = "챗봇 대화", description = "GEMINI 챗봇 대화하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("chat")
    public BaseResponse<AiChatResponse> getChat(HttpServletRequest request,
                                                @RequestBody AiChatRequest requestDTO) throws HttpException, IOException {
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        log.info(String.valueOf(requestDTO.getChatId()), requestDTO.getOcid(), requestDTO.getText());
        return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 대화 중", aiServiceImpl.getChat(userId, requestDTO.getChatId(),requestDTO.getCharacterName(),requestDTO.getType(), requestDTO.getOcid(), requestDTO.getText()));
    }

    @Operation(summary = "챗봇 대화", description = "GEMINI 챗봇 대화하는 API")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Object>> getStreamChat(HttpServletRequest request,
                                                   @RequestBody AiChatRequest requestDTO) throws HttpException, IOException {
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        log.info(String.valueOf(requestDTO.getChatId()), requestDTO.getOcid(), requestDTO.getText());

        return aiServiceImpl.getStreamChat(userId, requestDTO.getChatId(),requestDTO.getCharacterName(),requestDTO.getType(), requestDTO.getOcid(), requestDTO.getText());
    }

    @Operation(summary = "챗봇 기록", description = "GEMINI 챗봇 기록을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("history")
    public BaseResponse<List<AiHistoryResponse>> getHistory(HttpServletRequest request){
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 기록을 가져오는데 성공하였습니다.", aiServiceImpl.getHistory(userId));
    }


    @Operation(summary = "챗봇 대화 기록", description = "GEMINI 챗봇 대화 기록을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("history/{chatId}")
    public BaseResponse<AiChatHistoryDetailResponse> getHistory(HttpServletRequest request,
                                                                      @PathVariable String chatId){
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        return aiServiceImpl.getHistory(userId, chatId);
    }

    @Operation(summary = "챗봇 대화 기록 삭제", description = "GEMINI 챗봇 대화 기록을 삭제하는 API")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("history/{chatId}")
    public BaseResponse<String> deleteHistory(HttpServletRequest request,
                                              @PathVariable String chatId){
        Long userId = Long.valueOf(jwtUtil.getUserId(request));
        return aiServiceImpl.deleteHistory(userId, chatId);
    }

    @Operation(summary = "캐릭터 추천 질문", description = "GEMINI 캐릭터 추천 질문을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("recommend/character")
    public BaseResponse<String> getCharacterRecommend(HttpServletRequest request,
                                             @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            return new BaseResponse<>(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.", "로그인이 필요합니다.");
        }
        return new BaseResponse<>(HttpStatus.OK.value(), "유저 추천 질문을 가져오는데 성공하였습니다.", aiServiceImpl.getCharacterRecommend(requestDTO.getOcid()));
    }

    @Operation(summary = "유저 추천 질문", description = "GEMINI 유저 추천 질문을 가져오는 API")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("recommend/user")
    public BaseResponse<String> getUserRecommend(HttpServletRequest request,
                                             @RequestBody AiAdviceRequest requestDTO) throws HttpException, IOException {
        if(jwtUtil.getUserId(request) == null) {
            return new BaseResponse<>(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.", "로그인이 필요합니다.");
        }
        return new BaseResponse<>(HttpStatus.OK.value(), "유저 추천 질문을 가져오는데 성공하였습니다.", aiServiceImpl.getUserRecommend(requestDTO.getOcid()));
    }
}
