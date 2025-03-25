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
public class CharacterHexaMatrixEquipmentLinkedSkillDTO {
    /**
     * HEXA 스킬 명
     */
    @JsonProperty("hexa_skill_id")
    private String hexaSkillId;
}
