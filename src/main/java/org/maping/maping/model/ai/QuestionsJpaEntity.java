package org.maping.maping.model.ai;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.maping.maping.model.user.UserInfoJpaEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "QUESTIONS_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuestionsJpaEntity {


    @Id
    @Column(name = "question_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Lob
    @Column(name = "question_text", nullable = false)
    private String questionText;

    @NotNull
    @CreationTimestamp
    @Column(name = "asked_at", nullable = false)
    private LocalDateTime askedAt;

    @NotNull
    @Lob
    @Column(name = "status", nullable = false)
    @ColumnDefault("'PENDING'")
    private String status;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfoJpaEntity user;

}
