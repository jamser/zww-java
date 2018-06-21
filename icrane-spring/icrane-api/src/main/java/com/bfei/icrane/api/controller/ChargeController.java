package com.bfei.icrane.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bfei.icrane.common.util.*;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.service.ValidateTokenService;
/**
 * 充值
 */
@Controller
@RequestMapping(value = "/charge")
@CrossOrigin
public class ChargeController {
    private static final Logger logger = LoggerFactory.getLogger(ChargeController.class);

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ValidateTokenService validateTokenService;

    // 充值
    @RequestMapping(value = "/markCharge", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> markCharge(Integer memberId, Double chargePrice, String token) throws Exception {
        logger.info("充值接口参数：" + "memberId=" + memberId + "," + "chargePrice=" + chargePrice + "," + "token=" + token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            //if (token == null || "".equals(token) || !validateTokenService.validataToken(token)) {
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            Member member = memberService.selectById(memberId);
            Charge charge = chargeService.getChargeRules(chargePrice);
            charge.setMemberId(memberId);
            charge.setCoins(member.getAccount().getCoins());
            //charge.setCoins(charge.getCoinsCharge() + charge.getCoinsOffer());
            charge.setCoinsSum(charge.getCoinsCharge() + charge.getCoinsOffer());
            charge.setPrepaidAmt(charge.getChargePrice());
            charge.setType("income");
            charge.setChargeDate(TimeUtil.getTime());
            logger.info("充值参数charge=" + "memberId " + charge.getMemberId() + "coins " + charge.getCoins() + "coinsSum " + charge.getCoinsSum() + "prepaidAmt " + charge.getPrepaidAmt());
            Integer result = chargeService.insertChargeHistory(charge);
            logger.info("充值结果result=" + result);
            if (result == 1) {
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("充值结果resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("充值出错", e);
            throw e;
        }
    }

    /**
     * 充值历史
     *
     * @param memberId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/chargeHistory", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap chargeHistory(@RequestParam Integer memberId, @RequestParam String token) throws Exception {
        logger.info("充值历史接口参数：" + "memberId=" + memberId + "," + "token=" + token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                logger.info("获取充值历史传入异常" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            List<Charge> chargeHistorys = chargeService.getChargeHistory(memberId);
            if (chargeHistorys.size() != 0) {
                List<Map<String, Object>> chargeList = new ArrayList<Map<String, Object>>();
                for (Charge chargeHistory : chargeHistorys) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("prepaidAmt", chargeHistory.getPrepaidAmt());
                    map.put("coins", chargeHistory.getCoins());
                    map.put("chargeDate", chargeHistory.getChargeDate());
                    map.put("chargeMethod", chargeHistory.getChargeMethod());
                    map.put("type", chargeHistory.getType());
                    chargeList.add(map);
                }
                logger.info("返回前端需要的充值历史");
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, chargeList);
            } else {
                logger.info("返回前端需要的充值历史失败" + Enviroment.RETURN_FAILE_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("充值历史出错", e);
            throw e;
        }
    }

    /**
     * 成功充值后查询充值记录
     *
     * @param memberId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/successfulRechargeRecords", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap SuccessfulRechargeRecords(Integer memberId, String token, String mchOrderNo) throws Exception {
        logger.info("发送成功充值后查询充值记录接口参数mobile=" + memberId + ",mchOrderNo=" + mchOrderNo);
        try {
            //参数验证
            if (memberId == null && "".equals(memberId) && token == null && "".equals(token) && mchOrderNo == null && "".equals(mchOrderNo)) {
                logger.info("成功充值后查询充值记录接口=" + memberId + ",mchOrderNo=" + mchOrderNo);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //授权验证
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("成功充值后查询充值记录接口=" + memberId + ",mchOrderNo=" + mchOrderNo);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return chargeService.getSuccessfulRechargeRecords(memberId, mchOrderNo);
        } catch (Exception e) {
            logger.debug("成功充值后查询充值记录异常" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, "查询异常");
        }
    }

    /**
     * 兑换娃娃币
     *
     * @param memberId
     * @param orderIds
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/redeemCoins", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap redeemCoins(Integer memberId, @RequestParam("orderIds") Long[] orderIds, String token) throws Exception {
        logger.info("兑换娃娃币接口参数：memberId=" + memberId + "," + "dollId=" + orderIds);
        try {
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token) || orderIds == null || orderIds.length < 1) {
                logger.info("兑换娃娃币接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //访问间隔限制
            RedisUtil redisUtil = new RedisUtil();
            if (redisUtil.getString("redeemCoins" + memberId) != null) {
                logger.info("兑换娃娃币失败=" + Enviroment.PLEASE_SLOW_DOWN);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.PLEASE_SLOW_DOWN);
            }
            redisUtil.setString("redeemCoins" + memberId, "", Enviroment.ACCESS_SENDDOLL_TIME);
            // 验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Integer countSum = chargeService.insertChargeHistory(new Charge(), memberId, orderIds);
            logger.info("兑换结果result=" + countSum);
            if (countSum >= 0) {
                logger.info("兑换娃娃币countSum=" + countSum);
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, countSum);
            } else {
                logger.info("兑换娃娃币失败" + Enviroment.RETURN_FAILE_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("兑换娃娃币", e);
            throw e;
        }
    }

    /**
     * 绑定邀请好友
     *
     * @param memberId   用户ID
     * @param inviteCode 邀请人邀请码
     * @param token      token
     * @return 调用接口
     * @throws Exception
     */
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap invite(Integer memberId, String inviteCode, String token) throws Exception {
        try {
            logger.info("邀请好友接口参数：" + "memberId=" + memberId + "," + "inviteCode=" + inviteCode + "," + "token=" + token);
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(inviteCode)) {
                logger.info("邀请好友接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //访问间隔限制
            RedisUtil redisUtil = new RedisUtil();
            if (redisUtil.getString("invite" + memberId) != null || redisUtil.getString("invite" + inviteCode) != null) {
                logger.info("邀请好友失败=" + Enviroment.PLEASE_SLOW_DOWN);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PLEASE_SLOW_DOWN);
            }
            redisUtil.setString("invite" + memberId, "", Enviroment.ACCESS_CONTROL_TIME);
            redisUtil.setString("invite" + inviteCode, "", Enviroment.ACCESS_CONTROL_TIME);
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("邀请好友接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return chargeService.invite(memberId, inviteCode);
        } catch (Exception e) {
            logger.error("邀请失败异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 展示邀请人
     *
     * @param memberId 用户ID
     * @param token    token
     * @return 调用接口
     * @throws Exception
     */
    @RequestMapping(value = "/whoInvite", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap whoInvite(Integer memberId, String token) throws Exception {
        try {
            logger.info("展示邀请人接口参数：" + "memberId=" + memberId + "," + "token=" + token);
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("展示邀请人接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("展示邀请人接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return chargeService.whoInvite(memberId);
        } catch (Exception e) {
            logger.error("展示邀请人失败异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 展示邀请人数
     *
     * @param memberId 用户ID
     * @param token    token
     * @return 调用接口
     * @throws Exception
     */
    @RequestMapping(value = "/howInvite", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap howInvite(Integer memberId, String token) throws Exception {
        try {
            logger.info("展示邀请人数接口参数：" + "memberId=" + memberId + "," + "token=" + token);
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("展示邀请人数接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("展示邀请人数接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return chargeService.howInvite(memberId);
        } catch (Exception e) {
            logger.error("展示邀请人数失败异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }
}
