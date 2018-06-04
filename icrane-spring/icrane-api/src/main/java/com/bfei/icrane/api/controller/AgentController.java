package com.bfei.icrane.api.controller;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.form.AgentForm;
import com.bfei.icrane.core.form.AgentLoginForm;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentToken;
import com.bfei.icrane.core.pojos.AgentPojo;
import com.bfei.icrane.core.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by moying on 2018/6/1.
 */
@Controller
@RequestMapping(value = "/agent")
@CrossOrigin
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);


    @Autowired
    private ValidateTokenService validateTokenService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentChargeService agentChargeService;

    @Autowired
    private AdvertisementInfoService advertisementInfoService;

    @Autowired
    private SysNotifyService sysNotifyService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentLogin(@Valid AgentLoginForm agentLoginForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【代理登陆】参数不正确, agentLoginForm={}", agentLoginForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
        Agent agent = agentService.selectByUserName(agentLoginForm.getUsername());
        if (null == agent) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.USERNAME_ERROR);
        } else {
            if (agent.getPassword().equals(MD5Utils.md5(agentLoginForm.getPassword(), agent.getSalt()))) {
                AgentToken token = agentService.getAgentLogin(agent);
                if (null == token) {
                    return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.AGENT_LOGIN_ERROR);
                } else {
                    return new ResultMap(Enviroment.RETURN_SUCCESS_CODE, token);
                }
            }
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PASSWORD_ERROR);
        }
    }

    /**
     * 添加代理商
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap addAgent(@Valid AgentForm agentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【添加代理】参数不正确, agentForm={}", agentForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
//        验证token
        if (!validateTokenService.validataAgentToken(agentForm.getToken(), agentForm.getAgentId())) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        Agent agent = agentService.selectByPrimaryKey(agentForm.getAgentId());
        if (agent.getLevel() == 0 || agent.getLevel() == 1 || agent.getLevel() == 2) {

            Agent newAgent = agentService.selectByUserName(agentForm.getUsername());
            if (null != newAgent) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.USERNAME_EXIST);
            }
            if (agentService.insertAgent(agent, agentForm) == 1) {
                return new ResultMap(Enviroment.ADD_AGENT_SUCCESS);
            }
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.ADD_AGENT_ERROER);
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.ADD_AGENT_ERROER);
    }


    /**
     * 代理商登陆
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/userInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAccount(@RequestParam(value = "agentId", required = true) Integer agentId,
                                @RequestParam(value = "token", required = true) String token) {
        try {
            logger.info("代理账户接口参数:memberId=" + agentId + ",token=" + token);
            //验证token
            if (!validateTokenService.validataAgentToken(token, agentId)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Agent agent = agentService.selectByPrimaryKey(agentId);

            Long withdraw = 0L;

            switch (agent.getLevel()) {
                case 0:
                    withdraw = agentChargeService.selectByAgentSuperId(agent.getId());
                    break;
                case 1:
                    withdraw = agentChargeService.selectByAgentOneId(agent.getId());
                    break;
                case 2:
                    withdraw = agentChargeService.selectByAgentTwoId(agent.getId());
                    break;
                case 3:
                    withdraw = agentChargeService.selectByAgentThreeId(agent.getId());
                    break;
                default:
                    break;
            }
            agent.setWithdraw(withdraw);
            AgentPojo agentPojo = new AgentPojo();
            BeanUtils.copyProperties(agent, agentPojo);
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, agentPojo);
        } catch (Exception e) {
            logger.error("用户账户接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 获取文案列表
     * @param agentId
     * @param token
     * @return
     */

    @RequestMapping(value = "/ad_list", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap adList(@RequestParam(value = "agentId", required = true) Integer agentId,
                            @RequestParam(value = "token", required = true) String token) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return new ResultMap("文案列表", advertisementInfoService.selectAdInfoLists());
    }

    /**
     * 添加文案次数
     * @param agentId
     * @param token
     * @param adId
     * @return
     */
    @RequestMapping(value = "/ad_add", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap adList(@RequestParam(value = "agentId", required = true) Integer agentId,
                            @RequestParam(value = "token", required = true) String token,
                            @RequestParam(value = "adId", required = true) Integer adId) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        advertisementInfoService.updateByDownCount(adId);
        return new ResultMap("操作成功");
    }

    /**
     * 获取系统通知
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/sysNotify", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap sysNotifyLists(@RequestParam(value = "agentId", required = true) Integer agentId,
                                    @RequestParam(value = "token", required = true) String token) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return new ResultMap("公告列表", sysNotifyService.selectBySysNotifyLists());
    }
}
