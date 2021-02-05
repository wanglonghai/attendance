package com.wanglonghai.attendance.business.Service.impl;

import com.alibaba.fastjson.JSON;
import com.wanglonghai.attendance.business.Common.YmlConfig;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.html.HttpMethod;
import com.wanglonghai.attendance.common.html.HttpParamers;
import com.wanglonghai.attendance.common.html.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName WeiXinServiceImpl
 * @Author Gavin
 * @Date 2021/2/5 14:30
 * @Description 描述
 * @Version 1.0
 */
@Slf4j
@Service
public class WeiXinServiceImpl implements WeiXinService {
    @Autowired
    YmlConfig ymlConfig;
    final String template="{" +
            "    \"touser\":\"%s\"," +
            "    \"template_id\":\"%s\"," +
            "    \"url\":\"%s\"," +
            "    \"data\":{" +
            "        \"first\":{" +
            "            \"value\":\"%s\"" +
            "        }," +
            "        \"keyword1\":{" +
            "            \"value\":\"%s\"" +
            "        }," +
            "        \"keyword2\":{" +
            "            \"value\":\"%s\"" +
            "        }," +
            "        \"keyword3\":{" +
            "            \"value\":\"%s\"," +
            "            \"color\":\"#0d814a\"" +
            "        }," +
            "        \"remark\":{" +
            "            \"value\":\"%s\"" +
            "        }" +
            "    }" +
            "}";
    @Override
    public boolean sendMessageWX(String message) {
        if(StringUtils.isBlank(ymlConfig.getOpenId())){
            log.error("openId not config,send message cancel***********");
            return false;
        }
        HttpParamers httpParamers = new HttpParamers(HttpMethod.POST);
        String info=String.format(template,ymlConfig.getOpenId(),ymlConfig.getTemplateId(),"www.baidu.com","1",
                "2","3",message,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        httpParamers.addParam("temaplateMessageJSON",info);
        String httpUrl = ymlConfig.getWeChatServiceUrl() + "/wx/message/" + ymlConfig.getWeChatFlag() + "/sendTemplate2";
        Map<String, Object> responseData = HttpUtils.doWeChatRequest(httpUrl, httpParamers,
                ymlConfig.getWeChatAppid(), ymlConfig.getWeChatSecret());
        if (responseData == null || responseData.size() == 0) {
            log.error("可能网络不通，发送离线消息失败");
            return false;
        }
        log.debug(JSON.toJSONString(responseData));
        if (responseData.containsKey("data") && responseData.get("data") != null) {
            log.info((String) responseData.get("data"));
            return true;
        } else {
            log.error((String) responseData.get("message"));
            log.error("发送离线消息失败,请联系管理员");
            log.error("send message fail,please contact adminstrator");
            return false;
        }
    }
}
