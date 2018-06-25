package com.bfei.icrane.api.controller;

import java.util.List;
import java.util.Map;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bfei.icrane.api.service.ChargeRulesService;
import com.bfei.icrane.api.service.PrefSetService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.core.models.ChargeRules;
import com.bfei.icrane.core.models.SystemPref;
import com.bfei.icrane.core.service.ValidateTokenService;
/**
 * 充值规则
 */
@Controller
@RequestMapping(value = "/chargeRules")
@CrossOrigin
public class ChargeRulesController {
    private static final Logger logger = LoggerFactory.getLogger(ChargeRulesController.class);
    @Autowired
    ChargeRulesService chargeRulesService;
    @Autowired
    ValidateTokenService validateTokenService;
    @Autowired
    PrefSetService prefSetService;
    @Autowired
    private MemberService memberService;

    /**
     * 获取充值规则
     *
     * @param token 用户令牌
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getChargeRules", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getChargeRules(String token, Integer rulesType) throws Exception {
        //logger.info("获取充值规则token=" + token);
        Map<String, Object> resultMap = new HashedMap<>();
        try {
            // 验证token有效性
            if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token)) {
                logger.info("获取用户消息出错," + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            //List<ChargeRules> getChargeRules = chargeRulesService.getChargeRules();
            List<ChargeRules> getChargeRules = chargeRulesService.getChargeRulesByType(rulesType);
            RedisUtil redisUtil = new RedisUtil();
            String memberId = redisUtil.getString(token);
            //充值过的不显示首充
            if (memberService.selectById(Integer.valueOf(memberId)).getAccount().getCoinFirstCharge()) {
                getChargeRules.remove(0);
            }
            if (getChargeRules != null) {
                logger.info("获取用户消息resultMap=" + resultMap);
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, getChargeRules);
            } else {
                logger.info("获取用户消息出错," + Enviroment.RETURN_FAILE_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("获取用户消息出错", e);
            throw e;
        }
    }

    /**
     * 获取充值规则
     *
     * @param token 用户令牌
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/newChargeRules", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap newChargeRules(Integer memberId, String token) throws Exception {
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("充值列表接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("充值列表接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, chargeRulesService.getChargeRulesBymemberId(memberId));
        } catch (Exception e) {
            logger.error("充值列表接口出错", e);
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    @RequestMapping(value = "/getPayDetail", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPayDetail() throws Exception {
        logger.info("获取充值规则说明");
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        String code = "PAY_DETAIL";
        SystemPref prefSet = prefSetService.selectByPrimaryKey(code);
        if (prefSet == null) {
            resultMap.put("resultData", "充值规则说明未上传");
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
        } else {
            resultMap.put("resultData", prefSet);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
        }
        return resultMap;
    }


}
