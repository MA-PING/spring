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
public class CharacterSkillDTO {
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
     * 스킬 전직 차수
     */
    @JsonProperty("character_skill_grade")
    private String characterSkillGrade;

    /**
     * 스킬 정보 리스트
     */
    @JsonProperty("character_skill")
    private List<CharacterSkillInfoDTO> characterSkill;
}
