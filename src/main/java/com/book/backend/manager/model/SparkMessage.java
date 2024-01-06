package com.book.backend.manager.model;


import com.book.backend.manager.constant.SparkMessageRole;
import com.book.backend.manager.model.response.SparkResponseFunctionCall;

/**
 * 消息
 *
 * @author briqt
 */
public class SparkMessage {

    /**
     * 角色
     */
    private String role;

    /**
     * 内容类型
     */
    private String content_type;

    /**
     * 函数调用
     */
    private SparkResponseFunctionCall function_call;

    /**
     * 内容
     */
    private String content;

    /**
     * 响应时独有，请求入参请忽略
     */
    private String index;

    /**
     * 创建用户消息
     *
     * @param content 内容
     */
    public static SparkMessage userContent(String content) {
        return new SparkMessage(SparkMessageRole.USER, content);
    }

    /**
     * 创建机器人消息
     *
     * @param content 内容
     */
    public static SparkMessage assistantContent(String content) {
        return new SparkMessage(SparkMessageRole.ASSISTANT, content);
    }

    /**
     * 创建system指令
     * @param content 内容
     */
    public static SparkMessage systemContent(String content) {
        return new SparkMessage(SparkMessageRole.SYSTEM, content);
    }

    public SparkMessage() {
    }

    public SparkMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIndex() {
        return index;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public SparkResponseFunctionCall getFunction_call() {
        return function_call;
    }

    public void setFunction_call(SparkResponseFunctionCall function_call) {
        this.function_call = function_call;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
