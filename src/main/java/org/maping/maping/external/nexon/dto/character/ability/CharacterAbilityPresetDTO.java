package org.maping.maping.external.nexon.dto.character.ability;

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
public class CharacterAbilityPresetDTO {
    /**
     * 프리셋의 어빌리티 등급
     */
    @JsonProperty("ability_preset_grade")
    private String abilityPresetGrade;

    /**
     * 프리셋의 어빌리티 정보
     */
    @JsonProperty("ability_info")
    private List<CharacterAbilityInfoDTO> abilityInfo;
}
