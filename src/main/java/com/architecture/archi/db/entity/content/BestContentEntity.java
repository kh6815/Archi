package com.architecture.archi.db.entity.content;

import com.architecture.archi.db.entity.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변경을 자동 감지, AuditingEntityListener -> 생성, 변경 날짜를 자동 세팅(@CreatedDate, @LastModifiedDate)
@NoArgsConstructor // 기본 생성자 세팅
@DynamicInsert // 값이 있는것만 DB에 insert 하여, null이 DB에 들어가는 걸 방지
@DynamicUpdate // 변경된 필드들만 UPDATE 문에 포함되도록 최적화
@Entity
@Table(name = "BEST_CONTENT")
public class BestContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID")
    @NotNull
    private ContentEntity content;

    @Column(name = "CONTENT_RANK", unique = true)
    private int contentRank;

    @Builder
    public BestContentEntity(ContentEntity content, int contentRank) {
        this.content = content;
        this.contentRank = contentRank;
    }
}
