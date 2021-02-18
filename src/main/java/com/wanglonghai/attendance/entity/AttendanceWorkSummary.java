package com.wanglonghai.attendance.entity;
import lombok.Data;
@Data
public class AttendanceWorkSummary {
	/**
	 * 明日计划
	 */
	private String planTomorrow;
	/**
	 * 自评分
	 */
	private Integer selfScore;
	/**
	 * 今日成长与问题（今日总结）
	 */
	private String summaryToday;
	private String token;
	private String message;
	private String openId;
}
