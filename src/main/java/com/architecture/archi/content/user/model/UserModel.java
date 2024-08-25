package com.architecture.archi.content.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

public class UserModel {
    /**
     * 직접 검증을 하고 싶을 때 사용

    @AssertTrue
    @AssertFalse
    */

    /**
     * 문자열을 다룰 때 사용

    @NotNull // null 불가능
    @NotEmpty // null, 빈 문자열(스페이스 포함X) 불가
    @NotBlank // null, 빈 문자열, 스페이스만 포함한 문자열 불가
    @Size(min=?, max=?) // 최소 길이, 최대 길이 제한
    @Null // null만 가능
    */

    /**
     * 숫자를 다룰 때 사용

    @Positive // 양수만 허용
    @PositiveOrZero // 양수와 0만 허용
    @Negative // 음수만 허용
    @NegativeOrZero // 음수와 0만 허용
    @Min(?) // 최소값 제한
    @Max(?) // 최대값 제한
    */

    /**
     * 정규식 관련

    @Email // 이메일 형식만가능 (기본 제공)
    @Pattern(regexp="?") // 직접 정규식을 쓸 수 있음
    */


    /*
    * request
    * */
    @Getter
    public static class UserSignUpReq{
        @NotBlank(message = "필수값입니다.")
        @Size(min = 3, max = 20, message = "3글자이상 20글자이하입니다.")
        private String id;
        @NotBlank(message = "필수값입니다.")
        @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
        private String pw;
        @NotBlank(message = "필수값입니다.")
        private String pwCheck;
        @NotBlank(message = "필수값입니다.")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "형식이 올바르지 않습니다.")
        private String email;
        @NotBlank(message = "필수값입니다.")
        @Size(min = 2, max = 10, message = "2글자이상 10글자이하입니다.")
        private String nickName;
    }



    /*
    * response
    * */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserSignUpRes{
        private String id;

        @Builder
        public UserSignUpRes(String id) {
            this.id = id;
        }
    }
}
