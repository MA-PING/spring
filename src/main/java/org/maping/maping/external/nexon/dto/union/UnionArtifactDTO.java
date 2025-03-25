package org.maping.maping.external.nexon.dto.union;

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
public class UnionArtifactDTO {
    /**
     * 조회 기준일
     */
    @JsonProperty("date")
    private String date;

    /**
     * 아티팩트 효과 정보
     */
    @JsonProperty("union_artifact_effect")
    private List<UnionArtifactEffectDTO> unionArtifactEffect;

    /**
     * 아티팩트 크리스탈 정보
     */
    @JsonProperty("union_artifact_crystal")
    private List<UnionArtifactCrystalDTO> unionArtifactCrystal;

    /**
     * 잔여 아티팩트 AP
     */
    @JsonProperty("union_artifact_remain_ap")
    private Integer unionArtifactRemainAp;
}
