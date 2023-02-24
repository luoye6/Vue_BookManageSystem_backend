package com.book.backend.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自定义元数据对象处理器
 * 公共填充功能
 * MyBatisPlus
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

//        metaObject.setValue("createTime", LocalDateTime.now());
//        metaObject.setValue("updateTime",LocalDateTime.now());
        // MyBatisPlus3.3.0版本后推荐使用strictInsertFill，当字段值为null时，不进行填充
        this.strictInsertFill(metaObject,"createTime",String.class,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        this.strictInsertFill(metaObject,"updateTime",String.class,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // MyBatisPlus3.3.0版本后推荐使用strictUpdateFill，当字段值为null时，不进行更新
//        metaObject.setValue("updateTime",LocalDateTime.now());
        this.strictUpdateFill(metaObject,"updateTime",
                String.class,LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
