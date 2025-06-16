package org.maping.maping.model.search;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "CHARACTER_SEARCH_TB")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CharacterSearchJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "character_name", nullable = false, length = 100)
    private String characterName;

    @NotNull
    @Column(name = "character_level", nullable = false)
    private Long characterLevel;

    @Size(max = 100)
    @NotNull
    @Column(name = "world_name", nullable = false, length = 100)
    private String worldName;

    @Size(max = 100)
    @NotNull
    @Column(name = "character_class", nullable = false, length = 100)
    private String characterClass;


    @Column(name = "image", columnDefinition = "TEXT", nullable = false)
    private String image;


    @Size(max = 255)
    @NotNull
    @Column(name = "jaso", nullable = false)
    private String jaso;

    @NotNull
    @Column(name = "count", nullable = false)
    private Integer count;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Size(max = 150)
    @NotNull
    @Column(name = "ocid", nullable = false, length = 150)
    private String ocid;

    @Size(max = 128)
    @Column(name = "guild", length = 128)
    private String guild;

}
