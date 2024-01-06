package com.book.backend.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 程序员小白条
 */
@Data
public class CommentDTO implements Serializable {
    public Integer id;
    public String avatar;
    public String msg;
    public Integer time;
    public String barrageStyle;
}
