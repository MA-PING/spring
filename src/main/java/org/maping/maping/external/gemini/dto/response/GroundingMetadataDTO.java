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
public class GroundingMetadataDTO {
    @JsonProperty("searchEntryPoint")
    private SearchEntryPointDTO searchEntryPoint;

    @JsonProperty("groundingChunks")
    private List<GroundingChunksDTO> groundingChunks;

    @JsonProperty("groundingSupports")
    private List<GroundingSupportsDTO> groundingSupports;

    @JsonProperty("webSearchQueries")
    private List<String> webSearchQueries;
}
