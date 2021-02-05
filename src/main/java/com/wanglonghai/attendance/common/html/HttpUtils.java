package com.wanglonghai.attendance.common.html;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.wanglonghai.attendance.common.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * apache httpClient utils
 */
public class HttpUtils {

    private String serverUrl;
    private int connectTimeout = 15000;
    private int readTimeout = 30000;

    public HttpUtils(String serverUrl) {
        this.serverUrl = serverUrl.trim();
    }

    final static Logger log = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 执行请求
     * @param serviceUrl
     * @param paramers
     * @return
     * @throws Exception
     */
    public Map<String, Object> commonService(String serviceUrl, HttpParamers paramers) throws Exception {
        return commonService(serviceUrl, paramers, paramers.getHttpHeader());
    }

    /**
     * 执行请求
     * @param serviceUrl
     * @param paramers
     * @param header
     * @return
     * @throws Exception
     */
    public Map<String, Object> commonService(String serviceUrl, HttpParamers paramers, HttpHeader header) throws Exception {
        String response = service(serviceUrl, paramers, header);
        try {
            Map<String, Object> result = JSONObject.parseObject(response, new TypeReference<Map<String, Object>>() {
            });
            if ((result == null) || (result.isEmpty())) {
                throw new Exception("远程服务返回的数据无法解析");
            }
            return result;
        }catch (JSONException jsex){
            Map<String,Object> r=new HashMap<>();
            r.put("data",response);
            return r;
        }
        catch (Exception e) {
            throw new Exception("返回结果异常,response:" + response, e);
        }
    }

    public String service(String serviceUrl, HttpParamers paramers) throws Exception {
        return service(serviceUrl, paramers, paramers.getHttpHeader());
    }

    public String service(String serviceUrl, HttpParamers paramers, HttpHeader header) throws Exception {
        String url = this.serverUrl + serviceUrl;
        String responseData = "";
        try {
            responseData = HttpClient.doService(url, paramers, header, this.connectTimeout, this.readTimeout);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        return responseData;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    /**
     * 执行请求
     * @param url
     * @param params
     * @return
     */
    public static Map<String, Object> doRequest(String url,HttpParamers params){
        log.info(" ======> url:"+url);
        log.info(" ======> params:"+params.toString());
        Map<String, Object> response = new HashMap<>();
        try {
            HttpUtils httpService = new HttpUtils("");
            response = httpService.commonService(url,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //log.info(" ======> results:" + response);
        return response;
    }
    /**
     * 执行请求，结合调用微信接口需要的一些参数
     * @param
     * @return
     */
    public static Map<String, Object> doWeChatRequest(String url,HttpParamers params,
                                                      String wx_appid,String wx_secret){
        //参数是否需要参与签名判断
        boolean paramsIsNeedSign=(params.getHttpMethod()== HttpMethod.GET)||
                (params.getHttpMethod()== HttpMethod.POST&&!params.isJson());
        StringBuffer sb = new StringBuffer();
        if(paramsIsNeedSign){
            Map<String, String> p=params.getParams();
            //首先对参数的键 排序
            SortedMap<String, String> sortedMap = new TreeMap<>(p);
            Set<String> keySet = sortedMap.keySet();
            for (String key : keySet) {
                sb.append(key + "=");
                sb.append(sortedMap.get(key)+ "&");
            }
        }
        sb.append("key=").append(wx_secret);
        log.info("重构调用微信服务加密前的："+sb.toString());
        // sha1加密
        String aesHex = EncryptUtil.sha1Encode( sb.toString().trim());
        //md5加密
        //String aesHex = EncryptUtil.md5Encode( sb.toString().trim());
        params.addHeader("appid",wx_appid);
        params.addHeader("sign",aesHex);
        log.info("重构调用微信服务加密后的："+aesHex);
        Map<String, Object> res =  doRequest(url,params);
        return res;
    }
}
