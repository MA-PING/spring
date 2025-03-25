package org.maping.maping.external.nexon.dto.character.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterLinkSkillDTO {
    /**
     * 조회 기준일 (KST)
     */
    @JsonProperty("date")
    private String date;

    /**
     * 캐릭터 직업
     */
    @JsonProperty("character_class")
    private String characterClass;

    /**
     * 링크 스킬 정보
     */
    @JsonProperty("character_link_skill")
    private List<CharacterSkillInfoDTO> characterLinkSkill;

    /**
     * 링크 스킬 1번 프리셋 정보
     */
    @JsonProperty("character_link_skill_preset_1")
    private List<CharacterSkillInfoDTO> characterLinkSkillPreset1;

    /**
     * 링크 스킬 2번 프리셋 정보
     */
    @JsonProperty("character_link_skill_preset_2")
    private List<CharacterSkillInfoDTO> characterLinkSkillPreset2;

    /**
     * 링크 스킬 3번 프리셋 정보
     */
    @JsonProperty("character_link_skill_preset_3")
    private List<CharacterSkillInfoDTO> characterLinkSkillPreset3;

    /**
     * 내 링크 스킬 정보
     */
    @JsonProperty("character_owned_link_skill")
    private CharacterSkillInfoDTO characterOwnedLinkSkill;

    /**
     * 내 링크 스킬 1번 프리셋 정보
     */
    @JsonProperty("character_owned_link_skill_preset_1")
    private CharacterSkillInfoDTO characterOwnedLinkSkillPreset1;

    /**
     * 내 링크 스킬 2번 프리셋 정보
     */
    @JsonProperty("character_owned_link_skill_preset_2")
    private CharacterSkillInfoDTO characterOwnedLinkSkillPreset2;

    /**
     * 내 링크 스킬 3번 프리셋 정보
     */
    @JsonProperty("character_owned_link_skill_preset_3")
    private CharacterSkillInfoDTO characterOwnedLinkSkillPreset3;
}
