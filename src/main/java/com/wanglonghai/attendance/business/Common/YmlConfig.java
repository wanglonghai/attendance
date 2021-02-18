package com.wanglonghai.attendance.business.Common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
/**
 * yml文件的配置信息
 */
public class YmlConfig {
    //微信服务地址
    @Value("${wechat.serviceUrl}")
    private String weChatServiceUrl;
    //微信对接公众号标示
    @Value("${wechat.flag}")
    private String weChatFlag;

    //调用微信服务授权appid
    @Value("${wechat.appid}")
    private String weChatAppid;
    //调用微信服务授权secret
    @Value("${wechat.secret}")
    private String weChatSecret;
    //公众号模板id
    @Value("${wechat.templateId}")
    private String templateId;
    //离线消息打开地址
    @Value("${wechat.messageUrl}")
    private String messageUrl;

}