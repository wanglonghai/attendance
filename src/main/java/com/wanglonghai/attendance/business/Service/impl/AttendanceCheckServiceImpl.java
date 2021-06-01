package com.wanglonghai.attendance.business.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.html.HttpMethod;
import com.wanglonghai.attendance.common.html.HttpParamers;
import com.wanglonghai.attendance.common.html.HttpUtils;
import com.wanglonghai.attendance.entity.UserInfo;
import com.wanglonghai.attendance.entity.dto.UserList;
import lombok.extern.slf4j.Slf4j;
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
    @Autowired
    UserList userList;
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
    public String getQrLoginCodeUrl(){
        String  qrUrl="二维码地址，qrcode url";
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addParam("p","p");
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/loginManager/getQrCodeUrl", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
             qrUrl=(String)result.get("data");
        }else{
            log.error("获取二维码失败");
        }
        log.info("：：：：：："+qrUrl);
        return qrUrl;
    }
    @Override
    public Boolean doQrCodeLogin(String qrCode){
        HttpParamers httpParamers=new HttpParamers(HttpMethod.GET);
        httpParamers.addParam("qrCode",qrCode);
        httpParamers.addParam("openId",userList.getList().get(0).getOpenId());
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/loginManager/qrCodeLogin", httpParamers);
        if(result.get("data")!=null&&result.get("data").toString().contains("您已成功登录管理系统")){
            log.info("执行扫码后逻辑成功："+JSONObject.toJSONString(result));
            return true;
        }else{
            log.error("执行扫码后逻辑失败："+JSONObject.toJSONString(result));
            return false;
        }
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
        /**String qrCode=StringUtils.isBlank(userInfo.getQrCode())? getQrLoginCodeUrl():userInfo.getQrCode();
        if(StringUtils.isBlank(qrCode)){**/
            httpParamers.addParam("clientId","1");
        /**}else{
            //升级后 并且账户密码登录禁用后再放开
            httpParamers.addParam("clientId","4");
            httpParamers.addParam("qrCode",qrCode);
            httpParamers.addParam("accountId",userInfo.getAccountId().toString());
        }**/
        httpParamers.setJsonParamer();
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/loginManager/pcLogin", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            JSONObject jsonObject=(JSONObject)result.get("data");
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
    @Override
    public String mainTainToken(String tk){
        if(StringUtils.isBlank(tk)){
            log.warn("tk未配置");
            return "tk未配置";
        }
        HttpParamers httpParamers=new HttpParamers(HttpMethod.GET);
        httpParamers.addParam("limit","1");
        httpParamers.addParam("page","1");
        httpParamers.addHeader("tk",tk);
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/noticeManage/index/news", httpParamers);
        String res="未连接网络";
        if(result!=null){
             res=JSONObject.toJSONString(result);
        }
        log.info(res);
        return res;
    }
    private void errorInfo(Map<String, Object> result,UserInfo userInfo) {
        if(result!=null){
            log.error(JSONObject.toJSONString(result));
        }
        String code=result.get("code")==null?"":result.get("code").toString();
        String message=result.get("message")==null?"":result.get("message").toString();
        if(StringUtils.isBlank(message)){
            message="可能网络不通";
        }
        weiXinService.sendMessageWX("fail!"+ message,userInfo.getOpenId());
        log.error("###code:"+code);
        log.error("###message:"+message);
    }
}
