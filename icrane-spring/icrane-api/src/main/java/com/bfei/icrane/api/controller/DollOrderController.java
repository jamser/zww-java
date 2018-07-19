package com.bfei.icrane.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.SystemPrefDao;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderItem;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.game.GameProcessEnum;
import com.bfei.icrane.game.GameProcessUtil;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/27 Description: 娃娃订单控制层. Copyright
 * (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/doll/order")
@CrossOrigin
public class DollOrderController {

    private static final Logger logger = LoggerFactory.getLogger(DollOrderController.class);

    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private DollOrderService dollOrderService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private SystemPrefDao systemPrefDao;

    private RedisUtil redisUtil = new RedisUtil();

    // 获取娃娃订单详情
    @RequestMapping(value = "/getById", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getOrderById(HttpServletRequest request) throws Exception {
        logger.info("获取娃娃订单详情接口传入参数orderId=" + request.getParameter("orderId"));
        Map<String, Object> resultMap = new HashedMap<String, Object>();

        try {
            //验证token有效性
            if (request.getParameter("token") == null ||
                    "".equals(request.getParameter("token")) ||
                    !validateTokenService.validataToken(request.getParameter("token"))) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return resultMap;
            }
            if (request.getParameter("orderId") == null || "".equals(request.getParameter("orderId"))) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return resultMap;
            }

            DollOrder dollOrder = dollOrderService.selectByPrimaryKey(Long.valueOf(request.getParameter("orderId")));
            if (dollOrder != null) {
                resultMap.put("resultData", dollOrder);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else if (dollOrder == null) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("获取娃娃订单详情resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃订单出错", e);
            throw e;
        }

    }

    /**
     * 抓取历史列表
     *
     * @param token       身份令牌
     * @param memberId    账号ID
     * @param orderStatus 历史类型
     * @return 抓取历史列表
     * @throws Exception
     */
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getOrderItemsByMemberId(String token, String memberId, String orderStatus) throws Exception {
        try {
            logger.info("获取娃娃订单详情token=" + token + ",memberId=" + memberId + ",orderStatus=" + orderStatus);
            if (memberId == null || "".equals(memberId) || token == null || "".equals(token)) {
                logger.info("获取娃娃订单详情异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token有效性
            if (!validateTokenService.validataToken(token, Integer.parseInt(memberId))) {
                logger.info("获取娃娃订单详情异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            List<DollOrderItem> dollOrderItems;
            if (StringUtils.isEmpty(orderStatus)) {
                dollOrderItems = dollOrderService.selectItemsByMemberId(Integer.parseInt(memberId));
            } else {
                dollOrderItems = dollOrderService.selectItemsByMemberIdOrderStatus(Integer.parseInt(memberId), orderStatus);
            }
            ResultMap resultMap = new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
            resultMap.setResultData(dollOrderItems);
            //logger.info("获取娃娃订单详情resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃订单详情出错", e);
            throw e;
        }

    }

    // 创建娃娃订单
    @RequestMapping(value = "/createDollOrder", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> createDollOrder(Integer memberId, Integer dollId, Integer dollNum) throws Exception {
        logger.info("【createDollOrder】参数 memberId={},dollId={},dollNum={}", memberId, dollId, dollNum);
        Map<String, Object> resultMap = new HashedMap<String, Object>();

        try {
            //Integer num = 0;
            //num = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberSetter(memberId))) + 1;
            //redisUtil.setString(RedisKeyGenerator.getMemberSetter(memberId), String.valueOf(num), 3600 * 2);
            //优化订单 下单 计数控制   控制代码集中
            Integer num = GameProcessUtil.getInstance().addCountGameLock(memberId, dollId, GameProcessEnum.GAME_SETTER);
            //结算计数
            Integer dollOrder = 0;
            if (num <= 1) {//只结算一次
                dollOrder = dollOrderService.insertOrder(memberId, dollId, dollNum);
            } else {
                dollOrder = 1;//不重复创建 寄存订单
            }
            if (dollOrder == 1) {
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            return resultMap;
        } catch (Exception e) {
            logger.error("创建娃娃订单出错", e);
            throw e;
        }

    }

    /**
     * 预发货
     *
     * @param memberId
     * @param orderIds
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/beforeSendDoll", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap beforeSendDoll(@RequestParam Integer memberId, @RequestParam Long[] orderIds, @RequestParam String token) throws Exception {
        try {
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token) || orderIds == null || orderIds.length < 1) {
                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //看看是不是VIP
//            if (!memberService.isVIP(memberId)) {
//                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.NO_VIP_CODE, "您当前累计充值金额未达到" + systemPrefDao.selectByPrimaryKey("DELIVERY_NOFEE_COINS").getValue() + "元，不满足发货条件哦！");
//            }
            //看看是不是白名单
            /*if (memberService.isWorker(memberId) || memberId == 127051) {
                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.FAILE_CODE, "白名单用户禁止发货");
            }*/
            // 验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {//官方账号不发货
                logger.info("申请发货失败:没有授权");
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return dollOrderService.beforeSendDoll(memberId, orderIds);
        } catch (Exception e) {
            logger.error("发货出错,参数 memberId={},orderIds={},addrId={},token={},note={}", memberId, orderIds, token);
            throw e;
        }

    }

    /**
     * 申请发货
     *
     * @param memberId
     * @param orderIds
     * @param addrId
     * @param token
     * @param note
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendDoll", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap sendDoll(@RequestParam Integer memberId, @RequestParam Long[] orderIds, @RequestParam Integer addrId, @RequestParam String token, String note) throws Exception {
        try {
            //验证参数
            if (memberId == null || StringUtils.isEmpty(token) || orderIds == null || orderIds.length < 1 || addrId == null || (note != null && note.length() > 255)) {
                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //看看是不是VIP
//            if (!memberService.isVIP(memberId)) {
//                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.NO_VIP_CODE, "您当前累计充值金额未达到" + systemPrefDao.selectByPrimaryKey("DELIVERY_NOFEE_COINS").getValue() + "元，不满足发货条件哦！");
//            }
            //看看是不是白名单
            /*if (memberService.isWorker(memberId) || memberId == 127051) {
                logger.info("申请发货接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.FAILE_CODE, "白名单用户禁止发货");
            }*/
            //访问间隔限制
            RedisUtil redisUtil = new RedisUtil();
            if (redisUtil.getString("sendDoll" + memberId) != null) {
                logger.info("分享得币失败=" + Enviroment.PLEASE_SLOW_DOWN);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.PLEASE_SLOW_DOWN);
            }
            redisUtil.setString("sendDoll" + memberId, "", Enviroment.ACCESS_SENDDOLL_TIME);
            // 验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {//官方账号不发货
                logger.info("申请发货失败:没有授权");
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return dollOrderService.sendOrder(memberId, orderIds, addrId, note);
        } catch (Exception e) {
            logger.error("发货出错,参数 memberId={},orderIds={},addrId={},token={},note={}", memberId, orderIds, addrId, token, note);
            throw e;
        }

    }

}
