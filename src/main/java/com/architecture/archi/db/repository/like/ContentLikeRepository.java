package com.architecture.archi.db.repository.like;

import com.architecture.archi.db.entity.like.ContentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentLikeRepository extends JpaRepository<ContentLikeEntity, Long> {
    // userId와 contentId로 LikeEntity 조회
    Optional<ContentLikeEntity> findByUser_IdAndContent_Id(String userId, Long contentId);
}
