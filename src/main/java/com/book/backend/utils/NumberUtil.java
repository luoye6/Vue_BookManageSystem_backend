package com.book.backend.utils;
import java.math.BigDecimal;
import java.util.Random;

/**
 * @author 赵天宇
 */
public class NumberUtil {
    public static Integer getLibraryInt(){
        return new Random().nextInt(3);
    }
    public static Integer getStatusInt() {
        //[0 - 5)
        return new Random().nextInt(5);
    }

    public static Integer getAgeInt() {
        // [10 - 60) 60 = 10 + 50
        return new Random().nextInt(50) + 10;
    }
    public static Integer getBarrageTime(){
        // [5,9] 整数
        return new Random().nextInt(5)+5;
    }
    public static Integer getSexInt() {
        //[0 - 2)
        return new Random().nextInt(2);
    }

    public static Integer getLevelInt() {
        return new Random().nextInt(100);
    }

    public static BigDecimal accountDecimal() {
        float minF = 1000000.0f;
        float maxF = 1000.0f;

        //生成随机数
        BigDecimal bd = new BigDecimal(Math.random() * (maxF - minF) + minF);

        //返回保留两位小数的随机数。不进行四舍五入
        return bd.setScale(4,BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal balanceDecimal() {
        float minF = 1000000.0f;
        float maxF = 1000.0f;

        //生成随机数
        BigDecimal bd = new BigDecimal(Math.random() * (maxF - minF) + minF);

        //返回保留两位小数的随机数。不进行四舍五入
        return bd.setScale(4,BigDecimal.ROUND_DOWN);
    }

    public static Long randomLong() {
        return Math.abs(new Random().nextLong());
    }
    //num为随机数字字符串的长度
    public static StringBuilder getNumber(int num){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < num; i++){
            sb.append(random.nextInt(10));
        }
        return sb;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            Integer libraryInt = getLibraryInt();
            System.out.println(libraryInt);
        }
    }

}

