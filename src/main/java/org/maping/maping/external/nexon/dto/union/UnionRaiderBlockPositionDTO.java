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
public class UnionRaiderBlockPositionDTO {
    /**
     * 블록 X좌표
     */
    @JsonProperty("x")
    private long x;

    /**
     * 블록 Y좌표
     */
    @JsonProperty("y")
    private long y;
}
