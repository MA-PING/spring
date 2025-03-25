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
public class UnionRaiderPresetDTO {
    /**
     * 유니온 공격대원 효과
     */
    @JsonProperty("union_raider_stat")
    private List<String> unionRaiderStat;

    /**
     * 유니온 공격대 점령 효과
     */
    @JsonProperty("union_occupied_stat")
    private List<String> unionOccupiedStat;

    /**
     * 유니온 공격대 배치
     */
    @JsonProperty("union_inner_stat")
    private List<UnionRaiderInnerStatDTO> unionInnerStat;

    /**
     * 유니온 블록 정보
     */
    @JsonProperty("union_block")
    private List<UnionRaiderBlockDTO> unionBlock;
}
