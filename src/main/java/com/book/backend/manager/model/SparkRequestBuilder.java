package com.book.backend.manager.model;


import com.book.backend.manager.constant.SparkApiVersion;
import com.book.backend.manager.model.request.*;
import com.book.backend.manager.model.request.function.SparkRequestFunctionMessage;
import com.book.backend.manager.model.request.function.SparkRequestFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SparkRequestBuilder
 *
 * @author briqt
 */
public class SparkRequestBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SparkRequestBuilder.class);

    private final SparkRequest sparkRequest;

    public SparkRequestBuilder() {
        sparkRequest = new SparkRequest();

        // header
        sparkRequest.setHeader(new SparkRequestHeader());

        // parameter
        sparkRequest.setParameter(new SparkRequestParameter(new SparkChatParameter()));

        // payload
        sparkRequest.setPayload(new SparkRequestPayload(new SparkRequestMessage(new ArrayList<>())));
    }

    public SparkRequest build() {
        SparkApiVersion apiVersion = sparkRequest.getApiVersion();
        if (sparkRequest.getPayload().getFunctions() != null && (apiVersion == SparkApiVersion.V2_0 || apiVersion == SparkApiVersion.V1_5)) {
            logger.warn("apiVersion is {}, this version does not support functions", apiVersion.getVersion());
        }
        sparkRequest.getParameter().getChat().setDomain(apiVersion.getDomain());
        return sparkRequest;
    }

    /**
     * 消息列表，如果想获取结合上下文的回答，需要将历史问答信息放在一起<br/>
     * 必传，消息列表总tokens不能超过8192
     */
    public SparkRequestBuilder messages(List<SparkMessage> messages) {
        sparkRequest.getPayload().getMessage().setText(messages);
        return this;
    }

    /**
     * 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高<br/>
     * 非必传,取值为[0,1],默认为0.5
     */
    public SparkRequestBuilder temperature(Double temperature) {
        sparkRequest.getParameter().getChat().setTemperature(temperature);
        return this;
    }

    /**
     * 模型回答的tokens的最大长度<br/>
     * 非必传,取值为[1,4096],默认为2048
     */
    public SparkRequestBuilder maxTokens(Integer maxTokens) {
        sparkRequest.getParameter().getChat().setMaxTokens(maxTokens);
        return this;
    }

    /**
     * 从k个候选中随机选择⼀个（⾮等概率）<br/>
     * 非必传,取值为[1,6],默认为4
     */
    public SparkRequestBuilder topK(Integer topK) {
        sparkRequest.getParameter().getChat().setTopK(topK);
        return this;
    }

    /**
     * 每个用户的id，用于区分不同用户<br/>
     * 非必传，最大长度32
     */
    public SparkRequestBuilder uid(String uid) {
        sparkRequest.getHeader().setUid(uid);
        return this;
    }

    /**
     * 用于关联用户会话<br/>
     * 非必传,需要保障用户下的唯一性
     */
    public SparkRequestBuilder chatId(String chatId) {
        sparkRequest.getParameter().getChat().setChatId(chatId);
        return this;
    }

    /**
     * 覆盖默认的对话参数
     */
    public SparkRequestBuilder chatParameter(SparkChatParameter chatParameter) {
        sparkRequest.getParameter().setChat(chatParameter);
        return this;
    }

    /**
     * 指定apiVersion<br/>
     * 非必传，默认使用2.0版本
     */
    public SparkRequestBuilder apiVersion(SparkApiVersion apiVersion) {
        sparkRequest.setApiVersion(apiVersion);
        sparkRequest.getParameter().getChat().setDomain(apiVersion.getDomain());
        return this;
    }

    /**
     * 新增function
     */
    public SparkRequestBuilder addFunction(SparkRequestFunctionMessage function) {
        SparkRequestFunctions functions = sparkRequest.getPayload().getFunctions();
        if (null == functions) {
            functions = new SparkRequestFunctions(new ArrayList<>());
            sparkRequest.getPayload().setFunctions(functions);
        }
        functions.getText().add(function);
        return this;
    }
}
