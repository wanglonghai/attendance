package com.wanglonghai.attendance.business.Job;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.AttendanceSummaryService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.entity.AttendanceWorkSummary;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName SummaryJob
 * @Author Gavin
 * @Date 2021/2/8 9:59
 * @Description 描述
 * @Version 1.0
 */
@Component
@Slf4j
public class SummaryJob {
    @Autowired
    AttendanceSummaryService attendanceSummaryService;
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    WeiXinService weiXinService;
    @Scheduled(cron = "${attendance.summary.scheduled}")
    public void doSummaryScheduled(){
        Date d=new Date();
        UserInfo userInfo=new UserInfo();
        String token=attendanceCheckService.login(userInfo);
        String executeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        if(StringUtils.isBlank(token)){
            log.info(String.format("*****************%s at %s attendance.summary.scheduled,login fail*****************",executeTime));
            weiXinService.sendMessageWX("summary scheduled error,because login fail");
            return;
        }
        if(!attendanceSummaryService.hasWriteSummary(d,userInfo.getAccountId(),token)){
            AttendanceWorkSummary attendanceWorkSummary=new AttendanceWorkSummary();
            attendanceWorkSummary.setPlanTomorrow("继续任务");
            attendanceWorkSummary.setSelfScore(5);
            attendanceWorkSummary.setSummaryToday("1.修复系统问题2.优化调整一些代码模块3.部分完成任务");
            attendanceWorkSummary.setToken(token);
            attendanceSummaryService.writeSummary(attendanceWorkSummary);
        }
    }
    @Scheduled(cron = "1 1 22 * * ?")
    public void doSummaryRemind(){
        Date d=new Date();
        UserInfo userInfo=new UserInfo();
        String token=attendanceCheckService.login(userInfo);
        String executeTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        if(StringUtils.isBlank(token)){
            log.info(String.format("*****************%s at %s attendance.summary.scheduled,login fail*****************",executeTime));
            weiXinService.sendMessageWX("summary scheduled error,because login fail");
            return;
        }
        if(!attendanceSummaryService.hasWriteSummary(d,userInfo.getAccountId(),token)){
            weiXinService.sendMessageWX("you do not write summary!click to write..","summary/toSaveSummary");
        }
    }
}
