package com.book.backend.manager.exception;


import com.book.backend.manager.constant.SparkErrorCode;

/**
 * SparkException
 *
 * @author briqt
 */
public class SparkException extends RuntimeException {

    private static final long serialVersionUID = 3053312855506511893L;

    private Integer code;

    private String sid;

    public SparkException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public SparkException(Integer code, String message, Throwable t) {
        super(message, t);
        this.code = code;
    }

    public static SparkException bizFailed(Integer code) {
        String errorMessage = SparkErrorCode.RESPONSE_ERROR_MAP.get(code);
        if (null == errorMessage) {
            errorMessage = "未知的错误码";
        }
        return new SparkException(code, errorMessage);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
