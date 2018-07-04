package com.bfei.icrane.common.util;


import com.bfei.icrane.core.models.XmlMessageEntity;
import com.baidu.aip.speech.AipSpeech;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by SUN on 2017/11/1.
 */
public class SampleUtil {

    //设置APPID/AK/SK
    public static final String APP_ID = "10311630";
    public static final String API_KEY = "rvgGGRUECnqNYy24YTr9RhmB";
    public static final String SECRET_KEY = "5XrKgzFbbLbdLHb9yZgcckN0PmaZN9iq";
    public static final AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

//    public static String doem(XmlMessageEntity entity, XmlMessageEntity newEntity) {
//        client.setConnectionTimeoutInMillis(2000);
//        client.setSocketTimeoutInMillis(60000);
//
//        String savePath = MediaUtil.downloadMedia(WXUtil.getAccessToken(null), entity.getMediaId(), "/");
//        JSONObject asrRes = client.asr(savePath, "amr", 8000, null);
//        (new File(savePath)).delete();
//        String s = null;
//        try {
//            s = asrRes.getJSONArray("result").get(0).toString();
//        } catch (Exception e) {
//            return "老子没听清";
//        }
//        // 对本地语音文件进行识别
//        return s.substring(0, s.length() - 1);
//    }

}
