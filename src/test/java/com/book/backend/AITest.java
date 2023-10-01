package com.book.backend;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.book.backend.common.exception.ErrorCode;
import com.book.backend.common.exception.ThrowUtils;
import com.book.backend.manager.GuavaRateLimiterManager;
import com.book.backend.manager.SparkAIManager;
import com.book.backend.pojo.*;
import com.book.backend.pojo.vo.BiResponse;
import com.book.backend.service.*;
import com.google.common.util.concurrent.RateLimiter;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
public class AITest {

    @Resource
    private BooksService booksService;
    @Resource
    private AiIntelligentService aiIntelligentService;
    @Resource
    private AdminsService adminsService;
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private GuavaRateLimiterManager guavaRateLimiterManager;
    @Resource
    private BooksBorrowService booksBorrowService;
    @Resource
    private ChartService chartService;
    public static void main(String[] args) {
       new AITest().test2();
    }
    public void test1(){
        // 调用自己的密钥
        String accessKey = "xxxxx";
        String secretKey = "xxxxxx";
        YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
        // 创建开发请求，设置模型id和消息内容
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1694279925663539202L);
        devChatRequest.setMessage("请问Java怎么能够爬取网站的内容，请给我实例");
        // 发送请求给AI，进行对话
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        // 得到消息
        System.out.println(response.getData());
    }
    @Test
    public void test2(){
        // 调用自己的密钥
        String accessKey = "xxxx";
        String secretKey = "xxxxxx";
        YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
        // 创建开发请求，设置模型id和消息内容
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1694279925663539202L);

        List<Books> list = booksService.list();
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<>();
        String  presetInformation = "请根据数据库内容和游客信息作出推荐,书籍必须是数据库里面有的，可以是一本也可以是多本，根据游客喜欢的信息作出推荐。";

        stringBuilder.append(presetInformation).append("\n").append("数据库内容: ");
        for (Books books : list) {
            if(!hashSet.contains(books.getBookName())){
                hashSet.add(books.getBookName());
                stringBuilder.append(books.getBookName()).append(",");
            }
        }
        stringBuilder.append("\n");
        String customerInformation = "我喜欢关于动物的书籍，请给我推荐图书";
        stringBuilder.append("游客信息: ").append(customerInformation).append("\n");
//        list.forEach(System.out::println);
//        System.out.println(stringBuilder.toString());

        devChatRequest.setMessage(stringBuilder.toString());
        // 发送请求给AI，进行对话
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        // 得到消息
        System.out.println(response.getData());
    }
    @Test
    public void test3(){
        // POST 请求 URL
        String url = "https://api.deepai.org/make_me_a_pizza";

        // 构建请求参数
        String chatStyle = "chat";
        String chatHistory = "[{\"role\":\"user\",\"content\":\"你好\"}]";

        // 使用 HuTool 发送 POST 请求并携带参数
        HttpResponse response = HttpRequest.post(url)
                .form("chat_style", chatStyle)
                .form("chatHistory", chatHistory)
                .execute();

        // 获取响应结果
        String result = response.body();
        System.out.println(result);

    }
    @Test
    public void test4(){
        AiIntelligent aiIntelligent = new AiIntelligent();
        aiIntelligent.setInputMessage("我喜欢神话类的书籍，请给我推荐");
        aiIntelligent.setUserId(1923L);
        boolean save = aiIntelligentService.save(aiIntelligent);
        if(save){
            System.out.println("插入成功");
        }
    }
    @Test
    public void test5(){
        Long userId = 1923L;
        QueryWrapper<AiIntelligent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId!=null&&userId>0,"user_id",userId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 5");
        List<AiIntelligent> list = aiIntelligentService.list(queryWrapper);
        list.forEach(System.out::println);
    }
    @Test
    public void test6() throws InterruptedException {
            String userId = "1923";
            // 每秒允许的最大访问速率为1个许可
            RateLimiter rateLimiter = RateLimiter.create(4);
        for (int i = 0; i < 10; i++) {
            if (rateLimiter.tryAcquire()) {
                // 可以进行处理的代码，表示限流允许通过
                System.out.println(("用户id: " + userId + "请求一个令牌成功"));
            } else {
                // 限流超过了速率限制的处理代码
                System.out.println(("用户id: " + userId + "请求一个令牌失败"));
            }
            Thread.sleep(100);
        }

    }
    @Test
    public void test7(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int timeout = 30; // 超时时间，单位为秒
        Future<String> future = executor.submit(() -> {
            // 在这里执行你的任务
            // 调用自己的密钥
            String accessKey = "xxxxxx";
            String secretKey = "xxxxx";
            YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
            // 创建开发请求，设置模型id和消息内容
            DevChatRequest devChatRequest = new DevChatRequest();
            devChatRequest.setModelId(1694279925663539202L);

            List<Books> list = booksService.list();
            StringBuilder stringBuilder = new StringBuilder();
            HashSet<String> hashSet = new HashSet<>();
            String  presetInformation = "请根据数据库内容和游客信息作出推荐,书籍必须是数据库里面有的，可以是一本也可以是多本，根据游客喜欢的信息作出推荐。";

            stringBuilder.append(presetInformation).append("\n").append("数据库内容: ");
            for (Books books : list) {
                if(!hashSet.contains(books.getBookName())){
                    hashSet.add(books.getBookName());
                    stringBuilder.append(books.getBookName()).append(",");
                }
            }
            stringBuilder.append("\n");
            String customerInformation = "我喜欢编程类的书籍，请给我推荐图书";
            stringBuilder.append("游客信息: ").append(customerInformation).append("\n");
//        list.forEach(System.out::println);
//        System.out.println(stringBuilder.toString());

            devChatRequest.setMessage(stringBuilder.toString());
            // 发送请求给AI，进行对话
            BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
            // 得到消息
            System.out.println(response.getData());
            return "任务执行结果";
        });

        String result;
        try {
            result = future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 超时处理，返回错误信息
            result = "请求超时，请稍后再试";
        } catch (Exception e) {
            // 其他异常处理
            result = "请求异常：" + e.getMessage();
        }
        System.out.println(result);
// 关闭ExecutorService
        executor.shutdown();
    }
    @Test
    public void test8(){
        String name = "测试图表1";
        String goal = "我想要分析用户增长趋势";
        String chartType = "折线图";
        // 这里把逻辑写死，AI功能现在测试阶段
        Admins admin = adminsService.getById(1623L);
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 功能做限制，用户只能调用AI接口聊天，管理员只能调用图表生成，这边用管理员ID
        queryWrapper.eq(UserInterfaceInfo::getUserId,admin.getAdminId());
        UserInterfaceInfo interfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if(interfaceInfo == null){
            System.out.println("该接口已废弃");
        }
        Integer leftNum = interfaceInfo.getLeftNum();
        Integer totalNum = interfaceInfo.getTotalNum();
        if(leftNum<=0){
            System.out.println("AI接口次数不足，请明天再来");
        }
        // 限流判断，每个管理员一个限流器
        boolean limit = guavaRateLimiterManager.doRateLimit(admin.getAdminId());
        if(!limit){
            System.out.println("请求次数过多，请稍后重试");
        }
        // 用户输入
        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}"+"\n";
        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append(prompt);
        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和原始数据，请告诉我分析结论。").append("\n");
        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append("分析目标：").append(userGoal).append("\n");
        userInput.append("分析需求：");
        String csvData =
                "分析网站用户的增长情况\n" +
                "原始数据：\n" +
                "日期,用户数\n" +
                "1号,10\n" +
                "2号,20\n" +
                "3号,30";
        userInput.append(csvData).append("\n");
        String result;
        SparkAIManager sparkAIManager = new SparkAIManager(1923 + "", false);
        try {
            result = sparkAIManager.sendMessageAndGetResponse(userInput.toString(),20);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            System.out.println("AI生成错误，请稍后重试");
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }

        Pattern pattern = Pattern.compile("option = ([^;]+);");
        Matcher matcher = pattern.matcher(splits[1]);
        String genChart = "";
        if (matcher.find()) {
           genChart = matcher.group(1);
        }else{
            System.out.println("AI生成错误，请稍后重试");
        }
        String genResult = splits[2].split("}")[1].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart.trim());
        chart.setGenResult(genResult);
        chart.setAdminId(1923L);
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        // 更新调用接口的次数 剩余接口调用次数-1.总共调用次数+1
        interfaceInfo.setLeftNum(leftNum-1);
        interfaceInfo.setTotalNum(totalNum+1);
        boolean update = userInterfaceInfoService.updateById(interfaceInfo);
        if(!update){
            System.out.println("调用接口信息失败");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        System.out.println("图表生成成功");
    }
    @Test
    public void test9(){
        List<BooksBorrow> list = booksBorrowService.list();
        list.stream().map(BooksBorrow::getBorrowDate).forEach(System.out::println);
        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
                "分析需求：\n" +
                "{数据分析的需求或者目标}\n" +
                "原始数据：\n" +
                "{csv格式的原始数据，用,作为分隔符}\n" +
                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
                "【【【【【\n" +
                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
                "【【【【【\n" +
                "{明确的数据分析结论、越详细越好，不要生成多余的注释}"+"\n";
        System.out.println(prompt);
    }
}
