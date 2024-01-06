package com.book.backend.manager;


import com.book.backend.manager.constant.SparkApiVersion;
import com.book.backend.manager.exception.SparkException;
import com.book.backend.manager.listener.SparkBaseListener;
import com.book.backend.manager.listener.SparkSyncChatListener;
import com.book.backend.manager.model.SparkSyncChatResponse;
import com.book.backend.manager.model.request.SparkRequest;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * XfSparkClient
 *
 * @author briqt
 */
public class SparkClient {

    public String appid;

    public String apiKey;

    public String apiSecret;

    public OkHttpClient client = new OkHttpClient.Builder().build();

    public void chatStream(SparkRequest sparkRequest, SparkBaseListener listener) {
        sparkRequest.getHeader().setAppId(appid);
        listener.setSparkRequest(sparkRequest);

        SparkApiVersion apiVersion = sparkRequest.getApiVersion();
        String apiUrl = apiVersion.getUrl();

        // 构建鉴权url
        String authWsUrl = null;
        try {
            authWsUrl = getAuthUrl(apiUrl).replace("http://", "ws://").replace("https://", "wss://");
        } catch (Exception e) {
            throw new SparkException(500, "构建鉴权url失败", e);
        }
        // 创建请求
        Request request = new Request.Builder().url(authWsUrl).build();
        // 发送请求
        client.newWebSocket(request, listener);
    }

    public SparkSyncChatResponse chatSync(SparkRequest sparkRequest) {
        SparkSyncChatResponse chatResponse = new SparkSyncChatResponse();
        SparkSyncChatListener syncChatListener = new SparkSyncChatListener(chatResponse);
        this.chatStream(sparkRequest, syncChatListener);
        while (!chatResponse.isOk()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }
        }
        Throwable exception = chatResponse.getException();
        if (exception != null) {
            if (!(exception instanceof SparkException)) {
                exception = new SparkException(500, exception.getMessage());
            }
            throw (SparkException) exception;
        }
        return chatResponse;
    }

    /**
     * 获取认证之后的URL
     */
    public String getAuthUrl(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }
}
