package com.wanglonghai.attendance.business.controller;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.UserInfo;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import com.wanglonghai.attendance.entity.dto.UserList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName AttendanceController
 * @Author Gavin
 * @Date 2021/2/7 11:09
 * @Description 描述
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/wanglonghai")
@Slf4j
public class AttendanceController {
    @Autowired
    ToolRandom toolRandom;
    @Autowired
    WeiXinService weiXinService;
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    UserList userList;
    @GetMapping(value ="/testAlive")
    public String testAlive(){
        weiXinService.sendMessageWX("I am alive from controller!",userList.getList().get(0).getOpenId());
        return "I am alive from controller!";
    }
    @GetMapping(value ="/doAttendance")
    public String doAttendance(String timeTip){
        TimeEnum timeEnum=TimeEnum.valueOfStr(timeTip);
        String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String resultStr="未知";
        UserInfo userInfo=userList.getList().get(0);
        if(!toolRandom.judgeDate(timeEnum,dateStr)){
            log.info(String.format("*****************manual attendance start,attendanceTime: %s %s*****************",dateStr,timeEnum.getCode()));
            attendanceCheckService.login(userInfo);
            if(StringUtils.isNotBlank(userInfo.getTk())){
                Boolean result=attendanceCheckService.getQrCode(userInfo)
                        &&attendanceCheckService.dk(userInfo);
                toolRandom.markDate(timeEnum,dateStr);
                resultStr=String.format("*****************%s-%s,attendance %s*****************",dateStr,timeEnum.getCode(),result?"success":"fail!");;
            }else{
                resultStr=String.format("*****************%s-%s,attendance fail,login fail*****************",dateStr,timeEnum.getCode());;
            }
            log.info(resultStr);
        }else{
            resultStr=String.format("*****************已经打卡了*****************",timeEnum.getCode());
            log.warn(resultStr);
        }
        return resultStr;
    }
}
