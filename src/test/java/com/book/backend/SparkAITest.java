package com.book.backend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.book.backend.common.R;
import com.book.backend.common.exception.VueBookException;
import com.book.backend.manager.GuavaRateLimiterManager;
import com.book.backend.manager.SparkAIManager;
import com.book.backend.pojo.AiIntelligent;
import com.book.backend.pojo.Books;
import com.book.backend.pojo.UserInterfaceInfo;
import com.book.backend.service.BooksService;
import com.book.backend.service.impl.UserInterfaceInfoServiceImpl;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;

/**
 * @author <a href="https://github.com/luoye6">小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
@SpringBootTest
public class SparkAITest {
    @Resource
    private BooksService booksService;
    @Resource
    private GuavaRateLimiterManager guavaRateLimiterManager;
    @Resource
    private UserInterfaceInfoServiceImpl userInterfaceInfoService;
    @Test
    public void test() throws Exception {
        // 判断用户输入文本是否过长，超过128字，直接返回，防止资源耗尽
        BigModelNew bigModelNew = new BigModelNew("23", false);
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<>();
        String  presetInformation = "请根据数据库内容和游客信息作出推荐,书籍必须是数据库里面有的，可以是一本也可以是多本，根据游客喜欢的信息作出推荐。";

        List<Books> list = booksService.list();
        stringBuilder.append(presetInformation).append("\n").append("数据库内容: ");
        for (Books books : list) {
            if(!hashSet.contains(books.getBookName())){
                hashSet.add(books.getBookName());
                stringBuilder.append(books.getBookName()).append(",");
            }
        }
        stringBuilder.append("\n");
        String customerInformation = "我喜欢关于动物的书籍，请给我推荐图书";
        stringBuilder.append("游客信息: ").append(customerInformation).append("\n");
//        System.out.println(stringBuilder.toString());
        String reallyMessage = stringBuilder.toString();
        String result = bigModelNew.sendMessageAndGetResponse(reallyMessage);
        System.out.println(result);
    }

}
