package com.architecture.archi.db.entity.content;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.db.entity.file.FileEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor // 기본 생성자 세팅
@DynamicInsert // 값이 있는것만 DB에 insert 하여, null이 DB에 들어가는 걸 방지
@DynamicUpdate // 변경된 필드들만 UPDATE 문에 포함되도록 최적화
@Entity
@Table(name = "CONTENT_FILE")
public class ContentFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID", nullable = false)
    private ContentEntity content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID", nullable = false)
    private FileEntity file;

    @Enumerated(EnumType.STRING)
    @Column(name = "DEL_YN", columnDefinition = "enum('Y', 'N') default 'N'", nullable = false)
    private BooleanFlag delYn;

    @Builder
    public ContentFileEntity(ContentEntity content, FileEntity file) {
        this.content = content;
        this.file = file;
    }

    public void delete() {
        this.delYn = BooleanFlag.Y;
    }
}
