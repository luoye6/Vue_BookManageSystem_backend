package com.book.backend;

import cn.hutool.core.date.StopWatch;
import com.aliyun.broadscope.bailian.sdk.AccessTokenClient;
import com.aliyun.broadscope.bailian.sdk.ApplicationClient;
import com.aliyun.broadscope.bailian.sdk.models.BaiLianConfig;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsRequest;
import com.aliyun.broadscope.bailian.sdk.models.CompletionsResponse;
import com.aliyun.broadscope.bailian.sdk.utils.UUIDGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
public class AliAITest {
    public static void main(String[] args) {

    }

    // 单次对话
    @Test
    public void test1() {
        String accessKeyId = "xxxx";
        String accessKeySecret = "xxxxx";
        String agentKey = "xxxxx";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(token);

        String appId = "xxxxxx";
        String prompt = "我想要Vue3的登录页面";
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt);

        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);
        System.out.println(response);
    }

    // 多次会话有记忆功能
    @Test
    public void test2() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String accessKeyId = "xxxxx";
        String accessKeySecret = "xxxx";
        String agentKey = "xxxx";

        AccessTokenClient accessTokenClient = new AccessTokenClient(accessKeyId, accessKeySecret, agentKey);
        String token = accessTokenClient.getToken();

        BaiLianConfig config = new BaiLianConfig()
                .setApiKey(token);

        String appId = "xxxx";
        String prompt = "我想要Vue3的登录页面";
        String sessionId = UUIDGenerator.generate();
        CompletionsRequest request = new CompletionsRequest()
                .setAppId(appId)
                .setPrompt(prompt)
                .setSessionId(sessionId);
        ApplicationClient client = new ApplicationClient(config);
        CompletionsResponse response = client.completions(request);
        System.out.println(response);
        stopWatch.stop();
        System.out.println(stopWatch.getTotal(TimeUnit.SECONDS));



//阿里云百炼也支持调用侧自己维护上下文对话历史, 同时传入sessionId和history，会优先采用调用侧传入的上下文历史
//        List<CompletionsRequest.ChatQaPair> history = new ArrayList<>();
//        CompletionsRequest.ChatQaPair chatQaPair = new CompletionsRequest.ChatQaPair("我想去北京", "北京的天气很不错");
//        CompletionsRequest.ChatQaPair chatQaPair2 = new CompletionsRequest.ChatQaPair("北京有哪些景点", "北京有故宫、长城等");
//        history.add(chatQaPair);
//        history.add(chatQaPair2);
//        request.setHistory(history);
    }
}
