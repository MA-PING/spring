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
public class UnionArtifactEffectDTO {
    /**
     * 아티팩트 효과 명
     */
    @JsonProperty("name")
    private String name;

    /**
     * 아티팩트 효과 레벨
     */
    @JsonProperty("level")
    private int level;
}
