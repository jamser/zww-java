package com.bfei.icrane.api.controller;

import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.Banner;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.RepairsProblem;
import com.bfei.icrane.core.service.RepairsProblemService;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 报修接口
 * Created by SUN on 2018/1/16.
 */
@Controller
@RequestMapping("/dollRepairs")
@CrossOrigin
public class DollRepairsController {

    private static final Logger logger = LoggerFactory.getLogger(DollRepairsController.class);

    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private RepairsProblemService repairsProblemService;

    /**
     * 报修
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/giveAlarm", method = RequestMethod.POST)
    @ResponseBody
        public ResultMap giveAlarm(String token, Integer memberId, Integer dollId, String reason) {
        logger.info("报修接口参数：" + "token=" + token);
        try {
            // 验证token有效性
            if (memberId == null || token == null || "".equals(token) || dollId == null || reason == null || "".equals(reason)) {
                logger.info("报修接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("报修接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return repairsProblemService.insertRepairs(memberId, dollId, reason);
        } catch (Exception e) {
            logger.info("报修接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.PAY_ERROR);
        }
    }

    /**
     * 报修列表
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/repairsProblem", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap RepairsProblem(String token) {
//        logger.info("报修列表接口参数：" + "token=" + token);
        try {
            // 验证token有效性
            if (token == null) {
                logger.info("报修列表接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token)) {
                logger.info("报修列表接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, repairsProblemService.queryList());
        } catch (Exception e) {
            logger.info("报修列表接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.PAY_ERROR);
        }
    }


}
