package com.wanglonghai.attendance.entity;

import lombok.Data;

/**
 * @ClassName UserInfo
 * @Author Gavin
 * @Date 2021/2/8 10:13
 * @Description 描述
 * @Version 1.0
 */
@Data
public class UserInfo {
    private Long accountId;
    private String name;
    private String pwd;
    private String tk;
    private String openId;
}
