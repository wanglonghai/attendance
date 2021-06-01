package com.wanglonghai.attendance.common;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public class WebUtil {
    public static String getWebUri(HttpServletRequest request){
        StringBuffer url = request.getRequestURL();
        String tempContextUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
        return tempContextUrl;
    }
    public static String getUrlPath(HttpServletRequest request){
        String path = request.getServletPath();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> keySet = parameterMap.keySet();
        StringBuffer requestParams = new StringBuffer();
        for (String key : keySet) {
            requestParams.append(key + "=");
            requestParams.append(parameterMap.get(key)[0] + "&");
        }
        String requestParamsStr="";
        if(requestParams.length()>0){
            requestParamsStr=requestParams.toString();
            requestParamsStr="?"+requestParamsStr.substring(0,requestParamsStr.length()-1);
        }
        return path+requestParamsStr;
    }
}
