package com.architecture.archi.content.notification.controller.docs;

import com.architecture.archi.common.error.CustomException;
import com.architecture.archi.common.model.ApiResponseModel;
import com.architecture.archi.config.security.user.CustomUserDetails;
import com.architecture.archi.content.admin.model.AdminModel;
import com.architecture.archi.content.content.model.ContentModel;
import com.architecture.archi.content.notification.model.NotificationModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "알림 API", description = "알림 관련 컨트롤러입니다.")
public interface NotificationControllerDocs {
    @Operation(summary = "클라이언트 알림 구독 엔드포인트 ", description = "클라이언트 알림 구독 엔드포인트 API 입니다")
    @Parameters(value = {
            @Parameter(name = "userId", description = "유저 ID (PathVariable)", required = true, in = ParameterIn.PATH),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "구독 완료", content = @Content(schema = @Schema(implementation = SseEmitter.class)))
    })
    public SseEmitter subscribe(@PathVariable String userId);

    @Operation(summary = "알림 리스트 조회", description = "알림 리스트 조회 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "알림 리스트 조회 완료", content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ApiResponseModel<List<NotificationModel.NotificationListDto>> notificationList(@AuthenticationPrincipal CustomUserDetails userDetails) throws CustomException;

    @Operation(summary = "알림 읽음 수정 ", description = "알림 읽음 수정 API 입니다")
    @Parameters(value = {
            @Parameter(name = "notificationId", description = "알림 ID (PathVariable)", required = true, in = ParameterIn.PATH),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "알림 수정 완료", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ApiResponseModel<Boolean> changeNotificationRead(@PathVariable Long notificationId) throws CustomException;
}
