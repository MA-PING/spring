package org.maping.maping.api.ai.service;

import autovalue.shaded.org.checkerframework.checker.nullness.qual.Nullable;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.maping.maping.api.ai.dto.response.*;
import org.maping.maping.common.enums.expection.ErrorCode;
import org.maping.maping.common.response.BaseResponse;
import org.maping.maping.exceptions.CustomException;
import org.maping.maping.external.gemini.GEMINIUtils;
import org.maping.maping.external.nexon.NEXONUtils;
import org.maping.maping.external.nexon.dto.character.CharacterBasicDTO;
import org.maping.maping.external.nexon.dto.character.CharacterInfoDTO;
import org.maping.maping.external.nexon.dto.character.ability.CharacterAbilityDTO;
import org.maping.maping.external.nexon.dto.character.itemEquipment.CharacterItemEquipmentDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterLinkSkillDTO;
import org.maping.maping.external.nexon.dto.character.skill.CharacterSkillDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterFinalStatDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterHyperStatDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterHyperStatPresetDTO;
import org.maping.maping.external.nexon.dto.character.stat.CharacterStatDto;
import org.maping.maping.external.nexon.dto.character.symbol.CharacterSymbolEquipmentDTO;
import org.maping.maping.external.nexon.dto.union.UnionArtifactDTO;
import org.maping.maping.external.nexon.dto.union.UnionDTO;
import org.maping.maping.external.nexon.dto.union.UnionRaiderDTO;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.maping.maping.model.ai.QuestionsJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.maping.maping.repository.ai.AiHistoryRepository;
import org.maping.maping.repository.ai.NoticeRepository;
import org.maping.maping.repository.ai.QuestionsRepository;
import org.maping.maping.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
    private final QuestionsRepository questionsRepository;

    public AiServiceImpl(GEMINIUtils geminiUtils, NoticeRepository noticeRepository, NEXONUtils nexonUtils, AiHistoryRepository aiHistoryRepository, UserRepository userRepository, AiHistoryConvert setAiHistoryConvert, QuestionsRepository questionsRepository) {
        this.geminiUtils = geminiUtils;
        this.noticeRepository = noticeRepository;
        this.nexonUtils = nexonUtils;
        this.aiHistoryRepository = aiHistoryRepository;
        this.userRepository = userRepository;
        this.setAiHistoryConvert = setAiHistoryConvert;
        this.questionsRepository = questionsRepository;
    }

    @Override
    public BaseResponse<String> getAiStat(String ocid) throws HttpException, IOException {

        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterStatDto stat = nexonUtils.getCharacterStat(ocid);
        String statString = nexonUtils.statString(stat);

        String text = "기본정보 : {" + basicString + "}, 스탯 : {" + statString + "}\n" +
                "메이플 캐릭터의 기본 정보와 스탯 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 스탯을 다시 알려주지 않아도돼.  서두나 부연 설명은 일절 포함하지 마.\n" +
                "그리고 200자 이내로 대답해줘.";

        return new BaseResponse<>(HttpStatus.OK.value(), "스탯 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiItem(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterItemEquipmentDTO itemEquipment = nexonUtils.getCharacterItemEquip(ocid);
        String itemString = nexonUtils.itemString(itemEquipment);

        String text = "기본정보 : {" + basicString + "}, 장비 : {" + itemString + "}\n" +
                "메이플 캐릭터의 기본 정보와 장비 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 장비를 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";
        return new BaseResponse<>(HttpStatus.OK.value(), "장비 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiUnion(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        UnionDTO union = nexonUtils.getUnion(ocid);
        UnionRaiderDTO unionRaider = nexonUtils.getUnionRaider(ocid);
        String unionString = nexonUtils.unionString(union, unionRaider);

        String text = "기본정보 : {" + basicString + "}, 유니온 : {" + unionString + "}\n" +
                "메이플 캐릭터의 기본 정보와 유니온과 유니온 레이더 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 유니온을 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";

        return new BaseResponse<>(HttpStatus.OK.value(), "유니온 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiArtifact(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        UnionArtifactDTO unionArtifact = nexonUtils.getUnionArtifact(ocid);
        String artifactString = nexonUtils.artifactString(unionArtifact);

        String text = "기본정보 : {" + basicString + "}, 유니온 아티팩트 : {" + artifactString + "}\n" +
                "메이플 캐릭터의 기본 정보와 유니온 아티팩트 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 유니온 아티팩트를 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";
        return new BaseResponse<>(HttpStatus.OK.value(), "유니온 아티팩트 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiSkill(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterSkillDTO skill5 = nexonUtils.getCharacterSkill5(ocid, 5);
        CharacterSkillDTO skill6 = nexonUtils.getCharacterSkill5(ocid, 6);
        CharacterLinkSkillDTO linkSkill = nexonUtils.getCharacterLinkSkill(ocid);
        String skillString = nexonUtils.skillString(skill5, skill6, linkSkill);

        String text = "기본정보 : {" + basicString + "}, 스킬 : {" + skillString + "}\n" +
                "메이플 캐릭터의 기본 정보와 스킬 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 스킬을 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";
        return new BaseResponse<>(HttpStatus.OK.value(), "스킬 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiLinkSkill(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterLinkSkillDTO linkSkill = nexonUtils.getCharacterLinkSkill(ocid);
        String skillString = nexonUtils.LinkskillString(linkSkill);

        String text = "기본정보 : {" + basicString + "}, 스킬 : {" + skillString + "}\n" +
                "메이플 캐릭터의 기본 정보와 스킬 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 스킬을 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";
        return new BaseResponse<>(HttpStatus.OK.value(), "스킬 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiSymbol(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        String basicString = nexonUtils.basicString(basic);
        CharacterSymbolEquipmentDTO symbol = nexonUtils.getCharacterSymbolEquipment(ocid);
        String symbolString = nexonUtils.symbolString(symbol);
        String text = "기본정보 : {" + basicString + "}, 심볼 : {" + symbolString + "}\n" +
                "메이플 캐릭터의 기본 정보와 심볼 정보야 이걸로 같은 레벨과 비교해서 좋은지 나쁜지 평가해줘.\n" +
                "답변할때 내가 알려준 기본정보와 심볼 정보를 다시 알려주지 않아도돼. 서두나 부연 설명은 일절 포함하지 마. \n" +
                "그리고 200자 이내로 대답해줘.";
        return new BaseResponse<>(HttpStatus.OK.value(), "심볼 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
    }

    @Override
    public BaseResponse<String> getAiLevel(String ocid) throws HttpException, IOException {
        CharacterBasicDTO basic = nexonUtils.getCharacterBasic(ocid);
        CharacterBasicDTO[] level = new CharacterBasicDTO[7];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for(int i = 0; i < 7; i++){
            LocalDate date = LocalDate.now().minusDays(7 - i);
            String formattedDate = date.format(formatter);
            level[i] = nexonUtils.getCharacterLevel(ocid, formattedDate);
        }
        String name = basic.getCharacterName();
        String world = basic.getWorldName();
        String text = """
    지시문:
    제공된 메이플스토리 캐릭터의 일주일간 레벨 및 경험치 변화 데이터를 기반으로, 같은 레벨대의 일반적인 성장 속도와 비교하여 해당 캐릭터의 성장률을 평가해 주세요.
    답변은 200자 이내로 간결하게 작성하고, 서두나 부연 설명, 제공된 데이터는 일절 포함하지 마세요. 평가 결과만 제시하세요.

    캐릭터 정보:
    이름: %s,
    월드: %s

    일주일간 레벨 및 경험치 데이터:
    7일 전: 레벨 %d, 경험치 %s
    6일 전: 레벨 %d, 경험치 %s
    5일 전: 레벨 %d, 경험치 %s
    4일 전: 레벨 %d, 경험치 %s
    3일 전: 레벨 %d, 경험치 %s
    2일 전: 레벨 %d, 경험치 %s
    1일 전: 레벨 %d, 경험치 %s
    """.formatted(
                name,
                world,
                level[0].getCharacterLevel(), level[0].getCharacterExp(),
                level[1].getCharacterLevel(), level[1].getCharacterExp(),
                level[2].getCharacterLevel(), level[2].getCharacterExp(),
                level[3].getCharacterLevel(), level[3].getCharacterExp(),
                level[4].getCharacterLevel(), level[4].getCharacterExp(),
                level[5].getCharacterLevel(), level[5].getCharacterExp(),
                level[6].getCharacterLevel(), level[6].getCharacterExp()
        );
        return new BaseResponse<>(HttpStatus.OK.value(), "레벨 정보를 가져왔습니다.", geminiUtils.getGeminiGoogleResponse(text));
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

    public AiChatHistoryDTO historyDto(@Nullable String ocid, @Nullable String characterName, @Nullable String type, String text, String content, String timestamp){
        return AiChatHistoryDTO.builder()
                .ocid(ocid)
                .characterName(characterName)
                .type(type)
                .answer(content)
                .question(text)
                .timestamp(timestamp)
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
    public List<String> getCharacterRecommend(String ocid, Long userId) throws HttpException, IOException {
        UserInfoJpaEntity userInfoJpaEntity = userRepository.findById(userId).orElse(null);
        CharacterInfoDTO info = nexonUtils.getCharacterInfo(ocid, false);
        String text = """
                1. 지시문
                가장 먼저 참고자료에 있는 캐릭터의 정보를 확인해줘.\s
                그리고 그 캐릭터와 비슷한 스펙을 가진 캐릭터를 키우는 유저들이 주로 질문하는 것, 해결하고 싶어하는 것, 검색하는 것들을 구글 검색어 기반으로 조사해줘.\s
                조사한 데이터를기반으로 메이플스토리 유저가 자신의 캐릭터의 상황이나 문제를 개선 및 해결하기 위해 물어볼만한 질문 5개를 생성해줘.\s
                질문은 30자 이내로 작성해줘. 출력은 질문 5개만 번호를 붙여서 나열해줘. 서두나 부연 설명은 일절 포함하지 마.
                2. 참고자료
                캐릭터 정보: { %s }
                전투력 : { %s }
                하이퍼 스탯 : { %s }
                어빌리티 : { %s }
                장착 장비: { %s }
                3. 출력구조
                1. 질문 1
                2. 질문 2
                3. 질문 3
                4. 질문 4
                5. 질문 5
                질문 5개만 위 출력구조 형식으로 출력해줘.""".formatted(nexonUtils.basicFullString(info.getBasic()), getPower(info.getStat().getFinalStat())
                , convertHyperStat(info.getHyperStat()), convertAbility(info.getAbility()), convertItem(info.getItemEquipment()));
        String geminiResponse = geminiUtils.getGeminiTemGoogleResponse(text);
        final String uuid = UUID.randomUUID().toString();
        final String now = String.valueOf(LocalDateTime.now());
        List<AiChatHistoryDTO> HistoryList = new ArrayList<>();
        AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, info.getBasic().getCharacterName(), "recommend", text, geminiResponse, now);
        HistoryList.add(aiChatHistoryDTO);
        saveAiHistory(uuid, userInfoJpaEntity, null, setAiHistoryConvert.getHistoryJson(HistoryList));
        List<String> uuidResponse = new ArrayList<>();
        uuidResponse.add(uuid);
        uuidResponse.addAll(Arrays.asList(converQuestion(geminiResponse)));


        return uuidResponse;
    }

    public String getPower(List<CharacterFinalStatDTO> finalStat){
        String power = "";
        for (CharacterFinalStatDTO stat: finalStat){
            if(stat.getStatName().equals("전투력")){
                power = stat.getStatValue();
            }
        }
        return power;
    }

    public String convertHyperStat(CharacterHyperStatDTO hyperStat) {
        List<CharacterHyperStatPresetDTO> selectedPreset;

        switch (hyperStat.getUsePresetNo()) {
            case "1":
                selectedPreset = hyperStat.getHyperStatPreset1();
                break;
            case "2":
                selectedPreset = hyperStat.getHyperStatPreset2();
                break;
            case "3":
                selectedPreset = hyperStat.getHyperStatPreset3();
                break;
            default:
                selectedPreset = List.of(); // 유효하지 않은 프리셋 번호의 경우 빈 리스트를 반환
                break;
        }

        return selectedPreset.stream()
                .filter(stat -> stat.getStatIncrease() != null)
                .map(stat -> String.format(
                        """
                        {
                            "스탯 종류": "%s",
                            "스탯 레벨": %d,
                            "스탯 상승량": "%s"
                        }""",
                        stat.getStatType(), stat.getStatLevel(), stat.getStatIncrease()
                ))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    public String convertAbility(CharacterAbilityDTO ability) {
        String abilityInfoJson = ability.getAbilityInfo().stream()
                .map(ab -> String.format(
                        """
                        {
                            "어빌리티 등급": "%s",
                            "어빌리티 값": "%s"
                        }""",
                        ab.getAbilityGrade(), ab.getAbilityValue()
                ))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));

        return String.format(
                """
                {
                    "전체 어빌리티 등급": "%s",
                    "어빌리티 목록": %s
                }""",
                ability.getAbilityGrade(),
                abilityInfoJson
        );
    }

    public String convertItem(CharacterItemEquipmentDTO itemEquipment){
        return itemEquipment.getItemEquipment().stream()
                .map(item -> String.format(
                        """
                        {
                            "장비 부위 명": "%s",
                            "장비 명": "%s",
                            "장비 설명": "%s",
                            "장비 최종 옵션 정보": {
                                "STR": %s,
                                "DEX": %s,
                                "INT": %s,
                                "LUK": %s,
                                "최대 HP": %s,
                                "최대 MP": %s,
                                "공격력": %s,
                                "마력": %s,
                                "방어력": %s,
                                "이동속도": %s,
                                "점프력": %s,
                                "보스 몬스터 데미지 증가(%%)": %s,
                                "몬스터 방어율 무시(%%)": %s,
                                "올스탯(%%)": %s,
                                "데미지(%%)": %s,
                                "착용 레벨 감소": %s,
                                "최대 HP(%%)": %s,
                                "최대 MP(%%)": %s
                            }
                        }
                        """,
                        item.getItemEquipmentPart(), item.getItemName(), item.getItemDescription(),
                        item.getItemTotalOption().getStrength(), item.getItemTotalOption().getDexterity(), item.getItemTotalOption().getIntelligence(), item.getItemTotalOption().getLuck(),
                        item.getItemTotalOption().getMaxHp(), item.getItemTotalOption().getMaxMp(), item.getItemTotalOption().getAttackPower(), item.getItemTotalOption().getMagicPower(),
                        item.getItemTotalOption().getArmor(), item.getItemTotalOption().getSpeed(), item.getItemTotalOption().getJump(), item.getItemTotalOption().getBossDamage(),
                        item.getItemTotalOption().getIgnoreMonsterArmor(), item.getItemTotalOption().getAllStat(), item.getItemTotalOption().getDamage(),
                        item.getItemTotalOption().getEquipmentLevelDecrease(), item.getItemTotalOption().getMaxHpRate(), item.getItemTotalOption().getMaxMpRate()
                ))
                .collect(Collectors.joining(",\n", "[\n", "\n]"));
    }

    @Override
    public String[] getUserRecommend() throws HttpException, IOException {
        String text = """
                1. 지시문
                메이플스토리 복귀 유저, 신규 유저가 주로 궁금해하는 것들을 구글 검색어 기준으로 조사하고 이 유저들이 AI에게 물어볼 만한 질문 5가지를 작성해줘.
                질문은 30자 이내로 작성해줘. 출력은 질문 5개만 번호를 붙여서 나열해줘. 서두나 부연 설명은 일절 포함하지 마.
                2. 출력구조
                1. 질문 1
                2. 질문 2
                3. 질문 3
                4. 질문 4
                5. 질문 5
                질문 5개만 위 출력구조 형식으로 출력해줘.""";
        String geminiResponse = geminiUtils.getGeminiGoogleResponse(text);
        return converQuestion(geminiResponse);
    }

    public String[] converQuestion(String geminiResponse){
        String[] lines = geminiResponse.split("\n");

        // 서두가 있으면 startIndex를 1로, 없으면 0으로 설정
        int startIndex = 0;
        if (lines.length > 0 && !lines[0].trim().matches("^\\d+\\..*")) {
            startIndex = 1;
        }

        // 유효한 질문만 담을 리스트
        List<String> validQuestions = new ArrayList<>();

        for (int i = startIndex; i < lines.length; i++) {
            String question = lines[i].replaceAll("^\\d+\\.?\\s*", "").trim();
            // 비어 있지 않은 질문만 리스트에 추가
            if (!question.isEmpty()) {
                validQuestions.add(question);
            }
        }

        // 리스트를 배열로 변환하여 반환
        return validQuestions.toArray(new String[0]);
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
        final String now = String.valueOf(LocalDateTime.now());
        if(chatId == null & ocid == null & type == null) {
            content = geminiUtils.getGeminiGoogleResponse(text);
            String topic = geminiUtils.getGeminiResponse(text + "\n 이 내용에 대해 짧게 요약해줘.");

            //DTO에 값 넣기
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content, now);

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
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content, now);

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
            HistoryListDTO.add(historyDto(ocid, characterName, type, text, content, now));
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
            AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, content, now);

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
        final String now = String.valueOf(LocalDateTime.now());

        return Flux.create(sink -> {
            Flux<String> content;

            if (chatId == null) {
                content = geminiUtils.getGeminiStreamResponse(text);
                String topic = geminiUtils.getGeminiResponse(text + "\n 사용자의 위 질문에 대한 답변으로, 대화의 주제를 명확하고 간결하게 2~3단어 이내로 요약해서 추출해줘.");
                content.subscribe(c -> {
                    guestEmitResponse(sink, uuid, topic, c);
                    contentList.add(c); // 결과를 리스트에 추가
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(null, "null", "null", text, contentConvert(contentList), now);
                    HistoryList.add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryList));
                    saveQuestion(text, userInfoJpaEntity);
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
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(null, "null", "null", text, contentConvert(contentList), now);
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    saveQuestion(text, userInfoJpaEntity);
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
        final String now = String.valueOf(LocalDateTime.now());
        return Flux.create(sink -> {
            Flux<String> content;

            if (chatId == null && ocid == null && type == null) {
                content = geminiUtils.getGeminiStreamResponse(text);
                String topic = geminiUtils.getGeminiResponse(text + "\n 사용자의 위 질문에 대한 답변으로, 대화의 주제를 명확하고 간결하게 2~3단어 이내로 요약해서 추출해줘.");
                content.subscribe(c -> {
                    emitResponse(sink, uuid, characterName, type, ocid, topic, c);
                    contentList.add(c); // 결과를 리스트에 추가
                }, sink::error, () -> {
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList), now);
                    HistoryList.add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryList));
                    saveQuestion(text, userInfoJpaEntity);
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
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList), now);
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    saveQuestion(text, userInfoJpaEntity);
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
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList), now);
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    saveAiHistory(uuid, userInfoJpaEntity, topic, setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    saveQuestion(text, userInfoJpaEntity);
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
                    AiChatHistoryDTO aiChatHistoryDTO = historyDto(ocid, characterName, type, text, contentConvert(contentList), now);
                    HistoryListDTO[0].add(aiChatHistoryDTO);
                    aiHistoryChatId.setUpdatedAt(LocalDateTime.now());
                    aiHistoryChatId.setContent(setAiHistoryConvert.getHistoryJson(HistoryListDTO[0]));
                    aiHistoryRepository.save(aiHistoryChatId);
                    saveQuestion(text, userInfoJpaEntity);
                    sink.complete();
                });

            } else {
                sink.complete();
                return;
            }
        });
    }

    private void saveQuestion(String text, UserInfoJpaEntity user){
        QuestionsJpaEntity entity = QuestionsJpaEntity.builder()
                .questionText(text)
                .user(user)
                .status("PENDING")
                .build();
        questionsRepository.save(entity);
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
