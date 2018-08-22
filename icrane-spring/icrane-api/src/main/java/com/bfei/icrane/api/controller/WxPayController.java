package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.AgentService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.api.service.PayService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.common.wx.utils.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.pojos.AccessTokenPojo;
import com.bfei.icrane.core.service.*;
import com.google.gson.Gson;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

/**
 * Author: perry Version: 1.0 Date: 2017/09/25 Description: 微信支付统一下单和回调.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping("/wx")
@CrossOrigin
public class WxPayController {
    private static final Logger logger = LoggerFactory.getLogger(WxPayController.class);
    private static PropFileManager propFileMgr = new PropFileManager("interface.properties");
    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private ChargeOrderService chargeOrderService;//订单服务
    @Autowired
    private MemberService memberService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PayService payService;
    @Autowired
    private VipService vipService;
    @Autowired
    private OemService oemService;
    @Autowired
    private AgentService agentService;

    /**
     * 微信支付
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult wxPay(HttpServletRequest request, int chargeruleid, Double price, int memberId,
                              String token, String IP, String packageName) throws Exception {
        logger.info("微信支付接口参数:规则id" + chargeruleid + ", price=" + price + ", memberId=" + memberId + ", token=" + token + "，IP" + IP);
        try {
            boolean isToken = validateTokenService.validataToken(token, memberId);
            if (isToken) {

                //数据库 创建订单
                Vip vip = vipService.selectVipByMemberId(memberId);
                //总金额以分为单位，不带小数点
                //查询用户vip信息
                ChargeRules rule = chargeOrderService.queryRule(chargeruleid);
                Account account = accountService.selectById(memberId);
                if (rule.getChargeType() == 5) {
                    if (!account.getLover().equals("0")) {
                        return IcraneResult.build(Enviroment.RETURN_FAILE, "400", "不能购买此礼包");
                    }
                }


                Double dprice = rule.getChargePrice();
                if (vip != null) {

                    dprice = dprice * 10 * new BigDecimal(vip.getDiscount()).doubleValue();
                }
                if (dprice < 1) {
                    dprice = 1.0;
                }

                Member member = memberService.selectById(memberId);
                String orderNo = TenpayUtil.getCurrTime() + member.getMemberID();
                Integer result = chargeOrderService.createChareOrder(chargeruleid, dprice / 100, memberId, orderNo);


                if (result == -1) {
                    //return IcraneResult.build(Enviroment.RETURN_SUCCESS, "400", "超过限购次数");
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "超过限购次数");
                } else if (result == -2) {
                    //return IcraneResult.build(Enviroment.RETURN_SUCCESS, "400", "你已购买了时长包");
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "你已购买周卡或者月卡");
                }
                String currTime = TenpayUtil.getCurrTime();
                String strTime = currTime.substring(8, currTime.length());
                // 四位随机数
                String strRandom = TenpayUtil.buildRandom(4) + "";
                // 10位序列号,可以自行调整。
                String strReq = strTime + strRandom;
                // 设备号 非必输
                String device_info = "";
                // 随机数
                String nonce_str = strReq;
                // 商品描述
                String body = "网搜支付";
                // 附加数据
                String attach = memberId + "";
                // 商户订单号
                String out_trade_no = orderNo;


                Oem oem = oemService.selectByCode(member.getRegisterChannel());


                if (new BigDecimal(vip.getDiscount()).compareTo(new BigDecimal(10)) < 0) {
                    body = body + "-" + vip.getName() + vip.getDiscount() + "折";
                }
                price = rule.getChargePrice();
                Integer total_fee = dprice.intValue();// 正式环境下要*100
                // 订单生成的机器 IP
                String spbill_create_ip = IPUtils.getIpAddr(request);
                logger.info("支付ip ={}", spbill_create_ip);
                String notify_url = propFileMgr.getProperty("wx.notify");
                SortedMap<String, String> packageParams = new TreeMap<>();
                packageParams.put("trade_type", "APP");
                packageParams.put("appid", oem.getAppid());
                packageParams.put("mch_id", oem.getPartner());


                if (StringUtils.isNotEmpty(IP) && !"老子是公众号".equals(IP)) {
                    packageParams.put("trade_type", "MWEB");
                    //spbill_create_ip = IP;
                }
                packageParams.put("nonce_str", nonce_str);
                packageParams.put("body", body);
                packageParams.put("attach", attach);
                packageParams.put("out_trade_no", out_trade_no);
                packageParams.put("total_fee", String.valueOf(total_fee));
                packageParams.put("spbill_create_ip", spbill_create_ip);
                packageParams.put("notify_url", notify_url);
                RequestHandler reqHandler = new RequestHandler(null, null);
                reqHandler.init(packageParams.get("appid"), packageParams.get("mch_id"), WxConfig.PARTNERKEY);

                String gzhopenId = memberService.selectGzhopenId(memberId);
                if ("老子是公众号".equals(IP) && StringUtils.isEmpty(gzhopenId)) {
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "公众号支付接口获取预支付订单失败,请先关注网搜抓娃娃公众号");
                }
                if ("老子是公众号".equals(IP)) {
                    packageParams.put("openid", gzhopenId);
                    packageParams.put("trade_type", "JSAPI");
                    packageParams.put("appid", oem.getAppid());
                    packageParams.put("mch_id", oem.getPartner());
                    reqHandler.init(oem.getAppid(), oem.getAppsecret(), oem.getPartnerKey());
                }

                String sign = reqHandler.createSign(packageParams);

                String xml = "<xml>" + "<appid>" + packageParams.get("appid") + "</appid>" + "<mch_id>" + packageParams.get("mch_id")
                        + "</mch_id>" + "<nonce_str>" + nonce_str + "</nonce_str>" + "<sign>" + sign + "</sign>"
                        + "<body><![CDATA[" + body + "]]></body>" + "<attach>" + attach + "</attach>" + "<out_trade_no>"
                        + out_trade_no + "</out_trade_no>" + "<total_fee>" + total_fee + "</total_fee>" + "<spbill_create_ip>" + spbill_create_ip
                        + "</spbill_create_ip>" + "<notify_url>" + notify_url + "</notify_url>" + "<trade_type>"
                        + packageParams.get("trade_type") + "</trade_type>";
                if ("老子是公众号".equals(IP)) {
                    xml = xml + "<openid>" + gzhopenId + "</openid>";

                }
                xml = xml + "</xml>";
                String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                Map map = new GetWxOrderno().getPayNo(createOrderURL, xml);
                String prepay_id = (String) map.get("prepay_id");
                if ("".equals(prepay_id)) {
                    chargeOrderService.orderFailure(orderNo);
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "统一支付接口获取预支付订单出错");
                } else {
                    SortedMap<String, String> finalpackage = new TreeMap<String, String>();
                    String timestamp = Sha1Util.getTimeStamp();
                    String nonceStr2 = nonce_str;
                    if ("老子是公众号".equals(IP)) {
                        finalpackage.put("appId", oem.getAppid());
                        finalpackage.put("timeStamp", timestamp);
                        finalpackage.put("nonceStr", nonceStr2);
                        finalpackage.put("package", "prepay_id=" + prepay_id);
                        finalpackage.put("signType", "MD5");
                        //finalpackage.put("paySign", prepay_id);
                    } else {
                        finalpackage.put("appid", oem.getAppid());
                        finalpackage.put("partnerid", oem.getPartner());
                        finalpackage.put("timestamp", timestamp);
                        finalpackage.put("noncestr", nonceStr2);
                        finalpackage.put("prepayid", prepay_id);
                        finalpackage.put("package", "Sign=WXPay");
                    }
                    String finalsign = reqHandler.createSign(finalpackage);
                    WxPay wxPay = new WxPay();
                    wxPay.setAppId(oem.getAppid());
                    wxPay.setPrepayId(prepay_id);
                    wxPay.setMwebUrl((String) map.get("mweb_url"));
                    wxPay.setTimeStamp(timestamp);
                    wxPay.setNonceStr(nonceStr2);
                    wxPay.setPaySign(finalsign);
                    wxPay.setOutTradeNo(orderNo);
                    logger.info("微信支付统一下单: wxPay=" + wxPay);
                    return IcraneResult.ok(wxPay);
                }
            } else {
                return IcraneResult.build(Enviroment.RETURN_FAILE, "400", "token已失效");
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 支付回调
     */
    @RequestMapping("/notify")
    @ResponseBody
    public String notify(HttpServletRequest request) throws Exception {
        return payService.wxNotify(request);
    }

    /**
     * 微信登录接口
     *
     * @param code          微信登录code
     * @param lastLoginFrom 登陆时间
     * @param channel       渠道
     * @param phoneModel    用户手机型号
     * @param head          H5标记
     * @return 登录结果
     */
//    @RequestMapping(value = "/getAccessToken", method = RequestMethod.POST)
//    @ResponseBody
//    public IcraneResult getAccessToken(HttpServletRequest request, String code, String lastLoginFrom, String channel, String phoneModel, String head, String unionId, String openId, String accessToken, String IMEI) {
//        logger.info("微信登录code=" + code + ",lastLoginFrom=" + lastLoginFrom + ",channel=" + channel + ",phoneModel=" + phoneModel + ",head=" + head + ",unionId=" + unionId + ",openId=" + openId + ",accessToken" + accessToken + ",IMEI=" + IMEI);
//        //检测code
//        /*if (StringUtils.isEmpty(code)) {
//            logger.info("微信登录异常:code为空");
//            return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.CODE_IS_NULL);
//        }*/
//        try {
//            JSONObject object = null;
//            if (StringUtils.isEmpty(unionId) && StringUtils.isEmpty(openId) && StringUtils.isEmpty(accessToken) && StringUtils.isNotEmpty(code)) {
//                // 通过 code 获得 accessToken unionId
//                String state = redisUtil.getString(code);
//                if (state != null) {
//                    return IcraneResult.build(Enviroment.RETURN_SUCCESS, Enviroment.RETURN_SUCCESS_REAPEAT, Enviroment.CODE_REPEAT);
//                }
//                String result = WXUtil.getOauthInfo(code, head, null);
//                if (result != null) {
//                    redisUtil.setString(code, result, 60);
//                    object = JSONObject.fromObject(result);
//                    if ("老子是小程序".equals(head)) {
//                        accessToken = object.getString("session_key");
//                    } else {
//                        accessToken = object.getString("access_token");
//                    }
//                    redisUtil.setString("accessToken" + code, accessToken, 7200);
//                    unionId = object.getString("unionid");
//                    //unionId登录兼容问题
//                    openId = memberService.selectOpenIdByUnionId(unionId);
//                    if (StringUtils.isEmpty(openId)) {
//                        openId = object.getString("openid");
//                    }
//                }
//            } else if (StringUtils.isNotEmpty(unionId) && StringUtils.isNotEmpty(openId) && StringUtils.isNotEmpty(accessToken) && StringUtils.isEmpty(code)) {
//                String s = memberService.selectOpenIdByUnionId(unionId);
//                if (StringUtils.isNotEmpty(s)) {
//                    openId = s;
//                }
//            } else {
//                logger.info("微信登录异常:参数异常");
//                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "参数异常");
//            }
//            //根据openId获取登录信息
//            Member member = memberService.selectByOpenId(openId);
//            logger.info("根据微信OpenId查询用户是否存在:{}", member);
//            if (member == null) {
//                //新用户先注册
//                if (StringUtils.isEmpty(IMEI) || "IMEI".equals(IMEI) || riskManagementService.selectIMEICount(IMEI) < 3) {
//                    member = loginService.wxRegistered(openId, channel, phoneModel, accessToken, lastLoginFrom, unionId, null);
//                } else {
//                    return IcraneResult.build(Enviroment.RETURN_FAILE, "407", "该设备超过注册上限");
//                }
//                if (member == null) {
//                    IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.REGISTRATION_FAILED);
//                }
//            } else if (member != null && StringUtils.isEmpty(memberService.selectOpenIdByUnionId(unionId))) {
//                memberService.insertUnionId(member.getId(), openId, unionId);
//            }
//            //老用户直接登录
//            //登录前记录IMEI和IP]
//            if (IMEI != null && IMEI.length() > 40) {
//                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.ERROR_CODE, Enviroment.IMEI_TO_LONG);
//            }
//            int register = riskManagementService.register(member.getId(), IMEI, HttpClientUtil.getIpAdrress(request));
//            if (register != 1) {
//                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.ERROR_CODE, Enviroment.RISK_CONTROL_ABNORMAL);
//            }
//            return loginService.wxLogin(member, lastLoginFrom, channel, phoneModel);
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.info(e.getMessage());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            logger.info(e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info(e.getMessage());
//
//        }
//        return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.CODE_IS_NULL);
//    }
    @RequestMapping(value = "/checkAccessToken", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult getAccessToken(String accessToken, String refreshToken, String openId) throws Exception {
        logger.info("checkAccessToken参数accessToken=" + accessToken + "," + "refreshToken=" + refreshToken + ","
                + "openId=" + openId);
        String url = "https://api.weixin.qq.com/sns/auth?access_token=" + accessToken + "&openid=" + openId;
        URI uri = URI.create(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri);
        HttpResponse response;
        try {
            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();

                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();

                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
                JSONObject object = JSONObject.fromObject(sb.toString().trim());
                int errorCode = object.getInt("errcode");
                if (errorCode == 0) {
                    return IcraneResult.ok();
                }
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "accessToken无效");
    }

//    /**
//     * 调用微信JS接口的临时票据
//     *
//     * @return jsapiTicket
//     */
//    @RequestMapping(value = "/jsapiTicket", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap jsapiTicket() {
//        WXUtil wxUtil = new WXUtil();
//        String jsApiTicket = wxUtil.getJSApiTicket();
//        ResultMap resultMap = new ResultMap("操作成功");
//        resultMap.setResultData(jsApiTicket);
//        return resultMap;
//    }

    /**
     * 分享接口
     *
     * @return jsapiTicket
     */
    @RequestMapping(value = "/onMenuShareTimeline", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap onMenuShareTimeline(Integer memberId, Integer agentId, String url) {
        Oem oem = null;

        if (!StringUtils.isEmpty(memberId)) {
            Member member = memberService.selectById(memberId);
            oem = oemService.selectByCode(member.getRegisterChannel());
        }

        if (!StringUtils.isEmpty(agentId)) {
            Agent agent = agentService.selectByPrimaryKey(agentId);
            if (!ObjectUtils.isEmpty(agent)) {
                if (agent.getIsOem()) {
                    oem = oemService.selectOemById(agent.getId());
                } else if (agent.getLevel() != 0) {
                    oem = oemService.selectOemById(agent.getAgentId());
                }
            }
        }
        if (null == oem) {
            oem = oemService.selectByCode("lanaokj");
        }

        String currTime = TenpayUtil.getCurrTime();
        //随机字符串
        String noncestr = currTime.substring(8, currTime.length()) + TenpayUtil.buildRandom(4);
        //有效的jsapi_ticket
        WXUtil wxUtil = new WXUtil();
        String jsapi_ticket = wxUtil.getJSApiTicket(oem);
        //timestamp（时间戳）
        String timestamp = Sha1Util.getTimeStamp();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("noncestr", noncestr);
        packageParams.put("jsapi_ticket", jsapi_ticket);
        packageParams.put("timestamp", timestamp);
        packageParams.put("url", url);
        RequestHandler reqHandler = new RequestHandler(null, null);
        String sign = reqHandler.createSha1Sign(packageParams);

        if (url.contains(oem.getUrl())) {
            url = null;
        } else {
            url = oem.getUrl() + org.apache.commons.lang3.StringUtils.substringAfter(url, "fun");
        }
        packageParams.put("url", url);
        packageParams.put("sign", sign);
        packageParams.put("appId", oem.getAppid());
        packageParams.put("host", oem.getUrl());
        packageParams.put("icon", oem.getIcon());
        return new ResultMap("", packageParams);
    }


    /**
     * 支付查询
     *
     * @return jsapiTicket
     */
    @RequestMapping(value = "/queryOrder", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap queryOrder(@RequestParam String outTradeNo) {
        return payService.queryOrder(outTradeNo);
    }


    /**
     * 获取getAccessToken
     *
     * @return jsapiTicket
     */
    @RequestMapping(value = "/getAccessToken", method = RequestMethod.POST)
    @ResponseBody
    public String getAccessToken() {
        List<Oem> oems = oemService.selectAllOem();
        List<AccessTokenPojo> accessTokenPojos = new ArrayList<>();
        for (Oem oem : oems) {
            AccessTokenPojo accessTokenPojo = new AccessTokenPojo();
            accessTokenPojo.setCode(oem.getCode());
            accessTokenPojo.setAccessToken(WXUtil.getAccessToken(oem));
            accessTokenPojos.add(accessTokenPojo);
        }
        Gson gson = new Gson();
        return gson.toJson(accessTokenPojos);
    }


}