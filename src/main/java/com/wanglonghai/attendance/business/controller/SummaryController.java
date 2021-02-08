package com.wanglonghai.attendance.business.controller;

import com.wanglonghai.attendance.business.Service.AttendanceCheckService;
import com.wanglonghai.attendance.business.Service.AttendanceSummaryService;
import com.wanglonghai.attendance.business.Service.WeiXinService;
import com.wanglonghai.attendance.entity.AttendanceWorkSummary;
import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.dto.ToolRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Controller
@RequestMapping(value = "/summary")
@Slf4j
public class SummaryController {
    @Autowired
    ToolRandom toolRandom;
    @Autowired
    WeiXinService weiXinService;
    @Autowired
    AttendanceCheckService attendanceCheckService;
    @Autowired
    AttendanceSummaryService attendanceSummaryService;
    @PostMapping(value ="/saveSummary")
    @ResponseBody
    public String saveSummary(AttendanceWorkSummary attendanceWorkSummary){
        if(attendanceWorkSummary==null){
            attendanceWorkSummary=new AttendanceWorkSummary();
        }
        if(StringUtils.isBlank(attendanceWorkSummary.getSummaryToday())){
            attendanceWorkSummary.setSummaryToday("1.修复系统问题2.优化调整一些代码模块3.部分完成任务");
        }
        attendanceWorkSummary.setSelfScore(5);
        String token=attendanceCheckService.login(null);
        if(StringUtils.isBlank(token)){
            return "summary fail,login fail!";
        }
        attendanceWorkSummary.setToken(token);
        attendanceSummaryService.writeSummary(attendanceWorkSummary);
        return attendanceWorkSummary.getMessage();
    }
    @GetMapping(value = "/toSaveSummary")
    public String toSavePage(){
        return "WriteSummary";
    }
}
