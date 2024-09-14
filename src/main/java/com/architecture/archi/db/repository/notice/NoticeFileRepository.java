package com.architecture.archi.db.repository.notice;

import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.notice.NoticeFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeFileRepository extends JpaRepository<NoticeFileEntity, Long> {
}
