package org.maping.maping.repository.user;
import org.checkerframework.checker.units.qual.A;
import org.maping.maping.model.user.UserInfoJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfoJpaEntity, Long> {

    boolean existsByuserName(String userName);

    Optional<UserInfoJpaEntity> findByEmail(String email);


    @EntityGraph(attributePaths = {"userApi"})
    Optional<UserInfoJpaEntity> findById(Long userId);
}
