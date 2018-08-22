package com.bfei.icrane.common.util;

import com.bfei.icrane.common.wx.utils.WxConfig;
import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.models.vo.AccessTokenVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SUN on 2017/12/27.
 */
public class WXUtil {
    private static final Logger logger = LoggerFactory.getLogger(WXUtil.class);
    private RedisUtil redisUtil = new RedisUtil();
    public static String TOKEN;
    public static String APPID;
    public static String SECRET;
    public static String URLBody;
    public static String XCXAPPID;
    public static String XCXSECRET;

    static {
        try {
            TOKEN = WxConfig.GZHTOKEN;
            APPID = WxConfig.GZHAPPID;
            SECRET = WxConfig.GZHSECRET;
            URLBody = WxConfig.GZHURLBODY;
            XCXAPPID = WxConfig.XCXAPPID;
            XCXSECRET = WxConfig.XCXSECRET;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取基础ACCESSTOKEN的URL
    public static final String GET_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    //获取用户信息的URL(需要关注公众号)
    public static final String GET_USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    //创建自定义菜单的URL
    public static final String CREATEMENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
    //发送模板消息的URL
    public static final String SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    //获取网页版的ACCESSTOKEN的URL
    public static final String GET_WEB_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    //通过网页获取用户信息的URL(不需要关注公众号)
    public static final String GET_WEB_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    //获取jssdk使用的ticket
    public static final String GET_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    //获得网络授权
    public static final String GET_NET_CODE_URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE#wechat_redirect";
    //accexxToken
    public static Map<String, AccessTokenVO> accessTokenMap = new HashMap<>();
    //小程序accexxToken
    public static String miniappsaccessToken;
    //小程序accessToken的失效时间
    public static Long miniappsexpiresTime = 0L;


    public static HttpResponse getOauth2(String code, String head, Oem oem) {
        String wxUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + oem.getAppid() + "&secret=" + oem.getAppsecret() + "&code=" + code + "&grant_type=authorization_code";
        //logger.info("微信登录请求微信服务器:url={}", wxUrl);
        if (StringUtils.isNotEmpty(head)) {
            if ("老子是H5".equals(head)) {
                wxUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + oem.getAppid() + "&secret=" + oem.getAppsecret() + "&code=" + code + "&grant_type=authorization_code";
            } else if ("老子是小程序".equals(head)) {
                wxUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxConfig.XCXAPPID + "&secret=" + WxConfig.XCXSECRET + "&js_code=" + code + "&grant_type=authorization_code";
            }
        }
        //logger.info("微信登录请求微信服务器:url={}", wxUrl);
        URI url = URI.create(wxUrl);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;


    }

    /**
     * 获得  JSONObject 对象信息 accessToken unionid
     *
     * @return
     */
    public static String getOauthInfo(String code, String head, Oem oem) {
        //根据code请求用户openid unionid
        HttpResponse response = null;
        try {
            response = getOauth2(code, head, oem);
            //响应参数
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("请求微信服务器响应结果(200表示成功):{}", statusCode);
            //请求微信服务器响应成功
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
                //JSONObject object = JSONObject.fromObject(sb.toString().trim());
                logger.info("微信登录返回的json:{}", sb);
                return sb.toString().trim();
            }
        } catch (Exception e) {
            logger.error("微信登录异常:获取openid失败");
            // e.printStackTrace();
            return null;
        }
        return null;
    }

    public static HttpResponse getSns(String accessToken, String openId) {
        String getUserInfoUri = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken
                + "&openid=" + openId;
        //logger.info("获取微信用户信息Url:{}", getUserInfoUri);
        HttpGet getUserInfo = new HttpGet(URI.create(getUserInfoUri));
        URI url = URI.create(getUserInfoUri);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONObject batchget_material(Oem oem) {
        // String getUserInfoUri = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";
        String getUserInfoUri = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=";

        JSONObject json = new JSONObject();
     /*   json.put("type","image");
        json.put("offset","0");
        json.put("count","10");*/
        json.put("media_id", "PNI789FlvjpUMDO4QGnspZa4hM3YrhWj_27fN4ZfzlQ");
        try {
            String accessToken = getAccessToken(oem);
            if (StringUtils.isEmpty(accessToken)) {
                return null;
            }
            String url_shorts = getUserInfoUri + accessToken;
            String msg = doPost(json.toString(), url_shorts, "POST");
            return JSONObject.fromObject(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 模拟get请求
     * @param url
     * @param charset
     * @param timeout
     * @return
     */
    public static String sendGet(String url, String charset, int timeout) {
        String result = "";
        try {
            URL u = new URL(url);
            try {
                URLConnection conn = u.openConnection();
                conn.connect();
                conn.setConnectTimeout(timeout);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                String line = "";
                while ((line = in.readLine()) != null) {

                    result = result + line;
                }
                in.close();
            } catch (IOException e) {
                return result;
            }
        } catch (MalformedURLException e) {
            return result;
        }

        return result;
    }


    /**
     * 获取AccessToken
     *
     * @return
     */
    public static String getAccessToken(Oem oem) {
        try {
            //如果accessToken为null或者accessToken已经失效就去重新获取(提前10秒)
            AccessTokenVO accessTokenVO = accessTokenMap.get(oem.getCode());

            if (null == accessTokenVO || System.currentTimeMillis() >= accessTokenVO.getExpires_in_access()) {
                accessTokenVO = new AccessTokenVO();
                String result = HttpUtil.get(GET_ACCESSTOKEN_URL.replace("APPID", oem.getAppid()).replace("APPSECRET", oem.getAppsecret()));
                //转成json对象
                JSONObject json = JSONObject.fromObject(result);
                if (!json.has("access_token")) {
                    logger.info("获取accessToken异常" + json.toString());
                    return null;
                }
                Integer expires_in = json.getInt("expires_in");
                //失效时间=当前时间(毫秒)+7200
                accessTokenVO.setAccess_token(json.getString("access_token"));
                accessTokenVO.setExpires_in_access(System.currentTimeMillis() + ((expires_in - 60) * 1000));

                accessTokenMap.put(oem.getCode(), accessTokenVO);
            }

            return accessTokenVO.getAccess_token();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getJSApiTicket(Oem oem) {
        try {
            AccessTokenVO accessTokenVO = accessTokenMap.get(oem.getCode());

            if (null == accessTokenVO || null == accessTokenVO.getTicket() || System.currentTimeMillis() >= accessTokenVO.getExpires_in_ticket()) {
                String accessToken = getAccessToken(oem);
                if (StringUtils.isEmpty(accessToken)) {
                    return null;
                }

                String result = HttpUtil.get(GET_TICKET_URL.replace("ACCESS_TOKEN", accessToken));
                //转成json对象
                JSONObject json = JSONObject.fromObject(result);
                if (!json.has("ticket")) {
                    return null;
                }
                Integer expires_in = json.getInt("expires_in");
                //失效时间=当前时间(毫秒)+7200
                accessTokenVO = accessTokenMap.get(oem.getCode());
                accessTokenVO.setTicket(json.getString("ticket"));
                accessTokenVO.setExpires_in_ticket(System.currentTimeMillis() + ((expires_in - 60) * 1000));

                accessTokenMap.put(oem.getCode(), accessTokenVO);
            }
            return accessTokenVO.getTicket();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取短连接
     *
     * @param longurl
     * @return
     */
    public static String short_url(String longurl, Oem oem) {
        String url_short = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=";
        JSONObject resp = new JSONObject();
        try {
            resp.put("action", "long2short");
            resp.put("long_url", longurl);
            String accessToken = getAccessToken(oem);
            if (StringUtils.isEmpty(accessToken)) {
                return longurl;
            }
            String url_shorts = url_short + accessToken;
            String msg = doPost(resp.toString(), url_shorts, "POST");
            JSONObject resp4 = JSONObject.fromObject(msg);
            if (resp4.getInt("errcode") == 0) {
                logger.info("获取短连接" + resp);
                return resp4.getString("short_url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return longurl;
    }


    public static String qrcode(String ecode, Oem oem) {
        String url_ticket = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
        JSONObject resp = new JSONObject();
        JSONObject resp2 = new JSONObject();
        JSONObject resp3 = new JSONObject();
        resp3.put("scene_str", ecode);
        resp2.put("scene", resp3);
        resp.put("action_info", resp2);
        resp.put("action_name", "QR_LIMIT_STR_SCENE");
        try {
            String accessToken = getAccessToken(oem);
            if (StringUtils.isEmpty(accessToken)) {
                return null;
            }
            String url_tickets = url_ticket + accessToken;
            String msg = doPost(resp.toString(), url_tickets, "POST");
            JSONObject resp4 = JSONObject.fromObject(msg);
            // System.out.println(resp4.toString());
            if (resp4.containsKey("url")) {
                return resp4.getString("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        try {
//            String jsonStr = WXUtil.doPost("", "http://lanao.nat300.top/icrane/api/wx/getAccessToken", "POST");
//            //  [{ "code": "lanaokj","accessToken": "UxAjDRGUOMJjAEAFDA" }, {"code": "lanaocs", "accessToken": "7zMvA15BMujIRQhAJAQZY" }]
//            System.out.println("jsonStr = {}" + jsonStr);
//            JSONArray jsonArr = JSONArray.fromObject(jsonStr);
//            for (int i = 0; i < jsonArr.size(); i++) {
//                JSONObject json = jsonArr.getJSONObject(i);
//                String code = json.getString("code");
//                String accessToken = json.getString("accessToken");
//                if (!StringUtils.isEmpty(accessToken) && "lanaokj".equals(code)) {
//                    JSONObject jsonsend = sendTemplate("ItetnetU0PZPL2i0pW96XFyFYS2qBMr8uUvxAcqKQSc", "http://weixin.qq.com/download", "o_-591nmBVcc6SxgaoMb_jrC08L8", accessToken
//                            , "订单发货通知", "恭喜您支付成功! 预估五分钟后到帐，如有疑问请返回至在线客服联系我们或致电0551-62675556", "1213223", "Q币10元充值", "2件", "17.2元", format(new Date(), "yyyy-MM-dd"));
//                    System.out.println(jsonsend);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 发送模板消息
     *
     * @param templateId 模板id
     * @param openid     接收者openid
     * @return
     */
    public static JSONObject sendTemplate(String templateId, Oem oem, String openid,String url
            , String first, String remark, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) throws Exception {
        String info = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
        String accessToken = getAccessToken(oem);
        info = info.replace("ACCESS_TOKEN", accessToken);
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        json.put("touser", openid);
        json.put("template_id", templateId);
        json.put("url", url);
        if (!StringUtils.isEmpty(first)) {
            data.put("first", getJson(first, "#173177"));
        }
        if (!StringUtils.isEmpty(keyword1)) {
            data.put("keyword1", getJson(keyword1, "#173177"));
        }
        if (!StringUtils.isEmpty(keyword2)) {
            data.put("keyword2", getJson(keyword2, "#173177"));
        }
       if (!StringUtils.isEmpty(keyword3)) {
            data.put("keyword3", getJson(keyword3, "#173177"));
        }
        if (!StringUtils.isEmpty(keyword4)) {
            data.put("keyword4", getJson(keyword4, "#173177"));
        }
        if (!StringUtils.isEmpty(keyword5)) {
            data.put("keyword5", getJson(keyword5, "#173177"));
        }
        if (!StringUtils.isEmpty(remark)) {
            data.put("remark", getJson(remark, "#173177"));
        }
        json.put("data", data);
        System.out.println(json.toString());
        String msg = doPost(json.toString(), info, "POST");
        JSONObject resp4 = JSONObject.fromObject(msg);
        if (resp4.getInt("errcode") == 0 && "ok".equals(resp4.getString("errmsg"))) {
            return resp4;
        } else {
            logger.error("发送模板消息异常:" + resp4.toString());
        }
        return null;
    }

    private static JSONObject getJson(String value, String color) {
        JSONObject jsons = new JSONObject();
        jsons.put("value", value);
        jsons.put("color", color);
        return jsons;
    }


    public static Integer createMenu(Oem oem) {
        try {
            String accessToken = getAccessToken(oem);
            if (StringUtils.isEmpty(accessToken)) {
                return null;
            }
            String URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
            JSONObject json = new JSONObject();
            JSONObject json1 = new JSONObject();
            JSONArray jar1 = new JSONArray();
            //菜单栏一：
            json1.put("name", "抓娃娃");
            json1.put("type", "view");
            json1.put("key", "ceshi");
            json1.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + oem.getAppid()
                    + "&redirect_uri=http%3A%2F%2Flanao.nat300.top/icrane/api/WeChatLogin&response_type=code&scope=snsapi_userinfo&state=" + oem.getCode() + "#wechat_redirect");
            jar1.add(0, json1);
            //菜单栏二：
            json1 = new JSONObject();
            json1.put("name", "代理商中心");
            json1.put("type", "view");
            json1.put("key", "ceshi");
            json1.put("url", oem.getUrl() + "/lanaokj/personal.html");
            jar1.add(1, json1);
            json.put("button", jar1);
            String msg = doPost(json.toString(), URL, "POST");
            JSONObject resp4 = JSONObject.fromObject(msg);
            return resp4.getInt("errcode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public static JSONObject getUserInfo(String openid, Oem oem) {
        String info = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        try {
            String accessToken = getAccessToken(oem);
            if (StringUtils.isEmpty(accessToken)) {
                return null;
            }
            logger.info("getUserInfo==>accessToken={}", accessToken);
            info = info.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
            logger.info("getUserInfo==>info={}", info);
            String msg = doPost("", info, "GET");
            JSONObject resp4 = JSONObject.fromObject(msg);
            if (!resp4.containsKey("errcode")) {
                resp4.put("access_token", accessToken);
                return resp4;
            } else {
                logger.info("获取获取用户信息异常:" + resp4.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否为周末
     *
     * @param date
     * @return
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }


    /**
     * 获取AccessToken
     *
     * @return
     */
    public static String getMiniAppsAccessToken() {
        String result = null;
        try {
            //如果accessToken为null或者accessToken已经失效就去重新获取(提前10秒)
            if (System.currentTimeMillis() >= miniappsexpiresTime) {
                //发送http请求
                result = HttpUtil.get(GET_ACCESSTOKEN_URL.replace("APPID", XCXAPPID).replace("APPSECRET", XCXSECRET));
                //转成json对象
                JSONObject json = JSONObject.fromObject(result);
                if (!json.has("access_token")) {
                    logger.info("获取accessToken异常" + json.toString());
                    return json.toString();
                }
                miniappsaccessToken = json.getString("access_token");
                Integer expires_in = json.getInt("expires_in");
                //失效时间=当前时间(毫秒)+7200
                miniappsexpiresTime = System.currentTimeMillis() + ((expires_in - 60) * 1000);
            }
            return miniappsaccessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:21.0) Gecko/20100101 Firefox/21.0)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            OutputStreamWriter outWriter = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            out = new PrintWriter(outWriter);
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String doPost(String pa, String url, String method) throws Exception {
        String parameterData = pa;

        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(parameterData.length()));

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream, "utf-8");

            outputStreamWriter.write(parameterData.toString());
            outputStreamWriter.flush();
            inputStream = httpURLConnection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            reader = new BufferedReader(inputStreamReader);
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }

        } finally {

            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer.toString();
    }

    /**
     * 判断是否为节假日
     *
     * @param date 日期
     * @return 工作日对应结果为 0, 休息日对应结果为 1, 节假日对应的结果为 2
     */
    public static Integer isHostory(Date date) {
        try {
            SimpleDateFormat sim = new SimpleDateFormat("yyyyMMdd");
            String url = "http://tool.bitefu.net/jiari/?d=" + sim.format(date);
            String json = WXUtil.doPost("", url, "GET");
            return Integer.parseInt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}