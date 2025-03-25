package org.maping.maping.api.ai.dto.response;

import lombok.*;
import reactor.core.publisher.Flux;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AichatStreamResponse {
    private String chatId;
    private String topic;
    private String ocid;
    private Flux<String> text;
}
