package com.book.backend;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
public class OpenAPITest {
    @Test
    public void testOpenApi() {
        String txt = "Hello";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> dataList = new ArrayList<>();
        dataList.add(new HashMap<String, String>() {{
            put("role", "user");
            put("content", txt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            String body = HttpRequest.post("https://api.openai.com/v1/chat/completions").header("Authorization", "xxxxx").header("Content-Type", "application/json").body(JSONUtil.toJsonStr(paramMap)).execute().body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
            System.out.println(message);
        }catch (Exception e){
            System.out.println(e);
        }

    }
    @Test
    public void test2(){
      String str = "{前端 Echarts V5 的 option 配置对象js代码}\n" +
              "var option = {\n" +
              "    title: {\n" +
              "        text: '用户增长趋势'\n" +
              "    },\n" +
              "    tooltip: {},\n" +
              "    legend: {\n" +
              "        data:['用户数']\n" +
              "    },\n" +
              "    xAxis: {\n" +
              "        data: [\"1号\",\"2号\",\"3号\"]\n" +
              "    },\n" +
              "    yAxis: {},\n" +
              "    series: [{\n" +
              "        name: '用户数',\n" +
              "        type: 'line',\n" +
              "        data: [10, 20, 30]\n" +
              "    }]\n" +
              "};";
        Pattern pattern = Pattern.compile("var option = ([^;]+);");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            String match = matcher.group(1);
            System.out.println(match);
        }
    }
}

