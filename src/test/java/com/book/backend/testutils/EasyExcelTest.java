package com.book.backend.testutils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.book.backend.domain.BookData;
import com.book.backend.pojo.Books;
import com.book.backend.service.BooksService;
import com.book.backend.utils.NumberUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
public class EasyExcelTest {
    @Resource
    private BooksService booksService;

    @Test
    public void doImport() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
                .excelType(ExcelTypeEnum.XLSX)
                .sheet()
                .headRowNumber(0)
                .doReadSync();
        System.out.println(list);
    }
    @Test
    public void testReadExcel() {
        // 读取的excel文件路径
        String filename = "D:\\IDEAproject\\vue_book_backend\\src\\main\\resources\\test_excel.xlsx";
        // 读取excel
        EasyExcel.read(filename, BookData.class, new AnalysisEventListener<BookData>() {
            ArrayList<Books> booksList = new ArrayList<>();
            // 每解析一行数据,该方法会被调用一次
            @Override
            public void invoke(BookData bookData, AnalysisContext analysisContext) {
                Books books = new Books();
                // 生成11位数字的图书编号
                StringBuilder stringBuilder = NumberUtil.getNumber(11);
                long bookNumber = Long.parseLong(new String(stringBuilder));
                BeanUtils.copyProperties(bookData,books);
                books.setBookNumber(bookNumber);
                booksList.add(books);
//                System.out.println("解析数据为:" + bookData.toString());
            }
            // 全部解析完成被调用
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                // 全部加入到容器list中后，一次性批量导入,先判断容器是否为空
                if(!booksList.isEmpty()){
                    // 可以将解析的数据保存到数据库
                    boolean flag = booksService.saveBatch(booksList);
                    // 如果数据添加成功
                    if(flag){
                        System.out.println("Excel批量添加图书成功");
                    }else{
                        System.out.println("Excel批量添加图书失败");
                    }
                }else{
                    System.out.println("空表无法进行数据导入");
                }

            }
        }).sheet().doRead();
    }


}
