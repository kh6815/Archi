package com.architecture.archi.db.entity.content;

import com.architecture.archi.common.BooleanFlag;
import com.architecture.archi.db.entity.category.CategoryEntity;
import com.architecture.archi.db.entity.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변경을 자동 감지, AuditingEntityListener -> 생성, 변경 날짜를 자동 세팅(@CreatedDate, @LastModifiedDate)
@NoArgsConstructor // 기본 생성자 세팅
@DynamicInsert // 값이 있는것만 DB에 insert 하여, null이 DB에 들어가는 걸 방지
@DynamicUpdate // 변경된 필드들만 UPDATE 문에 포함되도록 최적화
@Entity
@Table(name = "CONTENT")
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @NotNull
    private UserEntity user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    @NotNull
    private CategoryEntity category;

    @Column(name = "TITLE")
    @NotNull
    private String title;

    @Column(name = "CONTENT")
    @NotNull
    private String content;

    @Column(name = "LIKE")
    @ColumnDefault("0")
    private Integer like;

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
}
