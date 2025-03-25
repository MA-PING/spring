package org.maping.maping.repository.ai;
import org.maping.maping.model.ai.NoticeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<NoticeJpaEntity, Long> {
    @Query("SELECT n FROM NoticeJpaEntity n ORDER BY n.id DESC LIMIT :i")
    List<NoticeJpaEntity> getNotice(int i);

    @Query("SELECT n FROM NoticeJpaEntity n WHERE n.noticeUrl = :url")
    NoticeJpaEntity findByNoticeUrl(String url);
}
