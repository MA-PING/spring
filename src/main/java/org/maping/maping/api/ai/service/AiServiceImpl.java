package org.maping.maping.api.ai.service;

import autovalue.shaded.org.checkerframework.checker.nullness.qual.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.genai.ResponseStream;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.maping.maping.api.ai.dto.response.*;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.external.gemini.GEMINIUtils;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.character.CharacterBasicDTO;
import org.maping.maping.external.nexon.dto.character.itemEquipment.CharacterItemEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterLinkSkillDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterSkillDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterStatDto;
import org.maping.maping.external.nexon.dto.character.symbol.CharacterSymbolEquipmentDTO;
import org.maping.maping.external.nexon.dto.union.UnionArtifactDTO;
import org.maping.maping.external.nexon.dto.union.UnionDTO;
import org.maping.maping.external.nexon.dto.union.UnionRaiderDTO;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.maping.maping.repository.ai.AiHistoryRepository;
import org.maping.maping.repository.ai.NoticeRepository;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
@Slf4j
@Service
public class AiServiceImpl implements AiService{
    private final GEMINIUtils geminiUtils;
    private final NoticeRepository noticeRepository;
    private final NEXONUtils nexonUtils;
    private final AiHistoryRepository aiHistoryRepository;
    private final UserRepository userRepository;
    private final AiHistoryConvert setAiHistoryConvert;

    public AiServiceImpl(GEMINIUtils geminiUtils, NoticeRepository noticeRepository, NEXONUtils nexonUtils, AiHistoryRepository aiHistoryRepository, UserRepository userRepository, AiHistoryConvert setAiHistoryConvert) {
        this.geminiUtils = geminiUtils;
        this.noticeRepository = noticeRepository;
        this.nexonUtils = nexonUtils;
        this.aiHistoryRepository = aiHistoryRepository;
        this.userRepository = userRepository;
        this.setAiHistoryConvert = setAiHistoryConvert;
    }

    @Override
    public Flux<String> getAiStat(String ocid) {

        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterStatDto stat = nexonUtils.getCharacterStat(ocid);
        String statString = nexonUtils.statString(stat);

        String text = "기본정보 : {" + basicString + "}, 스탯 : {" + statString + "}\n" +
                "메이플 캐릭터의 기본 정보와 스탯 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";


        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public Flux<String> getAiItem(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterItemEquipmentDTO itemEquipment = nexonUtils.getCharacterItemEquip(ocid);
        String itemString = nexonUtils.itemString(itemEquipment);

        String text = "기본정보 : {" + basicString + "}, 장비 : {" + itemString + "}\n" +
                "메이플 캐릭터의 기본 정보와 장비 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";

        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public Flux<String> getAiUnion(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        UnionDTO union = nexonUtils.getUnion(ocid);
        UnionRaiderDTO unionRaider = nexonUtils.getUnionRaider(ocid);
        String unionString = nexonUtils.unionString(union, unionRaider);

        String text = "기본정보 : {" + basicString + "}, 유니온 : {" + unionString + "}\n" +
                "메이플 캐릭터의 기본 정보와 유니온과 유니온 레이더 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";

        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public Flux<String> getAiArtifact(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        UnionArtifactDTO unionArtifact = nexonUtils.getUnionArtifact(ocid);
        String artifactString = nexonUtils.artifactString(unionArtifact);

        String text = "기본정보 : {" + basicString + "}, 유니온 아티팩트 : {" + artifactString + "}\n" +
                "메이플 캐릭터의 기본 정보와 유니온 아티팩트 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";
        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public Flux<String> getAiSkill(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterSkillDTO skill5 = nexonUtils.getCharacterSkill5(ocid, 5);
        CharacterSkillDTO skill6 = nexonUtils.getCharacterSkill5(ocid, 6);
        CharacterLinkSkillDTO linkSkill = nexonUtils.getCharacterLinkSkill(ocid);
        String skillString = nexonUtils.skillString(skill5, skill6, linkSkill);

        String text = "기본정보 : {" + basicString + "}, 스킬 : {" + skillString + "}\n" +
                "메이플 캐릭터의 기본 정보와 스킬 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";
        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public Flux<String> getAiSymbol(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterSymbolEquipmentDTO symbol = nexonUtils.getCharacterSymbolEquipment(ocid);
        String symbolString = nexonUtils.symbolString(symbol);
        String text = "기본정보 : {" + basicString + "}, 심볼 : {" + symbolString + "}\n" +
                "메이플 캐릭터의 기본 정보와 심볼 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "좋으면 답변할 때 맨 앞에 1~5로 매우 좋으면 5에서 매우 안 좋으면 1로 점수를 매겨서 알려줘 \n" +
                "답변할때 내가 알려준 기본정보와 스택을 다시 알려주지 않아도돼. \n" +
                "그리고 200자 이내로 대답해줘.";
        return geminiUtils.getGeminiStreamResponse(text);
    }

    @Override
    public List<NoticeSummaryResponse> getNoticeSummary() {
        return noticeRepository.getNotice(3).stream().map(notice -> {
            NoticeSummaryResponse noticeSummaryResponse = new NoticeSummaryResponse();
            noticeSummaryResponse.setTitle(notice.getNoticeTitle());
            noticeSummaryResponse.setUrl(notice.getNoticeUrl());
            noticeSummaryResponse.setDate(notice.getNoticeDate());
            noticeSummaryResponse.setSummary(notice.getNoticeSummary());
            noticeSummaryResponse.setVersion(notice.getVersion());
            return noticeSummaryResponse;
        }).toList();
    }

    public AiChatHistoryDTO historyDto(@Nullable String ocid, @Nullable String characterName, @Nullable String type, String text, String content){
        return AiChatHistoryDTO.builder()
                .ocid(ocid)
                .characterName(characterName)
                .type(type)
                .answer(content)
                .question(text)
                .build();
    }

    public AiChatResponse aiChatResponseDto(String chatId, @Nullable String ocid, String topic, String content){
        return AiChatResponse.builder()
                .chatId(chatId)
                .ocid(ocid)
                .topic(topic)
                .text(content)
                .build();
    }

    public String saveAiHistory(String chatId, UserInfoJpaEntity userInfoJpaEntity, String topic, String content) {
        AiHistoryJpaEntity aiHistoryJpaEntity = new AiHistoryJpaEntity();
        aiHistoryJpaEntity.setChatId(chatId);
        aiHistoryJpaEntity.setUser(userInfoJpaEntity);
        aiHistoryJpaEntity.setTopic(topic);
        aiHistoryJpaEntity.setContent(content);
        aiHistoryJpaEntity.setUpdatedAt(LocalDateTime.now());
        AiHistoryJpaEntity savedEntity = aiHistoryRepository.save(aiHistoryJpaEntity);
        return savedEntity.getChatId();
    }

    public boolean typeCheck(String type) {
        return type.equals("stat") || type.equals("item") || type.equals("union") || type.equals("artifact") || type.equals("skill") || type.equals("symbol");
    }

    public String getTypeText(String ocid,String type, String text) {
        String typeText;
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        switch (type) {
            case "stat" -> {
                CharacterStatDto stat = nexonUtils.getCharacterStat(ocid);
                String statString = nexonUtils.statString(stat);

                typeText = "기본정보 : {" + basicString + "}, 스탯 : {" + statString + "}\n" + text;
                return typeText;
            }
            case "item" -> {
                CharacterItemEquipmentDTO itemEquipment = nexonUtils.getCharacterItemEquip(ocid);
                String itemString = nexonUtils.itemString(itemEquipment);
                typeText = "기본정보 : {" + basicString + "}, 아이템 : {" + itemString + "}\n" + text;

                return typeText;
            }
            case "union" -> {
                UnionDTO union = nexonUtils.getUnion(ocid);
                UnionRaiderDTO unionRaider = nexonUtils.getUnionRaider(ocid);
                String unionString = nexonUtils.unionString(union, unionRaider);

                typeText = "기본정보 : {" + basicString + "}, 유니온 : {" + unionString + "}\n" + text;

                return typeText;
            }
            case "artifact" -> {
                UnionArtifactDTO unionArtifact = nexonUtils.getUnionArtifact(ocid);
                String artifactString = nexonUtils.artifactString(unionArtifact);

                typeText = "기본정보 : {" + basicString + "}, 유니온 아티팩트 : {" + artifactString + "}\n" + text;
                return typeText;
            }
            case "skill" -> {
                CharacterSkillDTO skill5 = nexonUtils.getCharacterSkill5(ocid, 5);
                CharacterSkillDTO skill6 = nexonUtils.getCharacterSkill5(ocid, 6);
                CharacterLinkSkillDTO linkSkill = nexonUtils.getCharacterLinkSkill(ocid);
                String skillString = nexonUtils.skillString(skill5, skill6, linkSkill);

                typeText = "기본정보 : {" + basicString + "}, 스킬 : {" + skillString + "}\n" + text;
                return typeText;
            }
            case "symbol" -> {
                CharacterSymbolEquipmentDTO symbol = nexonUtils.getCharacterSymbolEquipment(ocid);
                String symbolString = nexonUtils.symbolString(symbol);
                typeText = "기본정보 : {" + basicString + "}, 심볼 : {" + symbolString + "}\n" + text;
                return typeText;
            }
            default -> {
                throw new CustomException(ErrorCode.BadRequest, "잘못된 요청입니다.");
            }
        }
    }

    @Override
    public String getCharacterRecommend(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        String text = "기본정보 : {" + basicString + "}\n" +
                "메이플 캐릭터의 기본 정보야 사용자가 AI 에게 물어볼만한 추천 질문 5개를 알려줘.";
        return geminiUtils.getGeminiGoogleResponse(text);
    }

    @Override
    public String getUserRecommend(String ocid) throws HttpException, IOException {
        String text = "메이플 스토리 유저가 AI 에게 물어볼만한 추천 질문 5개를 알려줘.";
        return geminiUtils.getGeminiGoogleResponse(text);
    }

    @Override
    public List<AiHistoryResponse> getHistory(Long userId) {
        UserInfoJpaEntity userInfoJpaEntity = userRepository.findById(userId).orElse(null);
        return aiHistoryRepository.findByUserId(userInfoJpaEntity).stream().map(history ->
            AiHistoryResponse.builder()
                    .chatId(history.getChatId())
                    .topic(history.getTopic())
                    .dateTime(history.getUpdatedAt())
                    .build()
        ).toList().reversed();
    }

    @Override
    public BaseResponse<AiChatHistoryDetailResponse> getHistory(Long userId, String chatId) {
        AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(chatId);
        if(aiHistoryChatId == null){
            throw new CustomException(ErrorCode.NotFound, "챗봇 대화 기록이 존재하지 않습니다.");
        }else if(aiHistoryChatId.getChatId().equals(chatId)){
            return new BaseResponse<>(HttpStatus.OK.value(), "대화 기록을 가져왔습니다.", AiChatHistoryDetailResponse.builder()
                    .chatId(chatId)
                    .topic(aiHistoryChatId.getTopic())
                    .dateTime(aiHistoryChatId.getUpdatedAt())
                    .history(setAiHistoryConvert.setAiHistoryConvert(aiHistoryChatId.getContent()))
                    .build());
        }
        throw new CustomException(ErrorCode.BadRequest, "대화 기록을 찾을 수 없습니다.");
    }

    @Override
    public BaseResponse<String> deleteHistory(Long userId, String chatId) {
        AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(chatId);
        log.info("aiHistoryChatId: {}", aiHistoryChatId);
        if(aiHistoryChatId == null){
            return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 대화 기록이 존재하지 않습니다.", "챗봇 대화 기록이 존재하지 않습니다.");
        }else if(aiHistoryChatId.getUser().getUserId().equals(userId)){
            aiHistoryRepository.delete(aiHistoryChatId);
            return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 대화 기록을 삭제하였습니다.", "챗봇 대화 기록을 삭제하였습니다.");
        }
        return new BaseResponse<>(HttpStatus.OK.value(), "챗봇 대화 기록을 삭제할 수 없습니다.", "챗봇 대화 기록을 삭제할 수 없습니다.");
    }



    @Override
    public AiChatResponse getChat(Long userId, String chatId, String characterName, String type, String ocid, String text) throws HttpException, IOException {
        UserInfoJpaEntity userInfoJpaEntity = userRepository.findById(userId).orElse(null);
        AiChatResponse aiChatResponse = new AiChatResponse();
        AiHistoryJpaEntity aiHistoryJpaEntity = new AiHistoryJpaEntity();
        List<AiChatHistoryDTO> HistoryListDTO = new ArrayList<>();
        String content;
        if(chatId == null & ocid == null & type == null) {
            content = geminiUtils.getGeminiGoogleResponse(text);
            String topic = geminiUtils.getGeminiResponse(text + "\n 이 내용에 대해 짧게 요약해줘.");

            //DTO에 값 넣기
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content);

            //JPA에 값 넣기
            HistoryListDTO.add(aiChatHistoryDTO);
            String savedEntity = saveAiHistory(UUID.randomUUID().toString(), userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryListDTO));
            log.info("chatId: {}", aiHistoryJpaEntity.getChatId());

            //Response에 값 넣기
            aiChatResponse = aiChatResponseDto(savedEntity, ocid, topic, content);
        }else if(chatId != null & ocid == null & type == null) {
            AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(Objects.requireNonNull(chatId));

            if(aiHistoryChatId == null){
                throw new CustomException(ErrorCode.NotFound, "챗봇 대화가 존재하지 않습니다.");
            }

            HistoryListDTO = setAiHistoryConvert.setAiHistoryConvert(Objects.requireNonNull(aiHistoryChatId).getContent());
            content = geminiUtils.getGeminiChatResponse(HistoryListDTO, text);

            //DTO에 값 넣기
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content);

            HistoryListDTO.add(aiChatHistoryDTO);
            aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
            aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO));
            aiHistoryRepository.save(aiHistoryChatId);

            //Response에 값 넣기
            aiChatResponse = aiChatResponseDto(chatId, ocid, Objects.requireNonNull(aiHistoryChatId).getTopic(), content);
        }else if(chatId == null & ocid != null & type != null) {
            if(!typeCheck(type)){
                throw new CustomException(ErrorCode.BadRequest, "잘못된 타입 요청입니다.");
            }
            String typeText = getTypeText(ocid, type, text);
            String topic = geminiUtils.getGeminiResponse(text + "\n 이 내용에 대해 짧게 요약해줘.");
            content = geminiUtils.getGeminiGoogleResponse(typeText);

            //JPA에 값 넣기
            HistoryListDTO.add(historyDto(ocid, characterName, type, text, content));
            String savedEntity = saveAiHistory(UUID.randomUUID().toString(), userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryListDTO));
            log.info("chatId: {}, type: {}", aiHistoryJpaEntity.getChatId(), type);

            //Response에 값 넣기
            aiChatResponse = aiChatResponseDto(savedEntity, ocid, topic, content);
        }else if(chatId != null & ocid != null & type != null) {
            if(!typeCheck(type)){
                throw new CustomException(ErrorCode.BadRequest, "잘못된 타입 요청입니다.");
            }
            AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(Objects.requireNonNull(chatId));

            if(aiHistoryChatId == null){
                throw new CustomException(ErrorCode.NotFound, "챗봇 대화가 존재하지 않습니다.");
            }
            String typeText = getTypeText(ocid, type, text);
            HistoryListDTO = setAiHistoryConvert.setAiHistoryConvert(Objects.requireNonNull(aiHistoryChatId).getContent());
            content = geminiUtils.getGeminiChatResponse(HistoryListDTO, typeText);

            //DTO에 값 넣기
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content);

            //JPA에 값 넣기
            HistoryListDTO.add(aiChatHistoryDTO);
            aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
            aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO));
            aiHistoryRepository.save(aiHistoryChatId);

            //Response에 값 넣기
            aiChatResponse = aiChatResponseDto(chatId, ocid, Objects.requireNonNull(aiHistoryChatId).getTopic(), content);
        }

        return aiChatResponse;
    }
    public List<Part> setPart(String text){
        return ImmutableList.of(Part.builder().text(text).build());
    }

    @Override
    public Flux<Map<String, Object>> getGuestChat(String chatId, String text) throws HttpException, IOException {
        log.info("chatId: {}, text: {}", chatId, text);
        UserInfoJpaEntity userInfoJpaEntity = userRepository.findById(11L).orElse(null);
        final List<AiChatHistoryDTO>[] HistoryListDTO = new List[]{new ArrayList<>()};
        List<AiChatHistoryDTO> HistoryList = new ArrayList<>();
        final String uuid = UUID.randomUUID().toString();
        final List<String> contentList = new ArrayList<>(); // 결과를 모을 리스트 추가

        return Flux.create(sink -> {
            Flux<String> content;

            if (chatId == null) {
                content = geminiUtils.getGeminiStreamResponse(text);
                String topic = geminiUtils.getGeminiResponse(text + "\n 사용자의 위 질문에 대한 답변으로, 대화의 주제를 명확하고 간결하게 2~3단어 이내로 요약해서 추출해줘.");
                content.subscribe(c -> {
                    guestEmitResponse(sink, uuid, topic, c);
                    contentList.add(c); // 결과를 리스트에 추가
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(null, "null", "null", text, contentConvert(contentList));
                    HistoryList.add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryList));
                    sink.complete();
                });

            } else {
                AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(Objects.requireNonNull(chatId));
                if (aiHistoryChatId == null) {
                    sink.error(new CustomException(ErrorCode.NotFound, "챗봇 대화가 존재하지 않습니다."));
                    return;
                }
                if(!aiHistoryChatId.getUser().equals(userInfoJpaEntity)) {
                    sink.error(new CustomException(ErrorCode.NotFound, "접근 할 수 없습니다."));
                    return;
                }
                HistoryListDTO[0] = setAiHistoryConvert.setAiHistoryConvert(Objects.requireNonNull(aiHistoryChatId).getContent());
                content = geminiUtils.getGeminiStreamChatResponse(HistoryListDTO[0], text);
                content.subscribe(c -> {
                    guestEmitResponse(sink, uuid, aiHistoryChatId.getTopic(), c);
                    contentList.add(c);
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(null, "null", "null", text, contentConvert(contentList));
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    sink.complete();
                });
            }
        });
    }
    private void guestEmitResponse(FluxSink<Map<String, Object>> sink, String uuid, String topic, String content) {
        Map<String, Object> response = new HashMap<>();
        response.put("uuid", uuid);
        response.put("topic", topic);
        response.put("content", content);
        sink.next(response);
    }

    @Override
    public Flux<Map<String, Object>> getStreamChat(Long userId, String chatId, String characterName, String type, String ocid, String text) {
        UserInfoJpaEntity userInfoJpaEntity = userRepository.findById(userId).orElse(null);
        final List<AiChatHistoryDTO>[] HistoryListDTO = new List[]{new ArrayList<>()};
        List<AiChatHistoryDTO> HistoryList = new ArrayList<>();
        final String uuid = UUID.randomUUID().toString();
        final List<String> contentList = new ArrayList<>(); // 결과를 모을 리스트 추가

        return Flux.create(sink -> {
            Flux<String> content;

            if (chatId == null && ocid == null && type == null) {
                content = geminiUtils.getGeminiStreamResponse(text);
                String topic = geminiUtils.getGeminiResponse(text + "\n 사용자의 위 질문에 대한 답변으로, 대화의 주제를 명확하고 간결하게 2~3단어 이내로 요약해서 추출해줘.");
                content.subscribe(c -> {
                    emitResponse(sink, uuid, characterName, type, ocid, topic, c);
                    contentList.add(c); // 결과를 리스트에 추가
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList));
                    HistoryList.add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryList));
                    sink.complete();
                });

            } else if (chatId != null && ocid == null && type == null) {
                AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(Objects.requireNonNull(chatId));

                if (aiHistoryChatId == null) {
                    sink.error(new CustomException(ErrorCode.NotFound, "챗봇 대화가 존재하지 않습니다."));
                    return;
                }

                HistoryListDTO[0] = setAiHistoryConvert.setAiHistoryConvert(Objects.requireNonNull(aiHistoryChatId).getContent());
                content = geminiUtils.getGeminiStreamChatResponse(HistoryListDTO[0], text);
                content.subscribe(c -> {
                    emitResponse(sink, uuid, characterName, type, ocid, aiHistoryChatId.getTopic(), c);
                    contentList.add(c);
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList));
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    sink.complete();
                });

            } else if (chatId == null && ocid != null && type != null) {
                if (!typeCheck(type)) {
                    sink.error(new CustomException(ErrorCode.BadRequest, "잘못된 타입 요청입니다."));
                    return;
                }
                String typeText = getTypeText(ocid, type, text);
                String topic = geminiUtils.getGeminiResponse(text + "\n 이 내용에 대한 간단한 요약으로 30자 이내로 알려줘.");

                content = geminiUtils.getGeminiStreamResponse(typeText);
                content.subscribe(c -> {
                    emitResponse(sink, uuid, characterName, type, ocid, topic, c);
                    contentList.add(c);
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList));
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    sink.complete();
                });

            } else if (chatId != null && ocid != null && type != null) {
                if (!typeCheck(type)) {
                    sink.error(new CustomException(ErrorCode.BadRequest, "잘못된 타입 요청입니다."));
                    return;
                }
                AiHistoryJpaEntity aiHistoryChatId = aiHistoryRepository.findByChatId(Objects.requireNonNull(chatId));

                if (aiHistoryChatId == null) {
                    sink.error(new CustomException(ErrorCode.NotFound, "챗봇 대화가 존재하지 않습니다."));
                    return;
                }
                String typeText = getTypeText(ocid, type, text);
                HistoryListDTO[0] = setAiHistoryConvert.setAiHistoryConvert(Objects.requireNonNull(aiHistoryChatId).getContent());
                content = geminiUtils.getGeminiStreamChatResponse(HistoryListDTO[0], typeText);
                content.subscribe(c -> {
                    emitResponse(sink, uuid, characterName, type, ocid, aiHistoryChatId.getTopic(), c);
                    contentList.add(c);
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList));
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    sink.complete();
                });

            } else {
                sink.complete();
                return;
            }
        });
    }

    private String contentConvert(List<String> contentList) {
        StringBuilder sb = new StringBuilder();
        for (String content : contentList) {
            sb.append(content);
        }
        return sb.toString();
    }

    private void emitResponse(FluxSink<Map<String, Object>> sink, String uuid, String characterName, String type, String ocid, String topic, String content) {
        Map<String, Object> response = new HashMap<>();
        response.put("chatId", uuid);
        response.put("characterName", characterName);
        response.put("type", type);
        response.put("ocid", ocid);
        response.put("topic", topic);
        response.put("content", content);
        sink.next(response);
    }
}
