package com.bfei.icrane.api.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayObject;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.bfei.icrane.api.service.*;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.ChargeOrderService;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.core.service.VipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by SUN on 2018/1/2.
 * 支付宝支付统一下单和回调.
 */
@Controller
@RequestMapping("/ali")
@CrossOrigin
public class AliPayController {

    private static final Logger logger = LoggerFactory.getLogger(AliPayController.class);
    private static final PropFileManager propFileMgr = new PropFileManager("interface.properties");
    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    ChargeOrderService chargeOrderService;//订单服务
    @Autowired
    private PayService payService;
    @Autowired
    private VipService vipService;


    /**
     * 支付宝支付接口
     *
     * @param memberId     用户id
     * @param token        用户token
     * @param chargeruleid 礼包id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap aliPay(String memberId, String token, int chargeruleid, String packageName) throws Exception {
        try {
            logger.info("支付宝订单生成接口token=" + token + "chargeruleid=" + chargeruleid + "memberId=" + memberId);
            boolean isToken = validateTokenService.validataToken(token, Integer.valueOf(memberId));
            if (isToken) {
                //根据礼包id查询礼包
                ChargeRules rule = chargeOrderService.queryRule(chargeruleid);
                //生成订单号
                String out_trade_no = "ali" + UUID.randomUUID().toString().replaceAll("-", "");
                //查询付款规则
                ChargeRules chargeRules = chargeOrderService.queryRule(chargeruleid);
                //查询用户vip信息
                Vip vip = vipService.selectVipByMemberId(Integer.valueOf(memberId));
                double d = rule.getChargePrice() * 0.1 * new BigDecimal(vip.getDiscount()).doubleValue();
                BigDecimal b = new BigDecimal(d);
                d = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (d < 0.01) {
                    d = 0.01;
                }

                //数据库 创建订单
                Integer result = chargeOrderService.createChareOrder(chargeruleid, d, Integer.valueOf(memberId), out_trade_no);
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
                // 商品描述根据情况修改
          /*      if ("xiaoyaojing".equals(packageName)) {
                    if (new BigDecimal(vip.getDiscount()).compareTo(new BigDecimal(10)) < 0) {
                        model.setSubject("小妖精抓抓乐-" + chargeRules.getChargeName() + "-" + chargeRules.getDescription() + "-" + vip.getName() + vip.getDiscount() + "折");
                    } else {
                        model.setSubject("小妖精抓抓乐-" + chargeRules.getChargeName());
                    }
                } else {*/
                    if (new BigDecimal(vip.getDiscount()).compareTo(new BigDecimal(10)) < 0) {
                        model.setSubject("网搜抓娃娃-" + chargeRules.getChargeName() + "-" + chargeRules.getDescription() + "-" + vip.getName() + vip.getDiscount() + "折");
                    } else {
                        model.setSubject("网搜抓娃娃-" + chargeRules.getChargeName());
                    }
              //  }
                //商户订单号
                model.setOutTradeNo(out_trade_no);
                //该笔订单允许的最晚付款时间，逾期将关闭交易。
                //取值范围：1m～15d m-分钟，h-小时，d-天，1c-当天(1c-当天的情况下，无论交易何时创建，都在0点关闭)
                //该参数数值不接受小数点， 如1.5h，可转换为90m。
                model.setTimeoutExpress(Enviroment.ALIPAY_TIMEOUT_EXPRESS);
                //订单金额
                model.setTotalAmount(String.valueOf(d));
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
            } else {
                logger.info("支付宝支付失败：没有授权");
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("支付宝支付出错" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.ALIPAY_ERROR);
        }
    }

    /**
     * 支付宝授权回调接口
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/notify")
    @ResponseBody
    public String alinotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return payService.aliNotify(request);
    }
}
