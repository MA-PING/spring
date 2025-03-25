package org.maping.maping.model.ai;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.maping.maping.model.user.UserInfoJpaEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "AI_HISTORY_TB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiHistoryJpaEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfoJpaEntity user;

    @Id
    @Size(max = 36)
    @Column(name = "chat_id", nullable = false, length = 36)
    private String chatId;

    @Size(max = 255)
    @Column(name = "topic")
    private String topic;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
