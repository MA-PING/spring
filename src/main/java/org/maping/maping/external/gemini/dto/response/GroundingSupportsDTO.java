package org.maping.maping.external.gemini.dto.response;

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
public class GroundingSupportsDTO {
    @JsonProperty("segment")
    private SegmentDTO segment;

    @JsonProperty("groundingChunkIndices")
    private List<Float> groundingChunkIndices;

    @JsonProperty("confidenceScores")
    private List<Float> confidenceScores;
}
