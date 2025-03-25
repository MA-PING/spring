package org.maping.maping.api.ai.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AiHistoryResponse {
    private String chatId;
    private String topic;
    private LocalDateTime dateTime;
}
