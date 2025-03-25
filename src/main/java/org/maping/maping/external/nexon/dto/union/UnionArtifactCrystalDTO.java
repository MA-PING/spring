package org.maping.maping.external.nexon.dto.union;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class UnionArtifactCrystalDTO {
    /**
     * 아티팩트 효과 명
     */
    @JsonProperty("name")
    private String name;

    /**
     * 능력치 유효 여부 (0:유효, 1:유효하지 않음)
     */
    @JsonProperty("validity_flag")
    private String validityFlag;

    /**
     * 능력치 유효 기간(KST)
     */
    @JsonProperty("date_expire")
    private String dateExpire;

    /**
     * 아티팩트 크리스탈 등급
     */
    @JsonProperty("level")
    private int level;

    /**
     * 아티팩트 크리스탈 첫 번째 옵션 명
     */
    @JsonProperty("crystal_option_name_1")
    private String crystalOptionName1;

    /**
     * 아티팩트 크리스탈 두 번째 옵션 명
     */
    @JsonProperty("crystal_option_name_2")
    private String crystalOptionName2;

    /**
     * 아티팩트 크리스탈 세 번째 옵션 명
     */
    @JsonProperty("crystal_option_name_3")
    private String crystalOptionName3;
}
