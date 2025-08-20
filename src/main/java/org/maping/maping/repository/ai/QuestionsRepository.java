package org.maping.maping.repository.ai;
import org.maping.maping.model.ai.QuestionsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface QuestionsRepository extends JpaRepository<QuestionsJpaEntity, Long> {

}
