package com.wanglonghai.attendance.business.Service;

import com.wanglonghai.attendance.entity.UserInfo;

/**
 * @ClassName AttendanceCheckService
 * @Author Gavin
 * @Date 2021/2/5 14:12
 * @Description 描述
 * @Version 1.0
 */
public interface AttendanceCheckService {
    Boolean dk(String token);
    Boolean getQrCode(String tk);
    String login(UserInfo userInfo);
}
