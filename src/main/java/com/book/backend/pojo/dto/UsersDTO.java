package com.book.backend.pojo.dto;

import com.book.backend.pojo.Users;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 程序员小白条
 */
@Data
public class UsersDTO extends Users implements Serializable {
    public String userStatus;
}
