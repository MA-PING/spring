package org.maping.maping.repository.user;

import org.maping.maping.model.user.LocalJpaEntity;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalRepository extends JpaRepository<LocalJpaEntity, Long> {

    Optional<LocalJpaEntity> findByEmail(String email);

}
