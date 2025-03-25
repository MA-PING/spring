package org.maping.maping.api.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AiChatHistoryDTO {

    @JsonProperty("ocid")
    private String ocid;

    @JsonProperty("character_name")
    private String characterName;

    @JsonProperty("type")
    private String type;

    @JsonProperty("question")
    private String question;

    @JsonProperty("answer")
    private String answer;
}
