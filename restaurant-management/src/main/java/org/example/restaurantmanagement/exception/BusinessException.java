package org.example.restaurantmanagement.exception;

// 继承 RuntimeException 以便被全局捕获
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认业务错误码
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}