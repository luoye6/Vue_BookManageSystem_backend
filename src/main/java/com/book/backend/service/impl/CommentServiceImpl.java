package com.book.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.book.backend.common.R;
import com.book.backend.mapper.CommentMapper;
import com.book.backend.pojo.Comment;
import com.book.backend.pojo.dto.CommentDTO;
import com.book.backend.service.CommentService;
import com.book.backend.utils.NumberUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 赵天宇
 * @description 针对表【t_comment】的数据库操作Service实现
 * @createDate 2023-02-06 19:19:20
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {
    /**
     * 1.查询当前的弹幕列表
     * 2.如果弹幕列表为空，设置响应状态码404,和请求信息，返回前端
     * 3.不为空，封装为DTO,设置响应状态码200和请求信息，返回前端
     */
    @Override
    public R<List<CommentDTO>> getCommentList() {

        R<List<CommentDTO>> result = new R<>();
        List<Comment> list = this.list();
        if (list.isEmpty()) {
            result.setStatus(404);
            result.setMsg("弹幕列表为空");
            return result;
        }
        List<CommentDTO> commentDTOList = list.stream().map((item) -> {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setAvatar(item.getCommentAvatar());
            commentDTO.setId(item.getCommentId());
            commentDTO.setMsg(item.getCommentMessage());
            commentDTO.setTime(item.getCommentTime());
            commentDTO.setBarrageStyle(item.getCommentBarrageStyle());
            return commentDTO;
        }).collect(Collectors.toList());
        result.setData(commentDTOList);
        result.setMsg("获取弹幕列表成功");
        result.setStatus(200);
        return result;
    }

    /**
     * 1.先获取请求中的参数(msg即可)
     * 2.id为null,因为数据库设置了自增
     * 3.avatar可以设置为数组中随机获取，这里暂定同一个头像
     * 4.时间随机生成(5-9秒)调用工具类
     * 5.barrageStyle从数组中随机获取
     * 6.将生成参数，传给Comment,调用service进行插入
     * 7.如果插入失败，返回404,和错误信息
     * 8.插入成功，返回200和成功信息
     */
    @Override
    public R<String> addComment(CommentDTO commentDTO) {

        String[] barrageStyleArrays = {"yibai", "erbai", "sanbai", "sibai", "wubai", "liubai", "qibai", "babai", "jiubai", "yiqian"};
        Comment comment = new Comment();
        comment.setCommentId(null);
        comment.setCommentAvatar("https://img0.baidu.com/it/u=825023390,3429989944&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
        comment.setCommentTime(NumberUtil.getBarrageTime());
        comment.setCommentMessage(commentDTO.getMsg());
        long index = Math.round(Math.random() * 10);
        //如果四舍五入后，下标越界，直接-1
        if (index == 10) {
            index = 9L;
        }
        String s = Long.toString(index);
        int newIndex = Integer.parseInt(s);
        comment.setCommentBarrageStyle(barrageStyleArrays[newIndex]);
        boolean flag = this.save(comment);
        if (!flag) {
            return R.error("添加弹幕失败");
        }
        return R.success(null, "添加弹幕成功");
    }
}




