package com.book.backend.common.exception;

public class VueBookException extends RuntimeException{

    private static final long serialVersionUID = 5565760508056698922L;

    private String errMessage;

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

    public static void cast(CommonError commonError){
        throw new VueBookException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new VueBookException(errMessage);
    }
}
