package com.architecture.archi.common.error;

import lombok.Getter;

@Getter
public class CustomException extends Exception {

    private ExceptionCode exceptionCode = null;
    private Object exceptionData = null;

    public CustomException(ExceptionCode exceptionCode) {
        super(exceptionCode.getResultMessage());
        this.exceptionCode = exceptionCode;
    }

    public CustomException(ExceptionCode exceptionCode, Object exceptionData) {
        super(exceptionCode.getResultMessage());
        this.exceptionCode = exceptionCode;
        this.exceptionData = exceptionData;
    }

//    public CustomException(ExceptionCode exceptionCode, Throwable e) {
//        super(exceptionCode.getResultMessage(), e);
//        this.exceptionCode = exceptionCode;
//    }
//
//    public CustomException(ExceptionCode exceptionCode, Object exceptionData, Throwable e) {
//        super(exceptionCode.getResultMessage(), e);
//        this.exceptionCode = exceptionCode;
//        this.exceptionData = exceptionData;
//    }

}
