package com.architecture.archi.common.model;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponseModel<T> {
    @Getter @Setter
    public class Header {
        private Integer resultCode;
        private String resultMessage;
    }

    private Header header = new Header();
    private T data = null;
    private Object errorData = null;

    public ApiResponseModel() {
        header.setResultCode(0);
        header.setResultMessage("SUCCESS");
    }

    public ApiResponseModel(T data) {
        header.setResultCode(0);
        header.setResultMessage("SUCCESS");
        this.data = data;
    }

    public ApiResponseModel(Integer resultCode, String resultMessage) {
        header.setResultCode(resultCode);
        header.setResultMessage(resultMessage);
    }

    public void putErrorData(Object errorData) {
        this.errorData = errorData;
    }
}
