package com.wanglonghai.attendance.business.Service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.AttendanceSummaryService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.html.HttpMethod;
import com.wanglonghai.attendance.common.html.HttpParamers;
import com.wanglonghai.attendance.common.html.HttpUtils;
import com.wanglonghai.attendance.entity.AttendanceWorkSummary;
import com.wanglonghai.attendance.entity.UserInfo;
import com.wanglonghai.attendance.entity.dto.UserList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName AttendanceSummaryServiceImpl
 * @Author Gavin
 * @Date 2021/2/8 9:27
 * @Description 描述
 * @Version 1.0
 */
@Service
@Slf4j
public class AttendanceSummaryServiceImpl implements AttendanceSummaryService {
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    WeiXinService weiXinService;
    @Value("${attendance.serviceUrl}")
    public String serviceUrl;
    @Override
    public Boolean writeSummary(AttendanceWorkSummary attendanceWorkSummary) {
        String summaryDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",attendanceWorkSummary.getToken());
        httpParamers.addParam("planTomorrow",attendanceWorkSummary.getPlanTomorrow());
        httpParamers.addParam("selfScore",attendanceWorkSummary.getSelfScore()==null?"5":attendanceWorkSummary.getSelfScore().toString());
        httpParamers.addParam("summaryToday",attendanceWorkSummary.getSummaryToday());
        httpParamers.setJsonParamer();
        String message="";
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/attendanceWorkSummary/saveSummary", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            JSONObject jsonObject=(JSONObject)result.get("data");
            Object summaryIdObj=jsonObject.get("id");
            if(summaryIdObj!=null){
                message=(String.format("*****************at %s writeSummary success,summaryId=%s,result=true*****************",summaryDate,summaryIdObj));
                attendanceWorkSummary.setMessage(message);
                log.info(message);
                weiXinService.sendMessageWX(message,attendanceWorkSummary.getOpenId());
                return true;
            }else{
                message=(String.format("*****************at %s writeSummary fail*****************",summaryDate));
                log.info(message);
                attendanceWorkSummary.setMessage(message);
                weiXinService.sendMessageWX(message,attendanceWorkSummary.getOpenId());
                return false;
            }
        }else{
            message=(String.format("*****************at %s writeSummary fail,%s*****************",summaryDate,result.get("message")));
            log.info(message);
            attendanceWorkSummary.setMessage(message);
            weiXinService.sendMessageWX(message,attendanceWorkSummary.getOpenId());
            return false;
        }
    }

    @Override
    public Boolean hasWriteSummary(Date date, UserInfo userInfo) {
        String summaryDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String message="";
        Long summaryPersonId=userInfo.getAccountId();
        HttpParamers httpParamers=new HttpParamers(HttpMethod.POST);
        httpParamers.addHeader("tk",userInfo.getTk());
        httpParamers.addParam("summaryPersonId",summaryPersonId.toString());
        httpParamers.addParam("date",summaryDate);
        Map<String, Object> result= HttpUtils.doRequest(serviceUrl+"/attendance/attendanceWorkSummary/detailSummary", httpParamers);
        if(result.get("code")!=null&&"200".equalsIgnoreCase(result.get("code").toString())){
            JSONObject jsonObject=(JSONObject)result.get("data");
            Object addStatus,summaryId;
            //还没生成一条默认日志
            boolean noDefaultSummary=jsonObject.containsKey("summaryId")&&(summaryId=jsonObject.get("summaryId"))!=null&&"-1".equals(summaryId.toString());
            //生成了默认日志，但处于未提交状态
            boolean noWriteSummary=jsonObject.containsKey("addStatus")&&(addStatus=jsonObject.get("addStatus"))!=null&&"-1".equals(addStatus.toString());
            if(noDefaultSummary||noWriteSummary){
                message=(String.format("*****************%s at %s judgeHasWriteSummary success,result=false*****************",summaryPersonId,summaryDate));
                log.info(message);
                weiXinService.sendMessageWX(message,userInfo.getOpenId());
                return false;
            }else{
                message=(String.format("*****************%s at %s judgeHasWriteSummary success,result=true*****************",summaryPersonId,summaryDate));
                log.info(message);
                weiXinService.sendMessageWX(message,userInfo.getOpenId());
                return true;
            }
        }else{
            message=(String.format("*****************%s at %s judgeHasWriteSummary fail,%s*****************",summaryPersonId,summaryDate,result.get("message")));
            log.info(message);
            weiXinService.sendMessageWX(message,userInfo.getOpenId());
            return false;
        }
    }
}
