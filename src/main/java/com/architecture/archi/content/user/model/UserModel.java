package com.architecture.archi.content.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class UserModel {
    /*
    * request
    * */
    @Getter
    public static class UserSignUpReq{
        @NotBlank
        @Size(min = 3, max = 20)
        private String id;
        @NotBlank
        @Size(min = 3, max = 20)
        private String pw;
        @Email
        private String email;
        @NotBlank
        @Size(min = 2, max = 10)
        private String nickName;
    }



    /*
    * response
    * */

    @Builder
    public static class UserSignUpRes{
        private String id;
    }
}
