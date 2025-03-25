package org.maping.maping.model.user;

import com.sun.jna.platform.win32.Netapi32Util;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "NAVER_TB")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NaverJpaEntity {
    @Id
    private Long userId;

    @Column(nullable = false, length = 100)
    private String naverKey;

    @OneToOne
    @MapsId // userId가 UserInfo의 userId와 매핑됨을 나타냅니다.
    @JoinColumn(name = "user_id")
    private UserInfoJpaEntity userInfo; // UserInfo를 UserInfoJpaEntity로 수정
}
