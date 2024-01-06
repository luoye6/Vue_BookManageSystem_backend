package com.book.backend.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.R;
import com.book.backend.common.exception.ErrorCode;
import com.book.backend.common.exception.ThrowUtils;
import com.book.backend.manager.AiManager;
import com.book.backend.manager.GuavaRateLimiterManager;
import com.book.backend.manager.SparkAIManager;
import com.book.backend.pojo.Admins;
import com.book.backend.pojo.Chart;
import com.book.backend.pojo.UserInterfaceInfo;
import com.book.backend.pojo.dto.chart.GenChartByAiRequest;
import com.book.backend.pojo.vo.BiResponse;
import com.book.backend.service.AdminsService;
import com.book.backend.service.ChartService;
import com.book.backend.mapper.ChartMapper;
import com.book.backend.utils.ExcelUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author xiaobaitiao
* @description 针对表【t_chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-08-30 11:05:22
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{
    @Resource
    private AdminsService adminsService;
    @Resource
    private AiManager aiManager;
    @Resource
    private  GuavaRateLimiterManager guavaRateLimiterManager;
    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;
    // region
//    @Override 旧版本AI
//    public R<BiResponse> genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
//        String name = genChartByAiRequest.getName();
//        String goal = genChartByAiRequest.getGoal();
//        String chartType = genChartByAiRequest.getChartType();
//        // 校验
//        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
//        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
//        // 校验文件
//        long size = multipartFile.getSize();
//        String originalFilename = multipartFile.getOriginalFilename();
//        // 校验文件大小
//        final long ONE_MB = 1024 * 1024L;
//        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
//        // 校验文件后缀 aaa.png
//        String suffix = FileUtil.getSuffix(originalFilename);
//        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
//        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
//        // 这里把逻辑写死，AI功能现在测试阶段
//        Admins admin = adminsService.getById(genChartByAiRequest.getAdminId());
//        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
//        // 功能做限制，用户只能调用AI接口聊天，管理员只能调用图表生成，这边用管理员ID
//        queryWrapper.eq(UserInterfaceInfo::getUserId,admin.getAdminId());
//        UserInterfaceInfo interfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
//        if(interfaceInfo == null){
//            return R.error("该接口已废弃");
//        }
//        Integer leftNum = interfaceInfo.getLeftNum();
//        Integer totalNum = interfaceInfo.getTotalNum();
//        if(leftNum<=0){
//            return R.error("AI接口次数不足，请明天再来");
//        }
//        // 限流判断，每个管理员一个限流器
//        boolean limit = guavaRateLimiterManager.doRateLimit(admin.getAdminId());
//        if(!limit){
//            return R.error("请求次数过多，请稍后重试");
//        }
//
//        // 用户输入
//        // 无需写 prompt，直接调用现有模型，https://www.yucongming.com，公众号搜【鱼聪明AI】
////        final String prompt = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\n" +
////                "分析需求：\n" +
////                "{数据分析的需求或者目标}\n" +
////                "原始数据：\n" +
////                "{csv格式的原始数据，用,作为分隔符}\n" +
////                "请根据这两部分内容，按照以下指定格式生成内容（此外不要输出任何多余的开头、结尾、注释）\n" +
////                "【【【【【\n" +
////                "{前端 Echarts V5 的 option 配置对象js代码，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\n" +
////                "【【【【【\n" +
////                "{明确的数据分析结论、越详细越好，不要生成多余的注释}";
//        long biModelId = Constant.BI_MODEL_ID;
//        // 分析需求：
//        // 分析网站用户的增长情况
//        // 原始数据：
//        // 日期,用户数
//        // 1号,10
//        // 2号,20
//        // 3号,30
//
//        // 构造用户输入
//        StringBuilder userInput = new StringBuilder();
//        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和原始数据，请告诉我分析结论。").append("\n");
//        userInput.append("分析目标：").append(goal).append("\n");
//        userInput.append("分析需求：").append("\n");
//
//        // 拼接分析目标
//        String userGoal = goal;
//        if (StringUtils.isNotBlank(chartType)) {
//            userGoal += "，请使用" + chartType;
//        }
//        userInput.append(userGoal).append("\n");
//        userInput.append("原始数据：").append("\n");
//        String csvData = ExcelUtils.excelToCsv(multipartFile);
//        userInput.append(csvData).append("\n");
//        String s = userInput.toString();
//        System.out.println(s);
//        String result = aiManager.doChat(biModelId, userInput.toString());
//        String[] splits = result.split("【【【【【");
//        if (splits.length < 3) {
//            return R.error("AI生成错误，请稍后重试");
////            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
//        }
//        String genChart = splits[1].trim();
//        String genResult = splits[2].trim();
//        // 插入到数据库
//        Chart chart = new Chart();
//        chart.setName(name);
//        chart.setGoal(goal);
//        chart.setChartData(csvData);
//        chart.setChartType(chartType);
//        chart.setGenChart(genChart);
//        chart.setGenResult(genResult);
//        chart.setAdminId(genChartByAiRequest.getAdminId());
//        boolean saveResult = this.save(chart);
//        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
//        // 更新调用接口的次数 剩余接口调用次数-1.总共调用次数+1
//        interfaceInfo.setLeftNum(leftNum-1);
//        interfaceInfo.setTotalNum(totalNum+1);
//        boolean update = userInterfaceInfoService.updateById(interfaceInfo);
//        if(!update){
//            return R.error("调用接口信息失败");
//        }
//        BiResponse biResponse = new BiResponse();
//        biResponse.setGenChart(genChart);
//        biResponse.setGenResult(genResult);
//        biResponse.setChartId(chart.getId());
//        return R.success(biResponse,"图表生成成功");
//    }
    // endregion
    @Override
    public R<BiResponse> genChartByAi(MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        // 校验文件
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
        // 这里把逻辑写死，AI功能现在测试阶段
        Admins admin = adminsService.getById(genChartByAiRequest.getAdminId());
        LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 功能做限制，用户只能调用AI接口聊天，管理员只能调用图表生成，这边用管理员ID
        queryWrapper.eq(UserInterfaceInfo::getUserId,admin.getAdminId());
        UserInterfaceInfo interfaceInfo = userInterfaceInfoService.getOne(queryWrapper);
        if(interfaceInfo == null){
            return R.error("该接口已废弃");
        }
        Integer leftNum = interfaceInfo.getLeftNum();
        Integer totalNum = interfaceInfo.getTotalNum();
        if(leftNum<=0){
            return R.error("AI接口次数不足，请明天再来");
        }
        // 限流判断，每个管理员一个限流器
        boolean limit = guavaRateLimiterManager.doRateLimit(admin.getAdminId());
        if(!limit){
            return R.error("请求次数过多，请稍后重试");
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
        userInput.append("分析需求："+userGoal+"\n");
        userInput.append("原始数据: "+"\n");
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");
        String result;
        SparkAIManager sparkAIManager = new SparkAIManager(genChartByAiRequest.getAdminId() + "", false);
        try {
            result = sparkAIManager.sendMessageAndGetResponse(userInput.toString(),25);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            return R.error("AI生成错误，请稍后重试");
        }
        Pattern pattern = Pattern.compile("option = ([^;]+);");
        Matcher matcher = pattern.matcher(splits[1]);
        String genChart = "";
        if (matcher.find()) {
            genChart = matcher.group(1).trim();
        }else{
            return R.error("AI生成错误，请稍后重试");
        }
        String genResult = splits[2].split("}")[1].trim();
        Gson gson = new Gson();
        Object objectChart = gson.fromJson(genChart, Object.class);
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart.trim());
        chart.setGenResult(genResult);
        chart.setAdminId(genChartByAiRequest.getAdminId());
        boolean saveResult = this.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        // 更新调用接口的次数 剩余接口调用次数-1.总共调用次数+1
        interfaceInfo.setLeftNum(leftNum-1);
        interfaceInfo.setTotalNum(totalNum+1);
        boolean update = userInterfaceInfoService.updateById(interfaceInfo);
        if(!update){
            return R.error("调用接口失败");
        }
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        R<BiResponse> resultData = new R<>();
        resultData.add("genChart",objectChart);
        resultData.setData(biResponse);
        resultData.setMsg("图表生成成功");
        resultData.setStatus(200);
        return resultData;
    }
}




