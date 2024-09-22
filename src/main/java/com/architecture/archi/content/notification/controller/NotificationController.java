package com.architecture.archi.content.notification.controller;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.notification.controller.docs.NotificationControllerDocs;
import com.architecture.archi.content.notification.model.NotificationModel;
import com.architecture.archi.content.notification.service.NotificationService;
import com.architecture.archi.db.entity.notification.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;
    // 클라이언트가 알림을 구독하는 엔드포인트
    @GetMapping("/subscribe/{userId}")
    public SseEmitter subscribe(@PathVariable String userId) {
        return notificationService.setSseEmitter(userId);
    }

    @GetMapping("/list")
    public ApiResponseModel<List<NotificationModel.NotificationListDto>> notificationList(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException {
        return new ApiResponseModel<>(notificationService.findNotifications(userDetails.getUsername()));
    }

    @PatchMapping("/read/{notificationId}")
    public ApiResponseModel<Boolean> changeNotificationRead(@PathVariable Long notificationId) throws CustomException {
        return new ApiResponseModel<>(notificationService.changeNotificationRead(notificationId));
    }
}
