package org.maping.maping.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.maping.maping.model.ai.QuestionsJpaEntity;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "USER_INFO_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserInfoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column
    private String iconic;

    @CreationTimestamp // 생성일자를 자동으로 관리
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @OneToOne(mappedBy = "userInfoTb")
    private UserApiJpaEntity userApiTb;

    @OneToMany(mappedBy = "user")
    private Set<AiHistoryJpaEntity> aiHistoryTbs = new LinkedHashSet<>();
    @OneToOne(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private LocalJpaEntity local; // Local을 LocalJpaEntity로 수정

    @OneToOne(mappedBy = "userInfo", cascade = CascadeType.ALL)
    private NaverJpaEntity naver; // Local을 LocalJpaEntity로 수정

   // @OneToOne(mappedBy = "userInfo", cascade = CascadeType.ALL)
   // private UserApiJpaEntity userApi;


    // UserApi와 One-to-One 관계 추가
    @OneToOne(mappedBy = "userInfoTb")
    private UserApiJpaEntity userApi;

    @Size(max = 255)
    @Column(name = "main_character_ocid")
    private String mainCharacterOcid;

    @Size(max = 255)
    @Column(name = "main_character_name")
    private String mainCharacterName;

    @OneToMany(mappedBy = "user")
    private Set<QuestionsJpaEntity> questionsTbs = new LinkedHashSet<>();


    // getUserApi() 메서드 정의
    public UserApiJpaEntity getUserApi() {
        return userApi;
    }
}
