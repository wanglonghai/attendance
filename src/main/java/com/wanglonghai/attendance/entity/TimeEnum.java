package com.wanglonghai.attendance.entity;

import org.apache.commons.lang3.StringUtils;

import java.sql.Time;

public enum TimeEnum {

    Morning("morning"),
    Noon("noon"),
    Afternoon("afternoon");


    TimeEnum(String code){
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }
    public static TimeEnum valueOfStr(String tip){
        if(StringUtils.isNotBlank(tip)){
            switch (tip){
                case "morning":return TimeEnum.Morning;
                case "noon":return TimeEnum.Noon;
                case "afternoon":return TimeEnum.Afternoon;
            }
        }
        return TimeEnum.Morning;
    }
}