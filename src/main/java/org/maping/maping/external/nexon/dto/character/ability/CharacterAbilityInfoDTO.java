package org.maping.maping.external.nexon.dto.character.ability;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterAbilityInfoDTO {
    /**
     * 어빌리티 번호
     */
    @JsonProperty("ability_no")
    private String abilityNo;

    /**
     * 어빌리티 등급
     */
    @JsonProperty("ability_grade")
    private String abilityGrade;

    /**
     * 어빌리티 옵션 및 수치
     */
    @JsonProperty("ability_value")
    private String abilityValue;
}
