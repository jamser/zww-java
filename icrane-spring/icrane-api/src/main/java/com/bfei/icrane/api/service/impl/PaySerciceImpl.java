package com.bfei.icrane.api.service.impl;


import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayObject;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.bfei.icrane.api.controller.WxPayController;
import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.api.service.PayService;
import com.bfei.icrane.api.service.WxpayRecordService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.common.wx.utils.*;
import com.bfei.icrane.core.dao.ChargeDao;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.ChargeOrderService;
import com.bfei.icrane.core.service.OemService;
import com.bfei.icrane.core.service.OemTemplateService;
import com.github.binarywang.wxpay.bean.result.WxPayBillResult;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by SUN on 2018/1/9.
 */
@Service("PaySerciceImpl")
@Transactional
public class PaySerciceImpl implements PayService {
    private static final Logger logger = LoggerFactory.getLogger(PaySerciceImpl.class);
    private static final PropFileManager propFileMgr = new PropFileManager("interface.properties");

    @Autowired
    private ChargeOrderService chargeOrderService;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private WxpayRecordService wxpayRecordService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private OemService oemService;
    @Autowired
    private OemTemplateService oemTemplateService;

    /*@Override
    public ResultMap wxpay(HttpServletRequest request, int memberId, int chargeruleid) {
        try {

            String orderNo = UUID.randomUUID().toString().replaceAll("-", "");
            //数据库 创建订单
            Integer result = chargeOrderService.createChareOrder(chargeruleid, memberId, orderNo);
            if (result == -1) {
                //return IcraneResult.build(Enviroment.RETURN_SUCCESS, "400", "超过限购次数");
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, "超过限购次数");
            } else if (result == -2) {
                //return IcraneResult.build(Enviroment.RETURN_SUCCESS, "400", "你已购买了时长包");
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, "你已购买周卡或者月卡");
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
            // 商品描述根据情况修改
            String body = "网搜支付";
            // 附加数据
            String attach = memberId + "";
            // 商户订单号
            String out_trade_no = orderNo;
            // 总金额以分为单位，不带小数点
            ChargeRules rule = chargeOrderService.queryRule(chargeruleid);
            Double price = rule.getChargePrice();
            Double dprice = price * 100;
            Integer total_fee = dprice.intValue();// 正式环境下要*100
            // 订单生成的机器 IP
            String spbill_create_ip = request.getRemoteAddr();
            String notify_url = propFileMgr.getProperty("wx.notify");
            String trade_type = "APP";

            SortedMap<String, String> packageParams = new TreeMap<>();
            packageParams.put("appid", WxConfig.APPID);
            packageParams.put("mch_id", WxConfig.PARTNER);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("attach", attach);
            packageParams.put("out_trade_no", out_trade_no);
            packageParams.put("total_fee", String.valueOf(total_fee));
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", notify_url);
            packageParams.put("trade_type", trade_type);
            RequestHandler reqHandler = new RequestHandler(null, null);
            reqHandler.init(WxConfig.APPID, WxConfig.PARTNER, WxConfig.PARTNERKEY);

            String sign = reqHandler.createSign(packageParams);

            String xml = "<xml>" + "<appid>" + WxConfig.APPID + "</appid>" + "<mch_id>" + WxConfig.PARTNER
                    + "</mch_id>" + "<nonce_str>" + nonce_str + "</nonce_str>" + "<sign>" + sign + "</sign>"
                    + "<body><![CDATA[" + body + "]]></body>" + "<attach>" + attach + "</attach>" + "<out_trade_no>"
                    + out_trade_no + "</out_trade_no>" +
                    // 金额，这里写的1 分到时修改
                    "<total_fee>" + total_fee + "</total_fee>" + "<spbill_create_ip>" + spbill_create_ip
                    + "</spbill_create_ip>" + "<notify_url>" + notify_url + "</notify_url>" + "<trade_type>"
                    + trade_type + "</trade_type>" + "</xml>";
            String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            Map map = new GetWxOrderno().getPayNo(createOrderURL, xml);
            String prepay_id = (String) map.get("prepay_id");
            if (StringUtils.isEmpty(map.get("prepay_id"))) {
                chargeOrderService.orderFailure(orderNo);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, "统一支付接口获取预支付订单出错");
            } else {
                SortedMap<String, String> finalpackage = new TreeMap<String, String>();
                String timestamp = Sha1Util.getTimeStamp();
                String nonceStr2 = nonce_str;
                finalpackage.put("appid", WxConfig.APPID);
                finalpackage.put("partnerid", WxConfig.PARTNER);
                finalpackage.put("timestamp", timestamp);
                finalpackage.put("noncestr", nonceStr2);
                finalpackage.put("prepayid", prepay_id);
                finalpackage.put("package", "Sign=WXPay");
                String finalsign = reqHandler.createSign(finalpackage);
                WxPay wxPay = new WxPay();
                wxPay.setPrepayId(prepay_id);
                wxPay.setTimeStamp(timestamp);
                wxPay.setNonceStr(nonceStr2);
                wxPay.setPaySign(finalsign);
                wxPay.setOutTradeNo(orderNo);
                logger.info("微信支付统一下单: wxPay=" + wxPay);
                ResultMap resultMap = new ResultMap(Enviroment.WXPAY_SUCCESSFULLY_PLACED);
                resultMap.setResultData(wxPay);
                return resultMap;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public ResultMap alipay(int memberId, int chargeruleid) {
        try {
            //根据礼包id查询礼包
            ChargeRules rule = chargeOrderService.queryRule(chargeruleid);
            //生成订单号
            String out_trade_no = "ali" + UUID.randomUUID().toString().replaceAll("-", "");
            //查询付款规则
            ChargeRules chargeRules = chargeOrderService.queryRule(chargeruleid);
            //数据库 创建订单
            Integer result = chargeOrderService.createChareOrder(chargeruleid, Integer.valueOf(memberId), out_trade_no);
            if (result == -1) {
                logger.info("超过限购次数:memberId=" + memberId);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.EXCEED_THE_LIMIT);
            } else if (result == -2) {
                logger.info("已购买周卡或者月卡:memberId=" + memberId);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.HAVE_BOUGHT_CARD);
            }
            //实例化客户端
            AlipayClient alipayClient = new DefaultAlipayClient(propFileMgr.getProperty("alipay.URL"), propFileMgr.getProperty("alipay.APPID"), propFileMgr.getProperty("alipay.RSA_PRIVATE_KEY"), propFileMgr.getProperty("alipay.FORMAT"), propFileMgr.getProperty("alipay.CHARSET"), propFileMgr.getProperty("alipay.ALIPAY_PUBLIC_KEY"), propFileMgr.getProperty("alipay.SIGNTYPE"));
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            //商品详情
            model.setBody(chargeRules.getChargeName() + "-" + chargeRules.getDescription());
            //商品类型1：实物交易；0：虚拟交易。默认为1（实物交易）。
            model.setGoodsType("0");
            //商品名称
            model.setSubject("网搜抓娃娃-" + chargeRules.getChargeName() + "-" + chargeRules.getDescription());
            //商户订单号
            model.setOutTradeNo(out_trade_no);
            //该笔订单允许的最晚付款时间，逾期将关闭交易。
            //取值范围：1m～15d m-分钟，h-小时，d-天，1c-当天(1c-当天的情况下，无论交易何时创建，都在0点关闭)
            //该参数数值不接受小数点， 如1.5h，可转换为90m。
            model.setTimeoutExpress(Enviroment.ALIPAY_TIMEOUT_EXPRESS);
            //订单金额
            model.setTotalAmount(String.valueOf(rule.getChargePrice()));
            //model.setTotalAmount("0.01");
            //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
            model.setProductCode("QUICK_MSECURITY_PAY");
            //业务请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递
            request.setBizModel(model);
            AlipayObject bizModel = request.getBizModel();
            //支付宝服务器主动通知商户服务器里指定的页面http/https路径。建议商户使用https
            request.setNotifyUrl(propFileMgr.getProperty("alipay.NOTIFY"));
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            logger.info("支付宝支付统一下单: aliPay=" + response.getBody());
            ResultMap resultMap = new ResultMap(Enviroment.ALIPAY_SUCCESSFULLY_PLACED);
            HashMap<String, Object> map = new HashMap<>();
            map.put("responsebody", response.getBody());
            map.put("outtradeno", out_trade_no);
            resultMap.setResultData(map);
            return resultMap;
        } catch (Exception e) {
            logger.error("支付宝支付出错" + e.getMessage());
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.ALIPAY_ERROR);
        }
    }*/


    /**
     * 微信充值回调接口
     *
     * @return
     */
    @Override
    @Transactional
    public String wxNotify(HttpServletRequest request) {
        try {

            SortedMap<String, String> mapXml = null;
            try {
                mapXml = MessageUtil.dom4jXMLParse(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //logger.info("充值通知" + mapXml.toString());
            //创建支付应答对象
            RequestHandler reqHandler = new RequestHandler(null, null);
            if (reqHandler.isWechatSign(mapXml)) {
                logger.info("微信充值回调...", mapXml);
                if (mapXml.get("result_code").equalsIgnoreCase("SUCCESS")
                        && mapXml.get("return_code").equalsIgnoreCase("SUCCESS")) {
                    // 处理业务，更新用户余额或金币
                    int memberId = Integer.parseInt(mapXml.get("attach"));
                    double fee = Double.parseDouble(mapXml.get("total_fee")) / 100;

                /*Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("memberId", memberId);
                parameterMap.put("mchOrderNo", mapXml.get("out_trade_no"));
                parameterMap.put("wxOrderNo", mapXml.get("transaction_id"));
                parameterMap.put("openId", mapXml.get("openid"));
                parameterMap.put("totalFee", fee);
                parameterMap.put("timeEnd", mapXml.get("time_end"));
                parameterMap.put("chargeFrom", "weixin");
                parameterMap.put("result", 0);
                chargeDao.payNotify(parameterMap);*/
                    //更新充值订单状态
                    ChargeOrder order = chargeOrderService.orderSuccess(mapXml.get("out_trade_no"), fee);
                    if (order == null) {
                        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    }
                    Charge charge = new Charge();
                    charge.setMemberId(memberId);
                    charge.setCoins(order.getCoinsBefore());
                    charge.setCoinsSum(order.getCoinsCharge() + order.getCoinsOffer());
                    charge.setPrepaidAmt(order.getPrice());
                    charge.setSuperTicket(order.getSuperTicketBefore());
                    charge.setSuperTicketSum(order.getSuperTicketCharge() + order.getSuperTicketOffer());
                    charge.setType("income");
                    charge.setChargeDate(TimeUtil.getTime());
                    charge.setChargeMethod("微信充值-" + order.getChargeName());
                    Integer result = chargeService.insertChargeHistory(charge);
//                    chargeService.inviteChargeFirst(memberId);//检测邀请 首充赠送


                    //微信充值记录
                    WxpayRecord wr = new WxpayRecord();
                    wr.setMemberId(memberId);
                    wr.setMchOrderNo(mapXml.get("out_trade_no"));
                    wr.setWxOrderNo(mapXml.get("transaction_id"));
                    wr.setOpenId(mapXml.get("openid"));
                    wr.setTotalFee(fee + "");
                    wr.setTimeEnd(mapXml.get("time_end"));
                    wr.setChargeFrom("weixin");
                    if (wxpayRecordService.selectByOutTradeNo(mapXml.get("out_trade_no")) == null) {
                        int insert = wxpayRecordService.insert(wr);
                        logger.info("充值记录结果:insertResult=" + insert);
                    }
                    //发送模版支付成功
                    sendWxPaySuccessTemplate(order);
                    if (result == 1) {
                        logger.info("异步返回 SUCCESS");
                        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                    }
                }
            }
            logger.info("签名校验失敗");
            return null;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    private void sendWxPaySuccessTemplate(ChargeOrder order) {
        Member member = memberService.selectById(order.getMemberId());
        Oem oem = oemService.selectByCode(member.getRegisterChannel());
        OemTemplate oemTemplate = oemTemplateService.selectByOemIdAndType(oem.getId(), Enviroment.WXPAY_TEMPLATE);
        try {
            WXUtil.sendTemplate(oemTemplate.getTemplateId(), oem, member.getWeixinId(), null, "你好，你的微信支付已成功", null,
                    order.getOrderNo(), order.getPrice() + "元", oem.getName(), WXUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String aliNotify(HttpServletRequest request) {
        try {
            //获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }
            //logger.info("支付宝授权回调接口返回的信息:" + params);
            //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
            //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //异步通知ID
            String notify_id = request.getParameter("notify_id");
            //sign
            String sign = request.getParameter("sign");
            if (notify_id != "" && notify_id != null) {
                if ("true".equals(AlipayNotify.verifyResponse(notify_id)))//判断成功之后使用getResponse方法判断是否是支付宝发来的异步通知。
                {
                    if (
                            AlipayNotify.getSignVeryfy(params, sign)// 使用支付宝公钥验签
                            ) {
                        if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                            // 处理业务，更新用户余额或金币
                            int memberId = chargeOrderService.selectmemberIdByOrder_no(out_trade_no);
                            //微信充值记录
                            WxpayRecord wr = new WxpayRecord();
                            wr.setMemberId(memberId);
                            wr.setMchOrderNo(out_trade_no);
                            wr.setWxOrderNo(trade_no);
                            wr.setOpenId(params.get("buyer_logon_id"));
                            wr.setTotalFee(params.get("total_amount"));
                            wr.setTimeEnd(params.get("notify_time"));
                            wr.setChargeFrom("ali");
                            if (wxpayRecordService.selectByOutTradeNo(out_trade_no) == null) {
                                int insert = wxpayRecordService.insert(wr);
                                logger.info("充值记录结果:insertResult=" + insert);
                            }

                            //更新充值订单状态
                            ChargeOrder order = chargeOrderService.orderSuccess(out_trade_no, null);//订单支付成功
                            int coinsSum = order.getCoinsCharge() + order.getCoinsOffer();
                            int superTicketSum = order.getSuperTicketCharge() + order.getSuperTicketOffer();
                            Charge charge = new Charge();
                            charge.setMemberId(memberId);
                            charge.setCoinsSum(coinsSum);
                            charge.setCoins(order.getCoinsBefore());
                            charge.setSuperTicketSum(superTicketSum);
                            charge.setSuperTicket(order.getSuperTicketBefore());
                            charge.setPrepaidAmt(order.getPrice());
                            charge.setType("income");
                            charge.setChargeDate(TimeUtil.getTime());
                            charge.setChargeMethod("支付宝充值-" + order.getChargeName());
                            //logger.info("充值参数charge=" + charge);
                            //加钱
                            Integer result = chargeService.insertChargeHistory(charge);
                            //logger.info("充值结果result=" + result);
                            chargeService.inviteChargeFirst(memberId);//检测邀请 首充赠送
                            if (result == 1) {
                                logger.info("支付宝授权回调接口返回的结果:success");
                                return "success";
                            }
                        }
                    } else//验证签名失败
                    {
                        chargeOrderService.orderFailure(out_trade_no);
                        logger.info("支付宝授权回调接口返回的结果:sign fail验证签名失败");
                        return "sign fail";
                    }
                } else//验证是否来自支付宝的通知失败
                {
                    chargeOrderService.orderFailure(out_trade_no);
                    logger.info("支付宝授权回调接口返回的结果:response fail验证是否来自支付宝的通知失败");
                    return "response fail";
                }
            } else {
                chargeOrderService.orderFailure(out_trade_no);
                logger.info("支付宝授权回调接口返回的结果:no notify message");
                return "no notify message";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        logger.info("支付宝授权回调接口返回的异常:error message");
        return "error message";
    }

    @Autowired
    private WxPayService wxPayService;

    @Override
    public ResultMap queryOrder(String outTradeNo) {
        ChargeOrder chargeOrder = chargeOrderService.selectByOrderNo(outTradeNo);
        Member member = memberService.selectById(chargeOrder.getMemberId());
        Oem oem = oemService.selectByCode(member.getRegisterChannel());
        WxPayConfig wxPayConfig = new WxPayConfig();
        wxPayConfig.setAppId(oem.getAppid());
        wxPayConfig.setMchId(oem.getPartner());
        wxPayConfig.setMchKey(oem.getPartnerKey());
        wxPayService.setConfig(wxPayConfig);
        XStream xStream = new XStream();
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypes(new Class[]{WxPayController.class});
        WxpayRecord wxpayRecord = wxpayRecordService.selectByOutTradeNo(outTradeNo);
        try {
//            WxPayBillResult all = wxPayService.downloadBill("20180710", "ALL", null, null);
            WxPayOrderQueryResult result = wxPayService.queryOrder(wxpayRecord.getWxOrderNo(), null);
            if (StringUtils.success(result.getResultCode()) && StringUtils.success(result.getReturnCode())) {
                if (StringUtils.success(result.getTradeState())) {
                    return new ResultMap(result.getTradeState());
                }
            }

        } catch (WxPayException e) {
            e.printStackTrace();
        }
        return null;
    }
}
