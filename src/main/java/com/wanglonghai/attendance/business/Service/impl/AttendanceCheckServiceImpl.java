package com.wanglonghai.attendance.business.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.html.HttpMethod;
import com.wanglonghai.attendance.common.html.HttpParamers;
import com.wanglonghai.attendance.common.html.HttpUtils;
import com.wanglonghai.attendance.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AttendanceCheckService
 * @Author Gavin
 * @Date 2021/2/5 14:12
 * @Description 描述
 * @Version 1.0
 */
@Service
@Slf4j
public class AttendanceCheckServiceImpl implements AttendanceCheckService {
//    @Value("${attendance.userName}")
//    public String userName;
//    @Value("${attendance.passWord}")
//    public String passWord;
    @Value("${attendance.serviceUrl}")
    public String serviceUrl;
    @Autowired
    WeiXinService weiXinService;
    @Override
    public Boolean dk(UserInfo userInfo) {
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",userInfo.getTk());
        //httpParamers.addParam("latitude","111");
        //httpParamers.addParam("longitude","111");
        httpParamers.addParam("clientType","1");
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/apply/checkIn", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            //JSONObject jsonObject=(JSONObject)result.get("data");
            log.info("*****************attendance success*****************");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            date.setTime(Long.parseLong(result.get("data").toString()));//java里面应该是按毫秒
            weiXinService.sendMessageWX("attendance success!"+ sdf.format(date),userInfo.getOpenId());
            return true;
        }else if(result.get("code")!=null&&"-30000".equalsIgnoreCase(result.get("code").toString())){
            log.warn(result.get("message").toString());
            weiXinService.sendMessageWX("attendance fail!"+ result.get("message").toString(),userInfo.getOpenId());
            return true;
        }else{
            errorInfo(result,userInfo);
            log.info("*****************attendance fail*****************");
            return false;
        }
    }

    @Override
    public Boolean getQrCode(UserInfo userInfo) {
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",userInfo.getTk());
        httpParamers.addParam("isShow","true");
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/apply/checking/qrcode", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            //JSONObject jsonObject=(JSONObject)result.get("data");
            log.info("*****************qrCode success*****************");
            return true;
        }else{
            errorInfo(result,userInfo);
            log.info("*****************qrCode fail*****************");
            if(!StringUtils.isBlank(userInfo.getOpenId())){
                weiXinService.sendMessageWX("qrCode fail!"+ result.get("message").toString(),userInfo.getOpenId());
            }
            return false;
        }
    }
    @Override
    public void loginAll(List<UserInfo> userInfos){
        userInfos.forEach(userInfo -> {
            login(userInfo);
        });
    }
    @Override
    public String login(UserInfo userInfo){
        if(userInfo==null
                ||StringUtils.isBlank(userInfo.getName())
                ||StringUtils.isBlank(userInfo.getPwd())
                ||StringUtils.isBlank(serviceUrl)){
            log.error("!!!!!!!!!!!!!!!!!!!!config error!!!!!!!!!!!!!!!!!!!!");
            return "";
        }
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addParam("userName",userInfo.getName());
        httpParamers.addParam("passWord",userInfo.getPwd());
        httpParamers.addParam("sessionId","dd");
        httpParamers.addParam("clientId","1");
        httpParamers.setJsonParamer();
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/loginManager/pcLogin", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            JSONObject jsonObject=(JSONObject)result.get("data");
            userInfo.setAccountId(Long.valueOf(jsonObject.get("accountId").toString()));
            log.info(jsonObject.get("userName").toString()+" login success...");

            jsonObject=(JSONObject)jsonObject.get("token");
            String token=jsonObject.getString("token");
            userInfo.setTk(token);
            log.info(token);
            return token;
        }else{
            errorInfo(result,userInfo);
            log.info("*****************login fail*****************");
        }
        return null;
    }
    private void errorInfo(Map<String, Object> result,UserInfo userInfo) {
        String code=result.get("code")==null?"":result.get("code").toString();
        String message=result.get("message")==null?"":result.get("message").toString();
        weiXinService.sendMessageWX("fail!"+ message,userInfo.getOpenId());
        log.error("###code:"+code);
        log.error("###message:"+message);
    }
}
