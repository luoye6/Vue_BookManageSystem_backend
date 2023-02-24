package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.pojo.Violation;
import com.book.backend.service.ViolationService;
import com.book.backend.mapper.ViolationMapper;
import org.springframework.stereotype.Service;

/**
* @author 赵天宇
* @description 针对表【t_violation】的数据库操作Service实现
* @createDate 2023-02-06 16:31:20
*/
@Service
public class ViolationServiceImpl extends ServiceImpl<ViolationMapper, Violation>
    implements ViolationService{

}




