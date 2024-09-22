package com.architecture.archi.db.repository.notification;

import com.architecture.archi.db.entity.notification.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
