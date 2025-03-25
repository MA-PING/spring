package org.maping.maping.external.gemini.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CandidatesGoogleDTO {
    @JsonProperty("content")
    private ContentDTO content;

    @JsonProperty("finishReason")
    private String finishReason;

    @JsonProperty("index")
    private int index;

    @JsonProperty("groundingMetadata")
    private GroundingMetadataDTO groundingMetadata;
}
