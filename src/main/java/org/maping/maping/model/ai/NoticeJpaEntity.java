package org.maping.maping.model.ai;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "NOTICE_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NoticeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "notice_part", nullable = false)
    private String noticePart;

    @Size(max = 255)
    @NotNull
    @Column(name = "notice_title", nullable = false)
    private String noticeTitle;

    @NotNull
    @Column(name = "notice_date", nullable = false)
    private LocalDateTime noticeDate;


    @Size(max = 255)
    @NotNull
    @Column(name = "notice_url", nullable = false)
    private String noticeUrl;

    @Size(max = 15)
    @Column(name = "version", length = 15)
    private String version;


    @NotNull
    @Lob
    @Column(name = "notice_summary", nullable = false, columnDefinition = "TEXT")
    private String noticeSummary;

}
