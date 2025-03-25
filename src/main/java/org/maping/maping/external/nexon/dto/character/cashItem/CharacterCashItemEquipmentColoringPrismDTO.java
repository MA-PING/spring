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
public class CharacterCashItemEquipmentColoringPrismDTO {
    /**
     * 컬러링프리즘 색상 범위
     */
    @JsonProperty("color_range")
    private String colorRange;

    /**
     * 컬러링프리즘 색조
     */
    @JsonProperty("hue")
    private long hue;

    /**
     * 컬러링프리즘 채도
     */
    @JsonProperty("saturation")
    private long saturation;

    /**
     * 컬러링프리즘 명도
     */
    @JsonProperty("value")
    private long value;
}
