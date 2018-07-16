package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.MemberChargeComboService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by SUN on 2018/1/10.
 * 周卡月卡领取控制器
 */
@Controller
//@RequestMapping(value = "/ChargeCombo")
@CrossOrigin
public class MemberChargeComboController {
    private static final Logger logger = LoggerFactory.getLogger(MemberChargeComboController.class);

    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private MemberChargeComboService memberChargeComboService;

    /**
     * 周卡领取接口
     *
     * @param memberId 用户UserId
     * @param token    用户Token
     * @return
     */
    @RequestMapping(value = "/weeks", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap weeksCombo(Integer memberId, String token) {
        logger.info("周卡领取接口参数memberId=" + memberId + "token" + token);
        try {
            //验证参数
            if (memberId == null || token == null || "".equals(token)) {
                logger.info("周卡领取接口异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //访问间隔限制
            RedisUtil redisUtil = new RedisUtil();
            if (redisUtil.getString("weeks" + memberId) != null) {
                logger.info("周卡领取失败=" + Enviroment.PLEASE_SLOW_DOWN);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PLEASE_SLOW_DOWN);
            }
            redisUtil.setString("weeks" + memberId, "", Enviroment.ACCESS_CONTROL_TIME);
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("周卡领取接口异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return memberChargeComboService.weeksCombo(memberId);
        } catch (Exception e) {
            logger.error("周卡领取出错", e);
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.COMBO_IN_ABNORMAL);
        }
    }

    /**
     * 月卡领取接口
     *
     * @param memberId 用户UserId
     * @param token    用户Token
     * @return
     */
    @RequestMapping(value = "/month", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap monthCombo(Integer memberId, String token) {
        logger.info("月卡领取接口参数memberId=" + memberId + "token" + token);
        try {
            //验证参数
            if (memberId == null || token == null || "".equals(token)) {
                logger.info("月卡领取接口异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //访问间隔限制
            RedisUtil redisUtil = new RedisUtil();
            if (redisUtil.getString("month" + memberId) != null) {
                logger.info("月卡领取失败=" + Enviroment.PLEASE_SLOW_DOWN);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PLEASE_SLOW_DOWN);
            }
            redisUtil.setString("month" + memberId, "", Enviroment.ACCESS_CONTROL_TIME);
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("月卡领取接口异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return memberChargeComboService.monthCombo(memberId);
        } catch (Exception e) {
            logger.error("月卡领取出错", e);
            e.printStackTrace();
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.COMBO_IN_ABNORMAL);
        }
    }


}
