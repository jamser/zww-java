package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.RiskManagementService;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by SUN on 2018/1/10.
 * 用户账户控制器
 */
@Controller
@RequestMapping(value = "/account")
@CrossOrigin
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private MemberService memberService;

    /**
     * 查询用户账户
     *
     * @param memberId
     * @param token
     * @return
     */
    @RequestMapping(value = "/accountDetails", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAccount(Integer memberId, String token) {
        try {
            logger.info("用户账户接口参数:memberId=" + memberId + ",token=" + token);
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            //return accountService.selectById(memberId);
            Account account = accountService.selectById(memberId);
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, account);
        } catch (Exception e) {
            logger.error("用户账户接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 查询用户有效充值金额
     *
     * @param memberId
     * @param token
     * @return
     */
    @RequestMapping(value = "/chargeAmount", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap chargeAmount(Integer memberId, String token) {
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("充值金额接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("充值金额接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, memberService.rechargeAmount(memberId));
        } catch (Exception e) {
            logger.error("充值金额参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 查询会员信息
     *
     * @param memberId
     * @param token
     * @return
     */
    @RequestMapping(value = "/vip", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap vip(Integer memberId, String token) {
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("会员信息接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("会员信息接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, accountService.vip(memberId));
        } catch (Exception e) {
            logger.error("会员信息参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 查询上个月是否降级
     *
     * @param memberId
     * @param token
     * @return
     */
//    @RequestMapping(value = "/whetherTheDowngrade", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap whetherTheDowngrade(Integer memberId, String token) {
//        try {
//            if (memberId == null || StringUtils.isEmpty(token)) {
//                logger.info("查询上个月是否降级接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            if (!validateTokenService.validataToken(token, memberId)) {
//                logger.info("查询上个月是否降级接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, accountService.whetherTheDowngrade(memberId));
//        } catch (Exception e) {
//            logger.error("查询上个月是否降级接口异常=" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//        }
//    }

    /*@RequestMapping(value = "/test", method = RequestMethod.POST)
    @ResponseBody
    public Object text(Integer memberId1, Integer memberId2) {
        return new ResultMap("",riskManagementService.isOneIMEI(memberId1, memberId2));
    }*/

}
