package org.maping.maping.repository.search;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.checkerframework.checker.units.qual.A;
import org.maping.maping.api.character.dto.response.AutocompleteResponse;
import org.maping.maping.model.search.CharacterSearchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CharacterSearchRepository extends JpaRepository<CharacterSearchJpaEntity, Long> {

    Optional<CharacterSearchJpaEntity> findByCharacterName(String characterName);

    @Query("SELECT a FROM CharacterSearchJpaEntity a WHERE a.jaso LIKE CONCAT(:jaso, '%') ORDER BY a.count DESC LIMIT 5")
    List<CharacterSearchJpaEntity> findByJaso(@Param ("jaso") @Size(max = 255) @NotNull String jaso);
}
