package org.maping.maping.model.search;
import jakarta.persistence.*;
import lombok.*;
import org.maping.maping.model.user.UserInfoJpaEntity;

@Entity
@Table(name = "FAVORITE_TB")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfoJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private CharacterSearchJpaEntity character;

}
