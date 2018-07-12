package com.bfei.icrane.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/16 Description: Methods used to
 * manipulate string. Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public class StringUtils {

    public static String replace(final String sourceString, Object[] object) {

        String temp = sourceString;
        for (int i = 0; i < object.length; i++) {
            String[] result = (String[]) object[i];
            Pattern pattern = Pattern.compile(result[0]);
            Matcher matcher = pattern.matcher(temp);
            temp = matcher.replaceAll(result[1]);
        }
        return temp;
    }

    // convert integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    // convert xx:xx:xx to integer
    public static int timeToSec(String time) {
        int min = 0;
        String strs[] = time.split(":");
        if (strs[0].compareTo("0") > 0) {
            min += Integer.valueOf(strs[0]) * 60 * 60;// 秒
        }
        if (strs[1].compareTo("0") > 0) {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

    public static String getRandomUUID() {
        // 创建 GUID 对象
        UUID uuid = UUID.randomUUID();
        // 得到对象产生的ID
        return uuid.toString();
    }

    /**
     * 微信登录token
     *
     * @return
     */
    public static String getWxToken() {
        return "wx_" + getRandomUUID();
    }

    /**
     * 代理登录token
     *
     * @return
     */
    public static String getAgentToken() {
        return "agent_" + getRandomUUID();
    }


    /**
     * 手机登录token
     *
     * @return
     */
    public static String getPhoneToken() {
        return "ph_" + getRandomUUID();
    }


    public static String getCatchHistoryNum() {
        return getRandomUUID();
    }

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException     没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        // 加密后的字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }

    public static String getMemberCode() {
        StringBuilder str = new StringBuilder();// 定义变长字符串
        Random random = new Random();// 随机生成数字，并添加到字符串
        for (int i = 0; i < 8; i++) {
            str.append(random.nextInt(10));
        }
        // 将字符串转换为数字并输出
        // int num=Integer.parseInt(str.toString());

        return str.toString();
    }

    public static String getSmsCode() {
        StringBuilder str = new StringBuilder();// 定义变长字符串
        Random random = new Random();// 随机生成数字，并添加到字符串
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        // 将字符串转换为数字并输出
        // int num=Integer.parseInt(str.toString());
        return str.toString();
    }

    /**
     * 函数名称: parseData 函数描述: 将json字符串转换为map
     *
     * @param data
     * @return
     */
    public static Map<String, String> parseData(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }

    public static String getOrderNumber() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(date);

        StringBuilder str = new StringBuilder();// 定义变长字符串
        Random random = new Random();// 随机生成数字，并添加到字符串
        for (int i = 0; i < 5; i++) {
            str.append(random.nextInt(10));
        }
        // 将字符串转换为数字并输出
        // int num=Integer.parseInt(str.toString());
        return time + str.toString();
    }

    public static String getClawNumber() {
        return getNumber("claw_");
    }

    public static String getNumber(String biz) {
        StringBuilder str = new StringBuilder();// 定义变长字符串
        Random random = new Random();// 随机生成数字，并添加到字符串
        for (int i = 0; i < 8; i++) {
            str.append(random.nextInt(10));
        }
        // 将字符串转换为数字并输出
        // int num=Integer.parseInt(str.toString());
        return biz + str.toString();
    }

    //判断值是否为空   为空返回true   不为空返回false
    public static boolean isEmpty(Object obj) {
        if (obj == null || "".equals(obj)) {
            return true;
        }
        return false;
    }

    public static boolean isAgentEmpty(Integer obj) {
        if (obj == null || obj == 0) {
            return true;
        }
        return false;
    }

    //判断值是否为空   为空返回false   不为空返回true
    public static boolean isNotEmpty(Object obj) {
        if (obj != null && !"".equals(obj)) {
            return true;
        }
        return false;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean success(String str) {
        if (isNotEmpty(str)) {
            if ("SUCCESS".equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取请求数据
     *
     * @param request
     * @return
     * @throws IOException
     * @作者 bruce
     * @创建时间 2017年5月10日
     */
    public static String getRequestData(HttpServletRequest request) throws IOException {
        StringBuffer sbJson = new StringBuffer();
        String jsonStr = null;
        String line = null;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            sbJson.append(line);
        }
        jsonStr = sbJson.toString();
        return jsonStr;
    }

}
