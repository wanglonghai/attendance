package com.wanglonghai.attendance.business.controller;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.common.WebUtil;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.UserInfo;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import com.wanglonghai.attendance.entity.dto.UserList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
        attendanceCheckService.login(userList.getList().get(0));
        String res=attendanceCheckService.mainTainToken(userList.getList().get(0).getTk());
        return "I am alive from controller!，"+res;
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

    @GetMapping(value ="/doAttendanceManual")
    public String doAttendanceManual(String timeTip,HttpServletRequest request){
        String yy=attendanceCheckService.getQrLoginCodeUrl();
        String code=yy.split("=")[1];
        if(!attendanceCheckService.doQrCodeLogin(code)){
            String dd= WebUtil.getWebUri(request)+"/wanglonghai/doAttendanceManualReal?timeTip="+timeTip+"&qrCode="+code;
            String checkUrl="<a href=\""+dd+"\">打卡，"+timeTip+"</a></br>";
            return "<a href=\""+yy+"\">首先点击授权"+"</a></br>"+checkUrl+dd+"</br>授权地址："+yy;
        }else{
            return doAttendanceManual(timeTip,code);
        }

    }
    @GetMapping(value ="/doAttendanceManualReal")
    public String doAttendanceManual(String timeTip,String qrCode){
        String dateStr=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if(StringUtils.isBlank(timeTip)||"null".equalsIgnoreCase(timeTip)){
            return "未指定打卡时间段";
        }
        TimeEnum timeEnum=TimeEnum.valueOfStr(timeTip);
        UserInfo userInfo=userList.getList().get(0);
        userInfo.setQrCode(qrCode);
        String resultStr;
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
