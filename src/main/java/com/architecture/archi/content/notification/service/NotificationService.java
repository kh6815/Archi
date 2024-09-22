package com.architecture.archi.content.notification.service;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.error.ExceptionCode;
import com.architecture.archi.content.notification.model.NotificationModel;
import com.architecture.archi.db.entity.notification.NotificationEntity;
import com.architecture.archi.db.repository.notification.NotificationDao;
import com.architecture.archi.db.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    // 클라이언트를 관리하는 맵 (유저 ID를 키로 사용)
    private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private final static Long DEFAULT_TIMEOUT = 3600000L;

    private final NotificationRepository notificationRepository;
    private final NotificationDao notificationDao;

    public SseEmitter setSseEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT); // 타임아웃 설정
        clients.put(userId, emitter);

        System.out.println("emitter = " + emitter);
        System.out.println("clients = " + clients);

        // 타임아웃 또는 에러 발생 시 클라이언트를 제거
        emitter.onCompletion(() -> clients.remove(userId));
        emitter.onTimeout(() -> clients.remove(userId));
        emitter.onError(e -> clients.remove(userId));

        return emitter;
    }

    @Transactional(rollbackFor = Exception.class)
    // 알림을 특정 유저에게 전송하는 메소드
    public void sendNotification(NotificationEntity notificationEntity) throws IOException {

        NotificationEntity saveEntity = notificationRepository.save(notificationEntity);
        SseEmitter emitter = clients.get(notificationEntity.getReceiver().getId());
        if (emitter != null) {
            emitter.send(SseEmitter.event().name("newComment").data("새로운 댓글"));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    // 알림을 특정 유저에게 전송하는 메소드
    public Boolean changeNotificationRead(Long notificationId) throws CustomException {
        NotificationEntity notificationEntity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_EXIST, "존재하지 않은 알림"));

        notificationEntity.notificationRead();
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    // 알림을 특정 유저에게 전송하는 메소드
    public List<NotificationModel.NotificationListDto> findNotifications(String userId) throws CustomException {
        return notificationDao.findNotificationReadNByReceiverId(userId);
    }
}
