package com.book.backend;

import cn.hutool.core.lang.Console;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.book.backend.pojo.Books;
import com.book.backend.utils.NumberUtil;
import com.book.backend.utils.RandomNameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
public class CrawlerTest {
    @Test
    public void testFetch() {
        String json = "{\n" +
                "    \"filter\": [\n" +
                "        {\n" +
                "            \"fieldName\": \"FULLTEXT\",\n" +
                "            \"values\": [\n" +
                "                \"*\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"fieldName\": \"TZCID\",\n" +
                "            \"values\": [\n" +
                "                \"K\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"indexName\": \"\",\n" +
                "    \"searchtype\": \"\",\n" +
                "    \"sortFiedld\": \"\",\n" +
                "    \"size\": 20,\n" +
                "    \"from\": 1,\n" +
                "    \"searchType\": \"\"\n" +
                "}";
        String url = "https://z.library.sh.cn/ui/zh/search";
        String result = HttpRequest.post(url).body(json).execute().body();
        System.out.println(result);
    }

    @Test
    public void test() {
        String url = "http://library.mingyuefusu.top/admin/bookList?page=500&limit=100";
        //链式构建请求
        String result2 = HttpRequest.get(url)
                .header(Header.USER_AGENT, "Hutool http")//头信息，多个头信息多次调用此方法即可
                .timeout(20000)//超时，毫秒
                .execute().body();
        Console.log(result2);
    }

    @Test
    public void test1() {
        String[] bookLibraries = {"南图", "北图", "教师之家"};
        String url = "https://api.ituring.com.cn/api/Search/Advanced";
        String json = "{\n" +
                "    \"categoryId\": 108,\n" +
                "    \"sort\": \"new\",\n" +
                "    \"page\": 1,\n" +
                "    \"name\": \"\",\n" +
                "    \"edition\": 1\n" +
                "}";
        String result2 = HttpRequest.post(url)
                .body(json)
                .execute().body();
        Map<String, Object> map = JSONUtil.toBean(result2, Map.class);
        JSONArray bookItems = (JSONArray) map.get("bookItems");
        List<Books> booksList = new ArrayList<>();
        for (Object record : bookItems) {
            int randomLibrary = NumberUtil.getLibraryInt();
            JSONObject tempRecord = (JSONObject) record;
            Books books = new Books();
            // 生成11位数字的图书编号
            StringBuilder stringNumber = NumberUtil.getNumber(11);
            long bookNumber = Long.parseLong(new String(stringNumber));
            String bookName = tempRecord.getStr("name");
            String author = tempRecord.getStr("translatorNameString");
            String bookDescription = tempRecord.getStr("abstract");
            if(bookDescription.length()>=255){
                bookDescription = bookDescription.substring(0,254);
            }
            books.setBookNumber(bookNumber);
            books.setBookName(bookName);
            books.setBookAuthor(author);
            books.setBookLibrary(bookLibraries[randomLibrary]);
            books.setBookType("计算机");
            String location = RandomNameUtils.getRandomLocation();
            books.setBookLocation(location);
            books.setBookStatus("未借出");
            books.setBookDescription(bookDescription);
            booksList.add(books);
        }
        booksList.forEach(System.out::println);
    }
}
