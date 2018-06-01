package com.bfei.icrane.api.controller;

import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.service.AgentChargeService;
import com.bfei.icrane.core.service.AgentService;
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

    /**
     * 代理商登陆
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAccount(Integer agentId, String token) {
        try {
            logger.info("代理账户接口参数:memberId=" + agentId + ",token=" + token);
           /* if (agentId == null || StringUtils.isEmpty(token)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }*/
            //验证token
           /* if (!validateTokenService.validataAgentToken(token, agentId)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }*/
            //return accountService.selectById(memberId);
            Agent agent = agentService.selectByPrimaryKey(agentId);
            Long withdraw = 0L;
            AgentCharge agentCharge = new AgentCharge();

            switch (agent.getLevel()) {
                case 0:
                    agentCharge.setAgentSuperId(agent.getId());
                    agentChargeService.insertSelective(agentCharge);
                    break;
                case 1:
                    agentCharge.setAgentOneId(agent.getId());
                    agentChargeService.insertSelective(agentCharge);
                    break;
                case 2:
                    agentCharge.setAgentTwoId(agent.getId());
                    agentChargeService.insertSelective(agentCharge);
                    break;
                case 3:
                    agentCharge.setAgentThreeId(agent.getId());
                    agentChargeService.insertSelective(agentCharge);
                    break;
                default:
                    break;
            }

            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, agent);
        } catch (Exception e) {
            logger.error("用户账户接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

}
