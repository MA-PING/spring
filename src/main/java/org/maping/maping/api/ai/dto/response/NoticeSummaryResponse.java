package org.maping.maping.api.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class NoticeSummaryResponse {
    private String title;
    private String url;
    private LocalDateTime date;
    private String summary;
    private String version;
}
