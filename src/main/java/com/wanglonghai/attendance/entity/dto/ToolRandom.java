package com.wanglonghai.attendance.entity.dto;

import com.wanglonghai.attendance.entity.TimeEnum;
import com.wanglonghai.attendance.entity.UserInfo;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @ClassName ToolRandom
 * @Author Gavin
 * @Date 2021/2/5 17:03
 * @Description 描述
 * @Version 1.0
 */
@Component
public class ToolRandom {
    //随机秒数
    @Value("${attendance.scheduled.seconds}")
    private String scheduledSeconds;
    //早上打卡 分钟范围取值
    @Value("${attendance.scheduled.morningMinute}")
    private String morningMinute;
    //中午打卡 分钟范围取值
    @Value("${attendance.scheduled.noonMinute}")
    private String noonMinute;
    //晚上打卡 分钟范围取值
    @Value("${attendance.scheduled.afternoonMinute}")
    private String afternoonMinute;
    Map<TimeEnum,TimeEnumDto> randoms=new HashMap<>();
    @PostConstruct
    public void Init(){
        randoms.put(TimeEnum.Morning,new TimeEnumDto(TimeEnum.Morning,morningMinute,scheduledSeconds).resetRandomStand());
        randoms.put(TimeEnum.Noon,new TimeEnumDto(TimeEnum.Noon,noonMinute,scheduledSeconds).resetRandomStand());
        randoms.put(TimeEnum.Afternoon,new TimeEnumDto(TimeEnum.Afternoon,afternoonMinute,scheduledSeconds).resetRandomStand());
    }
    //标记日期已经执行过考勤
    public void markDate(TimeEnum timeEnum,String dateStr){
        randoms.get(timeEnum).getMarkedDate().add(dateStr);
        //当天执行过了，重新设置一个随机数，方便第二天执行
        randoms.get(timeEnum).resetRandomStand();
    }
    //检测某日期执行过
    public Boolean judgeDate(TimeEnum timeEnum,String dateStr){
        if(StringUtils.isBlank(dateStr)){
            return false;
        }
        return randoms.get(timeEnum).getMarkedDate().contains(dateStr);
    }
    public Integer getMinute(TimeEnum timeEnum){
        return randoms.get(timeEnum).getRandomMinuteValue();
    }
    public Integer getSecond(TimeEnum timeEnum){
        return randoms.get(timeEnum).getRandomSecondValue();
    }
    public static void main(String[] args) {
        for(int i=0;i<100;i++){
            System.out.println(new Random().nextInt(20)+1);
        }
    }
 }
