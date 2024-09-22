package com.architecture.archi.db.entity.notification;

import com.architecture.archi.common.enumobj.BooleanFlag;
import com.architecture.archi.db.entity.content.ContentEntity;
import com.architecture.archi.db.entity.user.UserEntity;
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

@Getter
@EntityListeners(AuditingEntityListener.class) // 엔티티의 변경을 자동 감지, AuditingEntityListener -> 생성, 변경 날짜를 자동 세팅(@CreatedDate, @LastModifiedDate)
@NoArgsConstructor // 기본 생성자 세팅
@DynamicInsert // 값이 있는것만 DB에 insert 하여, null이 DB에 들어가는 걸 방지
@DynamicUpdate // 변경된 필드들만 UPDATE 문에 포함되도록 최적화
@Entity
@Table(name = "NOTIFICATION")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_USER_ID", nullable = false)
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_USER_ID", nullable = false)
    private UserEntity receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID")
    private ContentEntity content;

    @Column(name = "MESSAGE", columnDefinition = "TEXT")
    @NotNull
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "READ_YN", columnDefinition = "enum('Y', 'N') default 'N'", nullable = false)
    private BooleanFlag readYn;

    @Column(name = "CREATED_AT", columnDefinition = "datetime default CURRENT_TIMESTAMP", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public NotificationEntity(UserEntity sender, UserEntity receiver, ContentEntity content , String message){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.message = message;
    }

    public void notificationRead(){
        this.readYn = BooleanFlag.Y;
    }
}
