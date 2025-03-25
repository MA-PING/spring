package org.maping.maping.repository.user;

import org.maping.maping.model.user.NaverJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NaverRepository extends JpaRepository<NaverJpaEntity, Long> {

}
