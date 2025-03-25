package org.maping.maping.external.nexon.dto.character.matrix;

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
public class CharacterHexaMatrixStatDTO {
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
     * HEXA 스탯 I 코어 정보
     */
    @JsonProperty("character_hexa_stat_core")
    private List<CharacterHexaMatrixStatCoreDTO> characterHexaStatCore;

    /**
     * HEXA 스탯 II 코어 정보
     */
    @JsonProperty("character_hexa_stat_core_2")
    private List<CharacterHexaMatrixStatCoreDTO> characterHexaStatCore2;

    /**
     * 프리셋 HEXA 스탯 I 코어 정보
     */
    @JsonProperty("preset_hexa_stat_core")
    private List<CharacterHexaMatrixStatCoreDTO> presetHexaStatCore;

    /**
     * 프리셋 HEXA 스탯 II 코어 정보
     */
    @JsonProperty("preset_hexa_stat_core_2")
    private List<CharacterHexaMatrixStatCoreDTO> presetHexaStatCore2;
}
