package com.architecture.archi.db.entity.comment;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.like.CommentLikeEntity;
import com.architecture.archi.db.entity.like.ContentLikeEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변경을 자동 감지, AuditingEntityListener -> 생성, 변경 날짜를 자동 세팅(@CreatedDate, @LastModifiedDate)
@NoArgsConstructor // 기본 생성자 세팅
@DynamicInsert // 값이 있는것만 DB에 insert 하여, null이 DB에 들어가는 걸 방지
@DynamicUpdate // 변경된 필드들만 UPDATE 문에 포함되도록 최적화
@Entity
@Table(name = "COMMENT")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 부모 댓글을 가리키는 필드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private CommentEntity parent;

    // 자식 댓글들을 가리키는 필드
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<CommentEntity> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @NotNull
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID")
    @NotNull
    private ContentEntity content;

    @Column(name = "COMMENT", columnDefinition = "TEXT")
    @NotNull
    private String comment;

    @Column(name = "IS_CONTENT_AUTHOR")
    @NotNull
    private Boolean isContentAuthor;

    @OneToMany(mappedBy = "comment", fetch=FetchType.LAZY)
    private List<CommentLikeEntity> commentLikes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "DEL_YN", columnDefinition = "enum('Y', 'N') default 'N'", nullable = false)
    private BooleanFlag delYn;

    @Column(name = "CREATED_AT", columnDefinition = "datetime default CURRENT_TIMESTAMP", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public CommentEntity(CommentEntity parent, UserEntity user, ContentEntity content, String comment, Boolean isContentAuthor){
        this.parent = parent;
        this.user = user;
        this.content = content;
        this.comment = comment;
        this.isContentAuthor = isContentAuthor;
    }

    public void updateComment(String comment){
        this.comment = comment;
    }

    public void deleteComment(){
        this.comment = "삭제된 댓글입니다.";
        this.delYn = BooleanFlag.Y;
    }
}
