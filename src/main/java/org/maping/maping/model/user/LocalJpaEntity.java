package org.maping.maping.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.maping.maping.model.BaseTime;

@Entity
@Table(name = "LOCAL_TB")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalJpaEntity extends BaseTime {

    @Id
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne
    @MapsId // userId가 UserInfo의 userId와 매핑됨을 나타냅니다.
    @JoinColumn(name = "user_id")
    private UserInfoJpaEntity userInfo; // UserInfo를 UserInfoJpaEntity로 수정
}
