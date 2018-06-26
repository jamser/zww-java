package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.AgentService;
import com.bfei.icrane.api.service.AgentWithdrawService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.vo.AgentProfitVO;
import com.bfei.icrane.core.service.AgentChargeService;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by moying on 2018/6/5.
 */
@Controller
@RequestMapping(value = "/agent/income")
@CrossOrigin
public class AgentWithdrawController {
    private static final Logger logger = LoggerFactory.getLogger(AgentWithdrawController.class);

    @Autowired
    private AgentChargeService agentChargeService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentWithdrawService withdrawService;

    @Autowired
    private ValidateTokenService validateTokenService;

    /**
     * 获取分润列表
     * @param token
     * @param agentId
     * @return
     */

    @PostMapping(value = "/lists")
    @ResponseBody
    public ResultMap getIncomeLists(@RequestParam String token, @RequestParam Integer agentId) {
        //验证token
//        if (!validateTokenService.validataAgentToken(token, agentId)) {
//            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//        }
        Agent agent = agentService.selectByPrimaryKey(agentId);
        AgentProfitVO agentProfitVO = new AgentProfitVO();
        switch (agent.getLevel()) {
            case 0:
                agentProfitVO.setTodayProfit(agentChargeService.selectByAgentSuperId(agent.getId(), null, new Date()));
                agentProfitVO.setTotalProfit(agentChargeService.selectByAgentSuperId(agent.getId(), null, null));
                break;
            case 1:
                agentProfitVO.setTodayProfit(agentChargeService.selectByAgentOneId(agent.getId(), null, new Date()));
                agentProfitVO.setTotalProfit(agentChargeService.selectByAgentOneId(agent.getId(), null, null));
                break;
            case 2:
                agentProfitVO.setTodayProfit(agentChargeService.selectByAgentTwoId(agent.getId(), null, new Date()));
                agentProfitVO.setTotalProfit(agentChargeService.selectByAgentTwoId(agent.getId(), null, null));
                break;
            case 3:
                agentProfitVO.setTodayProfit(agentChargeService.selectByAgentThreeId(agent.getId(), null, new Date()));
                agentProfitVO.setTotalProfit(agentChargeService.selectByAgentThreeId(agent.getId(), null, null));
                break;
        }
        agentProfitVO.setWithdrawProfit(withdrawService.selectByWithdraw(agentId));
        agentProfitVO.setAgentIncomeList(agentChargeService.selectIncomeByAgent(agent));

        return new ResultMap("获取数据成功", agentProfitVO);
    }

    /**
     * 获取提现记录
     * @param token
     * @param agentId
     * @return
     */
    @PostMapping(value = "/records")
    @ResponseBody
    public ResultMap getIncomeRecords(@RequestParam String token, @RequestParam Integer agentId) {
        //验证token
//        if (!validateTokenService.validataAgentToken(token, agentId)) {
//            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//        }
        Agent agent = agentService.selectByPrimaryKey(agentId);
        return new ResultMap("获取数据成功", withdrawService.selectByWithdrawLists(agent.getId()));
    }
}
