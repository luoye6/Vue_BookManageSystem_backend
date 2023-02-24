package com.book.backend.common;

/**
 * @author 赵天宇
 * 常量类
 * 防止魔法值
 */
public class Constant {
    /**
     * 字符串NULL判断
     */
    public  static final String NULL = "null";
    /**
     * 预检请求
     */
    public static final String OPTIONS = "OPTIONS";
    /**
     * 账号为可用状态
     */
    public static final Integer  AVAILABLE = 1;
    /**
     * 账号为禁用状态
     */
    public static final Integer DISABLE = 0;
    /**
     * 图书已借出状态
     */
    public static final String BOOKDISABLE = "已借出";
    /**
     * 图书未借出状态
     */
    public static final String BOOKAVAILABLE = "未借出";
    /**
     * 用户可用状态 字符串
     */
    public static  final String USERAVAILABLE = "可用";
    /**
     * 用户禁用状态 字符串
     */
    public static final String USERDISABLE = "禁用";
    /**
     * 密码超过30，判定为md5加密字符
     */
    public static final Integer MD5PASSWORD = 30;
}
