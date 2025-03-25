package org.maping.maping.api.ai.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AiChatHistoryDetailResponse {
    private String chatId;
    private String topic;
    private LocalDateTime dateTime;
    private List<AiChatHistoryDTO> history;
}
