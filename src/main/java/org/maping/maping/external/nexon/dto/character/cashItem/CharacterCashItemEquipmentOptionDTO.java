package org.maping.maping.external.nexon.dto.character.cashItem;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterCashItemEquipmentOptionDTO {
    /**
     * 옵션 타입
     */
    @JsonProperty("option_type")
    private String optionType;

    /**
     * 옵션 값
     */
    @JsonProperty("option_value")
    private String optionValue;
}
