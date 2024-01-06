package com.book.backend.manager.listener;

import com.book.backend.manager.exception.SparkException;
import com.book.backend.manager.model.SparkMessage;
import com.book.backend.manager.model.request.SparkRequest;
import com.book.backend.manager.model.response.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SparkBaseListener
 *
 * @author briqt
 */
public class SparkBaseListener extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(SparkBaseListener.class);

    private SparkRequest sparkRequest;

    public ObjectMapper objectMapper;

    public SparkBaseListener() {
        objectMapper = new ObjectMapper();
        // 排除值为null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置全局忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 收到回答时会调用此方法
     *
     * @param content       回答内容
     * @param usage         tokens消耗统计
     * @param status        会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果，当status为2时，webSocket连接会自动关闭
     * @param sparkRequest  本次会话的请求参数
     * @param sparkResponse 本次回调的响应数据
     * @param webSocket     本次会话的webSocket连接
     */
    public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {
        // 重写此方法，实现业务逻辑
    }

    /**
     * 收到functionCall调用此方法
     *
     * @param functionCall  functionCall
     * @param usage         tokens消耗统计
     * @param status        会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果，当status为2时，webSocket连接会自动关闭
     * @param sparkRequest  本次会话的请求参数
     * @param sparkResponse 本次回调的响应数据
     * @param webSocket     本次会话的webSocket连接
     */
    public void onFunctionCall(SparkResponseFunctionCall functionCall, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {
        // 重写此方法，实现业务逻辑
    }

    @Override
    public final void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        // 发送消息
        String requestJson = null;
        try {
            requestJson = objectMapper.writeValueAsString(sparkRequest);
        } catch (JsonProcessingException e) {
            throw new SparkException(400, "请求数据 SparkRequest 序列化失败", e);
        }
        webSocket.send(requestJson);
    }

    @Override
    public final void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        SparkResponse sparkResponse;

        // 解析响应
        try {
            sparkResponse = objectMapper.readValue(text, SparkResponse.class);
        } catch (JsonProcessingException e) {
            webSocket.close(1000, "");
            throw new SparkException(500, "响应数据 SparkResponse 解析失败：" + text, e);
        }
        SparkResponseHeader header = sparkResponse.getHeader();
        if (null == header) {
            webSocket.close(1000, "");
            throw new SparkException(500, "响应数据不完整 SparkResponse.header为null，完整响应：" + text);
        }

        // 业务状态判断
        Integer code = header.getCode();
        if (0 != code) {
            webSocket.close(1000, "");
            throw SparkException.bizFailed(code);
        }

        // 回答文本
        SparkResponseChoices choices = sparkResponse.getPayload().getChoices();
        List<SparkMessage> messages = choices.getText();
        StringBuilder stringBuilder = new StringBuilder();
        SparkResponseFunctionCall functionCall = null;

        SparkResponseUsage usage = sparkResponse.getPayload().getUsage();
        Integer status = header.getStatus();

        for (SparkMessage message : messages) {
            if (message.getFunction_call() != null) {
                functionCall = message.getFunction_call();
                break;
            }
            stringBuilder.append(message.getContent());
        }
        if (functionCall != null) {
            this.onFunctionCall(functionCall, usage, status, sparkRequest, sparkResponse, webSocket);
        } else {
            String content = stringBuilder.toString();
            this.onMessage(content, usage, status, sparkRequest, sparkResponse, webSocket);
        }

        // 最后一条结果，关闭连接
        if (2 == status) {
            webSocket.close(1000, "");
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        logger.error("讯飞星火api发生异常：", t);
    }

    public SparkRequest getSparkRequest() {
        return sparkRequest;
    }

    public void setSparkRequest(SparkRequest sparkRequest) {
        this.sparkRequest = sparkRequest;
    }
}
