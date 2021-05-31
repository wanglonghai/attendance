package com.wanglonghai.attendance.business.Service;

import com.wanglonghai.attendance.entity.UserInfo;

import java.util.List;

/**
 * @ClassName AttendanceCheckService
 * @Author Gavin
 * @Date 2021/2/5 14:12
 * @Description 描述
 * @Version 1.0
 */
public interface AttendanceCheckService {
    Boolean dk(UserInfo userInfo);
    Boolean getQrCode(UserInfo userInfo);
    String login(UserInfo userInfo);
    void loginAll(List<UserInfo> userInfos);
    String mainTainToken(String tk);
}
