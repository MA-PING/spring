package org.maping.maping.repository.ai;
import org.maping.maping.model.ai.AiHistoryJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AiHistoryRepository extends JpaRepository<AiHistoryJpaEntity, Long> {
    @Query("SELECT a FROM AiHistoryJpaEntity a WHERE a.user = :userId AND a.topic IS NOT NULL ORDER BY a.updatedAt ASC ")
    List<AiHistoryJpaEntity> findByUserId(UserInfoJpaEntity userId);

    AiHistoryJpaEntity findByChatId(String chatId);

    @Modifying
    @Query("DELETE FROM AiHistoryJpaEntity a WHERE a.chatId = :chatId")
    void deleteByChatId(String chatId);

    @Query("SELECT a FROM AiHistoryJpaEntity a WHERE a.user = :userId AND a.chatId = :chatId")
    AiHistoryJpaEntity findByChatIdAndUserId(Long userId, String chatId);
}
