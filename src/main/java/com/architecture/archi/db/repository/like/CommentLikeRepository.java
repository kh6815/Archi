package com.architecture.archi.db.repository.like;

import com.architecture.archi.db.entity.like.CommentLikeEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByUser_IdAndComment_Id(String userId, Long commentId);
}
