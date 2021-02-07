package com.wanglonghai.attendance.business.Job;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName AttendanceJob
 * @Author Gavin
 * @Date 2021/2/5 10:05
 * @Description 描述
 * @Version 1.0
 */
@Component
@Slf4j
public class AttendanceJob {
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    WeiXinService weiXinService;
    @Autowired
    ToolRandom toolRandom;
    /**
     *
     */
    @Scheduled(cron = "${attendance.scheduled.morning}")
    public void doAttendanceMorning(){
        doAttendanceRange(TimeEnum.Morning);
    }
    @Scheduled(cron = "${attendance.scheduled.noon}")
    public void doAttendanceNoon(){
        doAttendanceRange(TimeEnum.Noon);
    }
    //@Scheduled(cron = "1 20-35 18 * * ?")
    @Scheduled(cron = "${attendance.scheduled.afternoon}")
    public void doAttendanceAfterNoon(){
        doAttendanceRange(TimeEnum.Afternoon);
    }
    @Scheduled(cron = "1 1 7,8,11,18,19 * * ?")
    public void testAlive(){
        weiXinService.sendMessageWX("I am alive!");
    }
    private void doAttendanceRange(TimeEnum tip){
        String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        log.info(String.format("*****************%s-%s,waiting attendance*****************",dateStr,tip.getCode()));
        Integer doMinute=toolRandom.getMinute(tip);
        Integer doSecond=toolRandom.getSecond(tip);
        Integer nowMinute=Calendar.getInstance().get(Calendar.MINUTE);
        Integer nowSecond=Calendar.getInstance().get(Calendar.SECOND);
        //当天未执行过
        if (!toolRandom.judgeDate(tip,dateStr)){
            // 时间符合
            if(nowMinute.equals(doMinute)&&nowSecond.equals(doSecond)){
                // 执行打卡
                doAttendance(tip.getCode(),String.format("~~~打卡时间:%s:%s",doMinute,doSecond));
                toolRandom.markDate(tip,dateStr);
            }else{
                log.info(String.format("~~~打卡时间:%s:%s,现在时间:%s:%s",doMinute,doSecond,nowMinute,nowSecond));
                log.info(String.format("~~~attendanceTime:%s:%s,nowTime:%s:%s",doMinute,doSecond,nowMinute,nowSecond));
                log.info("~~~~~~~~~~~~executor  not hitting~~~~~~~~~~~~");
                log.info("~~~~~~~~~~~~时间未命中，不执行~~~~~~~~~~~~");
            }
        }else{
            log.info(dateStr+"-"+tip.getCode()+"~~~~~~~~~~~~is executed~~~~~~~~~~~~");
        }
    }
    private void doAttendance(String tip,String doTime){
        log.info(String.format("*****************%s,Scheduled start,attendanceTime: %s*****************",tip,doTime));
        String token=attendanceCheckService.login();
        if(StringUtils.isNotBlank(token)){
            Boolean result=attendanceCheckService.getQrCode(token)
                    &&attendanceCheckService.dk(token);
        }
        log.info(String.format("*****************%s,Scheduled end*****************",tip));
    }
}
