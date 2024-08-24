package com.architecture.archi.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    /*
    * HTTP
    * */
    // 잘못된 요청
    BAD_REQUEST(400, "Bad Request", HttpStatus.BAD_REQUEST),

    // 잘못된 id 요청
    BAD_ID_REQUEST(400, "Bad ID Request", HttpStatus.BAD_REQUEST),

    // 잘못된 pw 요청
    BAD_PW_REQUEST(400, "Bad PW Request", HttpStatus.BAD_REQUEST),

    // 인증 실패
    UNAUTHORIZED(401, "Unauthorized", HttpStatus.UNAUTHORIZED),

    // 접근 거부
    FORBIDDEN(403, "Forbidden", HttpStatus.FORBIDDEN),

    // 리소스 없음
    NOT_FOUND(404, "Not Found", HttpStatus.NOT_FOUND),

    // 서버 에러
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),

    /*
    * Custom ErrorCode
    * */
    // 서버 점검 중
    SERVER_MAINTENANCE(1000, "SERVER MAINTENANCE", HttpStatus.OK),

    // 인증 서버 에러
    AUTH_SERVER_ERROR(1001, "Auth Server Error", HttpStatus.OK),

    // 알수없는 오류
    UNKNOWN_ERROR(2000, "UNKNOWN ERROR", HttpStatus.OK),

    // 존재하지 않음
    NOT_EXIST(2001, "NOT EXIST", HttpStatus.OK),

    // 삭제됨
    DELETED(2002, "DELETED", HttpStatus.OK),

    // 이미 존재함
    ALREADY_EXIST(2003, "ALREADY EXIST", HttpStatus.OK),

    // 유효하지 않음
    INVALID(2004, "INVALID", HttpStatus.OK),

    // 금지어 불가능
    INVALID_KEYWORD(2005, "INVALID KEYWORD", HttpStatus.OK),

    // 유효하지 않은 토큰
    INVALID_TOKEN(2060, "INVALID TOKEN", HttpStatus.OK),

    // 만료된 토큰
    EXPIRED_TOKEN(2061, "EXPIRED TOKEN", HttpStatus.OK),

    // 접근이 허용되기 전인 JWT 수신
    PREMATURE_TOKEN(2062, "PREMATURE TOKEN", HttpStatus.OK),

    // JWT 권한 claim 검사 실패
    CLAIM_TOKEN(2063, "CLAIM TOKEN", HttpStatus.OK),

    // 구조적인 문제가 있는 JWT
    MALFORMED_TOKEN(2064, "MALFORMED TOKEN", HttpStatus.OK),

    // 시그너처 연산 실패 , 검증 실패
    SIGNATURE_TOKEN(2065, "SIGNATURE TOKEN", HttpStatus.OK),

    // JWT 형식이 애플리케이션에서 원하는 형식과 맞지 않은 경우
    UNSUPPORTED_TOKEN(2066, "UNSUPPORTED TOKEN", HttpStatus.OK),

    // JWT 토큰이 제공된 Exception이 아닌 경우
    UNSUPPORTED_JWT_EXCEPTION(2067, "UNSUPPORTED JWT EXCEPTION", HttpStatus.OK),

    // 잘못된 타입의 토근
    WRONG_TYPE_TOKEN(2068, "WRONG TYPE TOKEN", HttpStatus.OK),

    // Authorization이 없음
    NOT_AUTHORIZATION(2069, "Not Authorization", HttpStatus.OK),

    // 저장된 토큰이 없음
    NO_TOKEN(2070, "NO TOKEN", HttpStatus.OK),

    // 레디스 crud 오류
    ERROR_REDIS(2071, "ERROR_REDIS", HttpStatus.OK)
    ;

    private final Integer resultCode;
    private final String resultMessage;
    private final HttpStatus statusCode;
}
