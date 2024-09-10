package com.architecture.archi.db.repository.content;

import com.architecture.archi.db.entity.content.BestContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BestContentRepository extends JpaRepository<BestContentEntity, Long> {
}
