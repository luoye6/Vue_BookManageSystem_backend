package com.book.backend.service;

import com.book.backend.common.R;
import com.book.backend.pojo.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.book.backend.pojo.dto.chart.GenChartByAiRequest;
import com.book.backend.pojo.vo.BiResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author xiaobaitiao
* @description 针对表【t_chart(图表信息表)】的数据库操作Service
* @createDate 2023-08-30 11:05:22
*/
public interface ChartService extends IService<Chart> {
    R<BiResponse> genChartByAi(MultipartFile multipartFile,
                               GenChartByAiRequest genChartByAiRequest);
}
