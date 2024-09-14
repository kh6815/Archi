package com.architecture.archi.content.auth.model;

import com.architecture.archi.common.enumobj.RoleType;
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
    }
}
