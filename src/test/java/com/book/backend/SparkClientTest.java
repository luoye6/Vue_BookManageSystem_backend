package com.book.backend;

import cn.hutool.core.date.StopWatch;
import com.book.backend.manager.SparkClient;
import com.book.backend.manager.constant.SparkApiVersion;
import com.book.backend.manager.exception.SparkException;
import com.book.backend.manager.listener.SparkConsoleListener;
import com.book.backend.manager.model.SparkMessage;
import com.book.backend.manager.model.SparkSyncChatResponse;
import com.book.backend.manager.model.request.SparkRequest;
import com.book.backend.manager.model.request.function.SparkFunctionBuilder;
import com.book.backend.manager.model.response.SparkResponseFunctionCall;
import com.book.backend.manager.model.response.SparkTextUsage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SparkClientTest
 *
 * @author briqt
 */
public class SparkClientTest {

    /**
     * 客户端实例，线程安全
     */
    SparkClient sparkClient = new SparkClient();

    // 设置认证信息
    {
        sparkClient.appid = "xxxxxxx";
        sparkClient.apiKey = "xxxxx";
        sparkClient.apiSecret = "xxxxxx";
    }

    @Test
    void chatStreamTest() throws InterruptedException {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
        messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                // 指定请求版本，默认使用2.0版本
                .apiVersion(SparkApiVersion.V3_0)
                .build();

        // 使用默认的控制台监听器，流式调用；
        // 实际使用时请继承SparkBaseListener自定义监听器实现
        sparkClient.chatStream(sparkRequest, new SparkConsoleListener());

        Thread.sleep(60000);
    }

    @Test
    void chatSyncTest() throws JsonProcessingException {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
//        messages.add(SparkMessage.userContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
//        messages.add(SparkMessage.assistantContent("好的，这位同学，有什么问题需要李老师为你解答吗？"));
        messages.add(SparkMessage.userContent("查询北京未来七天的天气预报"));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println("提问：" + objectMapper.writeValueAsString(messages));

        try {
            // 同步调用
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
            SparkTextUsage textUsage = chatResponse.getTextUsage();
            stopWatch.stop();
            long total = stopWatch.getTotal(TimeUnit.SECONDS);
            System.out.println("耗时"+total+"\n");
            System.out.println("\n回答：" + chatResponse.getContent());
            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                    + "，回答tokens：" + textUsage.getCompletionTokens()
                    + "，总消耗tokens：" + textUsage.getTotalTokens());
        } catch (SparkException e) {
            System.out.println("发生异常了：" + e.getMessage());
        }
    }

    @Test
    void functionCallTest() throws JsonProcessingException {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent("科大讯飞的最新股票价格是多少"));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 使用functionCall功能版本需要大于等于3.0
                .apiVersion(SparkApiVersion.V3_0)
                // 添加方法，可多次调用添加多个方法
                .addFunction(
                        // 回调时回传的方法名
                        SparkFunctionBuilder.functionName("stockPrice")
                                // 让大模型理解方法意图 方法描述
                                .description("根据公司名称查询最新股票价格")
                                // 方法需要的参数。可多次调用添加多个参数
                                .addParameterProperty("companyName", "string", "公司名称")
                                // 指定以上的参数哪些是必传的
                                .addParameterRequired("companyName").build()
                ).build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println("request：" + objectMapper.writeValueAsString(sparkRequest));

        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        SparkTextUsage textUsage = chatResponse.getTextUsage();
        SparkResponseFunctionCall functionCall = chatResponse.getFunctionCall();

        if (null != functionCall) {
            String functionName = functionCall.getName();
            Map<String, Object> arguments = functionCall.getMapArguments();

            System.out.println("\n收到functionCall：方法名称：" + functionName + "，参数：" + objectMapper.writeValueAsString(arguments));

            // 在这里根据方法名和参数自行调用方法实现

        } else {
            System.out.println("\n回答：" + chatResponse.getContent());
        }

        System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                + "，回答tokens：" + textUsage.getCompletionTokens()
                + "，总消耗tokens：" + textUsage.getTotalTokens());
    }
}
