package com.wanglonghai.attendance.business.Service;

import com.wanglonghai.attendance.entity.AttendanceWorkSummary;

import java.util.Date;

/**
 * @ClassName AttendanceCheckService
 * @Author Gavin
 * @Date 2021/2/5 14:12
 * @Description 描述
 * @Version 1.0
 */
public interface AttendanceSummaryService {
    Boolean writeSummary(AttendanceWorkSummary attendanceWorkSummary);

    /**
     * 判断是否写日志
     * @param date 时间格式 yyyy-MM-dd 00:00:00
     * @param summaryPersonId 人员
     * @return
     */
    Boolean hasWriteSummary(Date date, Long summaryPersonId,String token);
}
