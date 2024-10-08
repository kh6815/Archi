package com.architecture.archi.content.auth.model;

import com.architecture.archi.common.enumobj.RoleType;
import com.architecture.archi.common.enumobj.SnsType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

public class AuthModel {
    @Getter
    public static class AuthLoginReq{
        @NotBlank(message = "아이디를 확인해주세요.")
        private String id;
        @NotBlank(message = "패스워드를 확인해주세요.")
        private String pw;
    }

    @Getter
    public static class SnsAuthLoginReq{
        @NotNull(message = "sns 타입을 확인해주세요.")
        private SnsType snsType; // provider
        @NotBlank(message = "sns providerId를 확인해주세요.")
        private String providerId;
        @NotBlank(message = "sns 이메일을 확인해주세요.")
        private String email;
        @NotBlank(message = "sns 이름을 확인해주세요.")
        private String name;
    }

    @Getter
    public static class AuthLogoutReq{
        @NotBlank(message = "아이디를 확인해주세요.")
        private String id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthLoginRes{
        private String id;
        private String accessToken;
        private String refreshToken;
        private RoleType role;
        private String imgUrl;
        private String nickName;
    }
}
