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
public class SearchEntryPointDTO {
    @JsonProperty("renderedContent")
    private String renderedContent;
}
