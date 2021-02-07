package com.wanglonghai.attendance.entity.dto;

import com.wanglonghai.attendance.entity.TimeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @ClassName TimeEnumDto
 * @Author Gavin
 * @Date 2021/2/7 9:39
 * @Description 描述
 * @Version 1.0
 */
@Data
@Slf4j
public class TimeEnumDto {
    private TimeEnum timeEnum;
    //打过卡的日期（如2021-12-05）
    private List<String> markedDate=new ArrayList<>();
    //分钟范围
    private String minuteRange;
    //秒范围
    private String secondRange;
    private List<Integer> timeMinuteValues=new ArrayList<>();
    private List<Integer> timeSecondsValues=new ArrayList<>();
    //分钟随机数种子
    private Integer randomMinuteStand=0;
    //秒随机数种子
    private Integer randomSecondStand=0;

    public TimeEnumDto(TimeEnum timeEnum, String minuteRange,String secondRange) {
        this.timeEnum = timeEnum;
        this.minuteRange = minuteRange;
        this.secondRange = secondRange;
    }

    /**
     * 所有秒值列表（从中随机取数）
     * @return
     */
    private List<Integer> getTimeSecondValues() {
        if(timeSecondsValues.size()==0&&StringUtils.isNotBlank(secondRange)){
            String[] values=secondRange.split(",");
            Arrays.asList(values).forEach(s->{
                timeSecondsValues.add(Integer.valueOf(s));
            });
        }
        return timeSecondsValues;
    }

    /**
     * 所有分钟列表（从中随机取数）
     * @return
     */
    private List<Integer> getTimeMinuteValues() {
        if(timeMinuteValues.size()==0&&StringUtils.isNotBlank(minuteRange)){
           Integer min=Integer.valueOf(minuteRange.split("-")[0]);
           Integer max=Integer.valueOf(minuteRange.split("-")[1]);
           for(int i=min;i<=max;i++){
               timeMinuteValues.add(i);
           }
        }
        return timeMinuteValues;
    }
    private Integer getRandomStand(List<Integer> values,Integer oldRandStand){
        if(values.size()>0){
            //int randNumber =rand.nextInt(MAX - MIN + 1) + MIN;
            //取值范围[0,integers.size()-1]
            Integer newRandom=new Random().nextInt(values.size());
            //保证每次都与上次不相等
            while (newRandom.equals(oldRandStand)){
                newRandom=new Random().nextInt(values.size());
            }
            return newRandom;
        }
        return oldRandStand;
    }
    /**
     * 刷新随机种子
     * @return
     */
    public TimeEnumDto resetRandomStand() {
        randomMinuteStand=getRandomStand(getTimeMinuteValues(),randomMinuteStand);
        Integer currentMinute= Calendar.getInstance().get(Calendar.MINUTE);
//        //保证取到的分钟值大于当前时间分钟值，否则时间将永不命中
//        //如果这边报内存堆栈溢出，那么请重新配置分钟范围时间
//        if(randomMinuteStand<getTimeMinuteValues().size()&&getTimeMinuteValues().get(randomMinuteStand)<=currentMinute){
//            log.warn("随机打卡分钟值( "+getTimeMinuteValues().get(randomMinuteStand)+")小于当前分钟("+currentMinute);
//            log.warn("启动项目出现，将嗝屁，请避免启动项目时执行到此处");
//        }
        randomSecondStand=getRandomStand(getTimeSecondsValues(),randomSecondStand);
        return this;
    }

    /**
     * 获取随机分钟时间
     * @return
     */
    public Integer getRandomMinuteValue(){
        List<Integer> integers=getTimeMinuteValues();
        if(integers.size()>0){
            return integers.get(randomMinuteStand);
        }
        return 1;
    }
    /**
     * 获取随机秒时间
     * @return
     */
    public Integer getRandomSecondValue(){
        List<Integer> integers=getTimeSecondValues();
        if(integers.size()>0){
            return integers.get(randomSecondStand);
        }
        return 1;
    }
}
