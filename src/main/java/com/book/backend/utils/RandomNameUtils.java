package com.book.backend.utils;
import cn.hutool.core.util.RandomUtil;

import java.io.UnsupportedEncodingException;
import java.util.Random;


/**
 * @author 赵天宇
 */
public class RandomNameUtils {

    /**
     * 随机获取姓名
     *
     * @return
     */
    public static String fullName() {
        return surname() + name(2);
    }

    /**
     * 随机获取姓
     *
     * @return
     */
    public static String surname() {
        return SURNAME[new Random().nextInt(SURNAME.length - 1)];
    }

    /**
     * 获取N位常用字
     *
     * @param len
     * @return
     */
    public static String name(int len) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String str = null;
            // 定义高低位
            int highPos, lowPos;
            Random random = new Random();
            //获取高位值
            highPos = (176 + Math.abs(random.nextInt(39)));
            //获取低位值
            lowPos = (161 + Math.abs(random.nextInt(93)));
            byte[] b = new byte[2];
            b[0] = (new Integer(highPos).byteValue());
            b[1] = (new Integer(lowPos).byteValue());
            try {
                //转成中文
                str = new String(b, "GBK");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
            ret.append(str);
        }
        return ret.toString();
    }
    public static String getRandomLocation(){
        int i=(int)Math.round(Math.random()*26);
        int j=(int)'A'+i;
        char ch=(char)j;
        String s = RandomUtil.randomNumbers(6);
        return ch+s;
    }
    /**
     * 2021年姓排行100
     */
    private final static String[] SURNAME = {
            "李", "王", "张", "刘", "陈",
            "杨", "赵", "黄", "周", "吴",
            "徐", "孙", "胡", "朱", "高",
            "林", "何", "郭", "马", "罗",
            "梁", "宋", "郑", "谢", "韩",
            "唐", "冯", "于", "董", "萧",
            "程", "曹", "袁", "邓", "许",
            "傅", "沈", "曾", "彭", "吕",
            "苏", "卢", "蒋", "蔡", "贾",
            "丁", "魏", "薛", "叶", "阎",
            "余", "潘", "杜", "戴", "夏",
            "钟", "汪", "田", "任", "姜",
            "范", "方", "石", "姚", "谭",
            "廖", "邹", "熊", "金", "陆",
            "郝", "孔", "白", "崔", "康",
            "毛", "邱", "秦", "江", "史",
            "顾", "侯", "邵", "孟", "龙",
            "万", "段", "漕", "钱", "汤",
            "尹", "黎", "易", "常", "武",
            "乔", "贺", "赖", "龚", "文"};
}
