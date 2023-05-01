package com.book.backend.common.exception;

public enum CommonError {
    UNKOWN_ERROR("系统内部错误"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空或图书已借出"),
    REQUEST_NULL("请求参数为空"),
    USER_NULL("用户为空");
    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonError( String errMessage) {
        this.errMessage = errMessage;
    }
}
