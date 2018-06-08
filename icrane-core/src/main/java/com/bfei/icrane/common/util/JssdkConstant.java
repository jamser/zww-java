package com.bfei.icrane.common.util;

/**
 * 页面调用微信js-sdk接口的全局变量，在系统启动时设置
 * accessToken和 jsapiTicket 在 TokenThread.java 进程中设置
 * */
public class JssdkConstant {

    public static String accessToken ;//
    public static String jsTicket;
    public static final String timestamp = "1419835029"; // 必填，生成签名的时间戳
    public static final String nonceStr = "82693e11-a9bc-448e-892f-f5209f49cd0f"; // 必填，生成签名的随机串
    public static String signature;// 必填，签名
}
