package com.wanglonghai.attendance.business.Service;

/**
 * @ClassName WeiXinService
 * @Author Gavin
 * @Date 2021/2/5 14:29
 * @Description 描述
 * @Version 1.0
 */
public interface WeiXinService {
    boolean sendMessageWX(String message,String toUrl,String openId);
    boolean sendMessageWX(String message,String openId);
}
