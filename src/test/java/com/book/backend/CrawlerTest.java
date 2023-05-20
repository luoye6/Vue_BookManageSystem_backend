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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
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
            if (bookDescription.length() >= 255) {
                bookDescription = bookDescription.substring(0, 254);
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

    @Test
    public void fetchLibrary() throws IOException {
        String url = "https://search.bilibili.com/all?keyword=Java&page=1";
        List<Video> videos = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String pageUrl = url + i;
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla")
                    .referrer("https://www.bilibili.com/")
                    .timeout(100000)
                    .get();
            Elements items = doc.select(".video-list > ul > li");
            for (Element item : items) {
                Element titleElement = item.select(".title > a").first();
                String title = titleElement.text();
                String videoUrl = titleElement.attr("href");
                Element authorElement = item.select(".up-name > a").first();
                String author = authorElement.text();
                String authorUrl = authorElement.attr("href");
                String playCount = item.select(".play > span").first().text();
                String danmuCount = item.select(".dm > span").first().text();
                String releaseTime = item.select(".so-icon.time > span").first().text();
                Video video = new Video(title, videoUrl, author, authorUrl, playCount, danmuCount, releaseTime);
                videos.add(video);
            }
        }

        for (Video video : videos) {
            System.out.println(video);
        }
    }

    static class Video {
        private String title;
        private String videoUrl;
        private String author;
        private String authorUrl;
        private String playCount;
        private String danmuCount;
        private String releaseTime;

        public Video(String title, String videoUrl, String author, String authorUrl, String playCount, String danmuCount, String releaseTime) {
            this.title = title;
            this.videoUrl = videoUrl;
            this.author = author;
            this.authorUrl = authorUrl;
            this.playCount = playCount;
            this.danmuCount = danmuCount;
            this.releaseTime = releaseTime;
        }

        @Override
        public String toString() {
            return "Video{" +
                    "title='" + title + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", author='" + author + '\'' +
                    ", authorUrl='" + authorUrl + '\'' +
                    ", playCount='" + playCount + '\'' +
                    ", danmuCount='" + danmuCount + '\'' +
                    ", releaseTime='" + releaseTime + '\'' +
                    '}';
        }
    }
}
