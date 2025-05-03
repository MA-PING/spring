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
public class CharacterAndroidEquipmentFaceDTO {
    /**
     * 안드로이드 성형 명
     */
    @JsonProperty("face_name")
    private String faceName;

    /**
     * 안드로이드 성형 베이스 컬러
     */
    @JsonProperty("base_color")
    private String baseColor;

    /**
     * 안드로이드 성형 믹스 컬러
     */
    @JsonProperty("mix_color")
    private String mixColor;

    /**
     * 안드로이드 성형 믹스 컬러의 염색 비율
     */
    @JsonProperty("mix_rate")
    private String mixRate;
}
