package com.book.backend.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.book.backend.pojo.Books;
import com.book.backend.service.BooksService;
import com.book.backend.utils.NumberUtil;
import com.book.backend.utils.RandomNameUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
//  取消注释后，每次执行springboot项目，都会执行一次run方法
//@Component
@Slf4j
public class FetchInitBookList implements CommandLineRunner {
    @Resource
    private BooksService booksService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void run(String... args) throws Exception {
        String[] bookLibraries = {"南图", "北图", "教师之家"};
        String url = "https://api.ituring.com.cn/api/Search/Advanced";
        // 可以手动修改page来改变获取不同的图书
        String json = "{\n" +
                "    \"categoryId\": 1,\n" +
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
        boolean b = booksService.saveBatch(booksList);
        if(b){
            log.info("批量添加图书成功,成功插入的条数为:{}",booksList.size());
        }else{
            log.error("批量添加图书失败");
        }
    }
}
