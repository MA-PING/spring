    package org.maping.maping.model.user;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "USER_API_TB")
    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public class UserApiJpaEntity {

        @Id
        @Column(name = "user_id", nullable = false)
        private Long id;

        @MapsId
        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private UserInfoJpaEntity userInfoTb;

        @Size(max = 255)
        @NotNull
        @Column(name = "user_api_info", nullable = false)
        private String userApiInfo;

        // userInfo 필드를 추가합니다.
        //@OneToOne(fetch = FetchType.LAZY)
        //@JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
        //private UserInfoJpaEntity userInfo;

        @Version  // 낙관적 락을 위한 버전 필드 추가
        private Integer version;

    }
