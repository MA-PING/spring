package org.maping.maping.api.ai.dto.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AiChatResponse {
    private String chatId;
    private String topic;
    private String ocid;
    private String text;

}
