package org.maping.maping.external.nexon.dto.character.android;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CharacterAndroidCashItemEquipmentColoringPrismDTO {
    /**
     * 컬러링프리즘 색상 범위
     */
    @JsonProperty("color_range")
    private String colorRange;

    /**
     * 컬러링프리즘 색조
     */
    @JsonProperty("hue")
    private Long hue;

    /**
     * 컬러링프리즘 채도
     */
    @JsonProperty("saturation")
    private Long saturation;

    /**
     * 컬러링프리즘 명도
     */
    @JsonProperty("value")
    private Long value;
}
