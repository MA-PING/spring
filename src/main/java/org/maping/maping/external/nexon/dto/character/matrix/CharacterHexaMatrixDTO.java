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
public class CharacterHexaMatrixDTO {
    /**
     * 조회 기준일 (KST)
     */
    @JsonProperty("date")
    private String date;

    /**
     * HEXA 코어 정보
     */
    @JsonProperty("character_hexa_core_equipment")
    private List<CharacterHexaMatrixEquipmentDTO> characterHexaCoreEquipment;
}
