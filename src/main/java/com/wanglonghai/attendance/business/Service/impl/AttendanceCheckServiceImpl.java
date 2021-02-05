package com.wanglonghai.attendance.business.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.html.HttpMethod;
import com.wanglonghai.attendance.common.html.HttpParamers;
import com.wanglonghai.attendance.common.html.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    @Value("${attendance.userName}")
    public String userName;
    @Value("${attendance.passWord}")
    public String passWord;
    @Value("${attendance.serviceUrl}")
    public String serviceUrl;
    @Autowired
    WeiXinService weiXinService;
    @Override
    public Boolean dk(String token) {
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",token);
        httpParamers.addParam("latitude","111");
        httpParamers.addParam("longitude","111");
        httpParamers.addParam("clientType","0");
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/apply/checkIn", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            //JSONObject jsonObject=(JSONObject)result.get("data");
            log.info("*****************attendance success*****************");
            weiXinService.sendMessageWX("ceshi");
            return true;
        }else if(result.get("code")!=null&&"-30000".equalsIgnoreCase(result.get("code").toString())){
            log.warn(result.get("message").toString());
            return true;
        }else{
            errorInfo(result);
            log.info("*****************attendance fail*****************");
            return false;
        }
    }

    @Override
    public Boolean getQrCode(String token) {
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",token);
        httpParamers.addParam("isShow","true");
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/apply/checking/qrcode", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            //JSONObject jsonObject=(JSONObject)result.get("data");
            log.info("*****************qrCode success*****************");
            return true;
        }else{
            errorInfo(result);
            log.info("*****************qrCode fail*****************");
            return false;
        }
    }

    @Override
    public String login(){
        if(StringUtils.isBlank(userName)||StringUtils.isBlank(passWord)||StringUtils.isBlank(serviceUrl)){
            log.error("!!!!!!!!!!!!!!!!!!!!config error!!!!!!!!!!!!!!!!!!!!");
        }
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addParam("userName",userName);
        httpParamers.addParam("passWord",passWord);
        httpParamers.addParam("sessionId","dd");
        httpParamers.addParam("clientId","1");
        httpParamers.setJsonParamer();
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/loginManager/pcLogin", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            JSONObject jsonObject=(JSONObject)result.get("data");
            //log.info(jsonObject.get("userName").toString()+"登录成功");
            log.info(jsonObject.get("userName").toString()+" login success...");
            jsonObject=(JSONObject)jsonObject.get("token");
            String token=jsonObject.getString("token");
            log.info(token);
            return token;
        }else{
            errorInfo(result);
            log.info("*****************login fail*****************");
        }
        return null;
    }
    private void errorInfo(Map<String, Object> result) {
        String code=result.get("code")==null?"":result.get("code").toString();
        String message=result.get("message")==null?"":result.get("message").toString();
        log.error("###code:"+code);
        log.error("###message:"+message);
    }
}
