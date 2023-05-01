package com.book.backend.common.exception;

public class VueBookException extends RuntimeException{

    private static final long serialVersionUID = 5565760508056698922L;
    /**
     * 错误信息
     */
    private  String errMessage;
    /**
     * 错误码
     */
    private  int code;
    public VueBookException() {
        super();
    }

    public VueBookException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 新增
     * @param errorCode
     * @param message
     */
    public VueBookException(ErrorCode errorCode,String message){
        super(message);
        this.code = errorCode.getCode();
    }
    public VueBookException(ErrorCode errorCode){
        this.code = errorCode.getCode();
    }

    public static void cast(CommonError commonError){
        throw new VueBookException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new VueBookException(errMessage);
    }

}
