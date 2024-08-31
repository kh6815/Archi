package com.architecture.archi.db.repository.content;

import com.architecture.archi.db.entity.content.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {
    List<ContentEntity> findByIdIn(List<Long> id);
}
