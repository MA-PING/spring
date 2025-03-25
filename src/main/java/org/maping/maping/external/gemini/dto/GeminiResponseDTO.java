package org.maping.maping.external.gemini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.maping.maping.external.gemini.dto.response.CandidatesDTO;
import org.maping.maping.external.gemini.dto.response.CandidatesGoogleDTO;
import org.maping.maping.external.gemini.dto.response.UsageMetadataDTO;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GeminiResponseDTO {
    @JsonProperty("candidates")
    private List<CandidatesDTO> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadataDTO usageMetadata;

    @JsonProperty("modelVersion")
    private String modelVersion;
}
