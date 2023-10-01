package com.book.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.R;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.manager.GuavaRateLimiterManager;
import com.book.backend.manager.SparkAIManager;
import com.book.backend.mapper.AiIntelligentMapper;
import com.book.backend.pojo.AiIntelligent;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.UserInterfaceInfo;
import com.book.backend.service.AiIntelligentService;
import com.book.backend.service.BooksService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobaitiao
 * @description 针对表【t_ai_intelligent】的数据库操作Service实现
 * @createDate 2023-08-27 18:44:26
 */
@Service
public class AiIntelligentServiceImpl extends ServiceImpl<AiIntelligentMapper, AiIntelligent>
        implements AiIntelligentService {

    @Resource
    private BooksService booksService;
    @Resource
    private GuavaRateLimiterManager guavaRateLimiterManager;
    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;

    @Override
    public R<String> getGenResult(AiIntelligent aiIntelligent) {
        // 判断用户输入文本是否过长，超过128字，直接返回，防止资源耗尽
        String message = aiIntelligent.getInputMessage();
        if(StringUtils.isBlank(message)){
            return R.error("文本不能为空");
        }
        if (message.length() > 128) {
            return R.error("文本字数过长");
        }
        Long user_id = aiIntelligent.getUserId();
        // 查看用户接口次数是否足够，如果不够直接返回接口次数不够
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInterfaceInfo::getUserId, user_id);
        queryWrapper.eq(UserInterfaceInfo::getInterfaceId, 1);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if (userInterfaceInfo == null) {
            return R.error("该接口已废弃");
        }
        Integer leftNum = userInterfaceInfo.getLeftNum();
        Integer totalNum = userInterfaceInfo.getTotalNum();
        if (leftNum <= 0) {
            return R.error("AI接口次数不足，请明天再来");
        }
        // 限流
        boolean rateLimit = guavaRateLimiterManager.doRateLimit(user_id);
        if (!rateLimit) {
            return R.error("请求次数过多，请稍后重试");
        }
        // 调用自己的密钥

        // 创建开发请求，设置模型id和消息内容

        List<Books> list = booksService.list();
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<>();
        String presetInformation = "请根据数据库内容和游客信息作出推荐,书籍必须是数据库里面有的，可以是一本也可以是多本，但不可以超过三本书，根据游客喜欢的信息作出推荐(注意你推荐的图书必须从我的数据库内容中选择)。";

        stringBuilder.append(presetInformation).append("\n").append("数据库内容: ");
        for (Books books : list) {
            if (!hashSet.contains(books.getBookName())) {
                hashSet.add(books.getBookName());
                stringBuilder.append(books.getBookName()).append(",");
            }
        }
        stringBuilder.append("\n");

        stringBuilder.append("游客信息: ").append(message).append("\n");
//        list.forEach(System.out::println);
//        System.out.println(stringBuilder.toString());

        // 发送请求给AI，进行对话
        // 超时判断 利用ExecutorService
//
        SparkAIManager manager = new SparkAIManager(user_id + "", false);
        String response;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int timeout = 25; // 超时时间，单位为秒
        int sleepTime = getSleepTimeStrategy(message); // 等待时间
        Future<String> future = executor.submit(() -> {
            try {
                return manager.sendMessageAndGetResponse(stringBuilder.toString(),sleepTime);
            } catch (Exception exception) {
                throw new RuntimeException("遇到异常");
            }
        });

        try {
            response = future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            return R.error("服务器内部错误，请稍后重试");
        }
//        // 关闭ExecutorService
        executor.shutdown();

        // 得到消息
//        System.out.println(response);
        AiIntelligent saveResult = new AiIntelligent();
        saveResult.setInputMessage(aiIntelligent.getInputMessage());
        saveResult.setAiResult(response);
        saveResult.setUserId(user_id);
        boolean save = this.save(saveResult);
        if (!save) {
            throw new VueBookException("获取AI推荐信息失败");
        }
        // 更新调用接口的次数 剩余接口调用次数-1.总共调用次数+1
        userInterfaceInfo.setLeftNum(leftNum - 1);
        userInterfaceInfo.setTotalNum(totalNum + 1);
        boolean update = userInterfaceInfoService.updateById(userInterfaceInfo);
        if (!update) {
            return R.error("调用接口信息失败");
        }
        return R.success(response, "获取AI推荐信息成功");
    }

    @Override
    public R<List<AiIntelligent>> getAiInformationByUserId(Long userId) {
        QueryWrapper<AiIntelligent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(userId != null && userId > 0, "user_id", userId);
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 5");
        List<AiIntelligent> list = this.list(queryWrapper);
        if (list.size() == 0) {
            return R.success(null, "用户暂时没有和AI的聊天记录");
        }
        Collections.reverse(list);
        return R.success(list, "获取和AI最近的5条聊天记录成功");
    }
    public static int getSleepTimeStrategy(String message){
        int length = message.length();
        if(length<20){
            return 10;
        }else if(length<=30){
            return 12;
        }else if(length<=50){
            return 15;
        }else{
            return 20;
        }
    }
}




