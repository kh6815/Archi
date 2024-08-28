package com.architecture.archi.db.entity.category;

import com.architecture.archi.common.enumobj.BooleanFlag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Table(name = "CATEGORY")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENTS_CATEGORY_ID")
    private CategoryEntity parentsCategory;

    @OneToMany(mappedBy = "parentsCategory", fetch=FetchType.LAZY)
    private List<CategoryEntity> subCategories = new ArrayList<>();

    @Column(name = "CATEGORY_NAME")
    @NotNull
    private String categoryName;

    @Column(name = "CREATE_USER")
    @NotNull
    private String createUser;

    @Column(name = "UPDATE_USER")
    private String updateUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVE_YN", columnDefinition = "enum('Y', 'N') default 'Y'", nullable = false)
    private BooleanFlag activeYn;

    @Column(name = "CREATED_AT", columnDefinition = "datetime default CURRENT_TIMESTAMP", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public CategoryEntity(CategoryEntity parentsCategory, String categoryName, String createUser){
        this.parentsCategory = parentsCategory;
        this.categoryName = categoryName;
        this.createUser = createUser;
    }

    public void updateCategoryName(String categoryName, String updateUser) {
        this.categoryName = categoryName;
        this.updateUser = updateUser;
    }
    public void updateActive(BooleanFlag activeYn, String updateUser){
        this.activeYn = activeYn;
        this.updateUser = updateUser;
    }
}
