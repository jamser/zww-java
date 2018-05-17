package com.bfei.icrane.common.util;

import com.bfei.icrane.common.wx.utils.WxConfig;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Properties;

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
    public static String accessToken;
    //小程序accexxToken
    public static String miniappsaccessToken;
    //accessToken的失效时间
    public static Long expiresTime = 0L;
    //小程序accessToken的失效时间
    public static Long miniappsexpiresTime = 0L;
    //获得jssdk需要的ticket
    public static String ticket;
    //ticket的失效时间
    public static Long expiresTime_1 = 0L;


    public static HttpResponse getOauth2(String code, String head) {
        String wxUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxConfig.APPID + "&secret=" + WxConfig.SECRET + "&code=" + code + "&grant_type=authorization_code";
        //logger.info("微信登录请求微信服务器:url={}", wxUrl);
        if (StringUtils.isNotEmpty(head)) {
            if ("老子是H5".equals(head)) {
                wxUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WxConfig.GZHAPPID + "&secret=" + WxConfig.GZHSECRET + "&code=" + code + "&grant_type=authorization_code";
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
    public static String getOauthInfo(String code, String head) {
        //根据code请求用户openid unionid
        HttpResponse response = null;
        try {
            response = getOauth2(code, head);
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
    public static String getAccessToken() {
        String result = null;
        try {
            //如果accessToken为null或者accessToken已经失效就去重新获取(提前10秒)
            if (System.currentTimeMillis() >= expiresTime) {
                //发送http请求
                result = HttpUtil.get(GET_ACCESSTOKEN_URL.replace("APPID", APPID).replace("APPSECRET", SECRET));
                //转成json对象
                JSONObject json = JSONObject.fromObject(result);
                if (!json.has("access_token")) {
                    logger.info("获取accessToken异常" + json.toString());
                    return json.toString();
                }
                accessToken = json.getString("access_token");
                Integer expires_in = json.getInt("expires_in");
                //失效时间=当前时间(毫秒)+7200
                expiresTime = System.currentTimeMillis() + ((expires_in - 60) * 1000);
            }
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
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

    public static String getJSApiTicket() {
        String result = null;
        try {
            if (System.currentTimeMillis() >= expiresTime_1) {
                //发送http请求
                result = HttpUtil.get(GET_TICKET_URL.replace("ACCESS_TOKEN", getAccessToken()));
                //转成json对象
                JSONObject json = JSONObject.fromObject(result);
                if (!json.has("ticket")) {
                    return json.toString();
                }
                ticket = json.getString("ticket");
                Integer expires_in = json.getInt("expires_in");
                //失效时间=当前时间(毫秒)+7200
                expiresTime_1 = System.currentTimeMillis() + ((expires_in - 60) * 1000);
            }
            return ticket;
        } catch (Exception e) {
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
}