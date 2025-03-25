package org.maping.maping.api.ai.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AiChatRequest {
    private String chatId;
    private String ocid;
    private String characterName;
    private String type;
    private String text;
}
