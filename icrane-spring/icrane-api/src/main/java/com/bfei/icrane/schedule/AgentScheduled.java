package com.bfei.icrane.schedule;

import com.bfei.icrane.api.controller.AgentBankController;
import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.SystemPrefService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.service.AgentChargeService;
import com.bfei.icrane.core.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by moying on 2018/6/5.
 */
@Component
public class AgentScheduled {

    private static final Logger logger = LoggerFactory.getLogger(AgentBankController.class);
    @Autowired
    private AgentChargeService chargeService;

    @Autowired
    private SystemPrefService systemPrefService;

    @Autowired
    private AgentService agentService;


            @Scheduled(cron = "0 0 1 * * ?") //每天一点执行
//    @Scheduled(cron = "0/5 * *  * * ? ")
    public void agentIncomeTrans() {
        List<AgentCharge> chargeList = chargeService.selectByStatus(0);
        Integer time = Integer.valueOf(systemPrefService.selectByPrimaryKey(Enviroment.BALANCE_CHANGE_TIME).getValue());
        if (time == 0) {
            return;
        }
        for (int i = 0; i < chargeList.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            AgentCharge agentCharge = chargeList.get(i);
            Agent agent = new Agent();
            int count = 0;
            for (int j = 1; j <= time; j++) {
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, -j);
                Date date = calendar.getTime();
                if (isWeekend(date)) {
                    count++;
                }
            }
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -(time + count));
            Date oldDate = calendar.getTime();
            logger.info("当前时间==={},订单时间=={},结算时间==={}", new Date(), agentCharge.getCreateTime(), oldDate);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(agentCharge.getCreateTime());
            String format1 = sdf.format(oldDate);

            if (format.compareTo(format1) <= 0) {
                if (agentCharge.getAgentOneIncome() > 0) {
                    agent = new Agent();
                    agent.setId(agentCharge.getAgentSuperId());
                    agent.setBalance(agentCharge.getAgentSuperIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentOneIncome() > 0) {
                    agent = new Agent();
                    agent.setId(agentCharge.getAgentOneId());
                    agent.setBalance(agentCharge.getAgentOneIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentTwoIncome() > 0) {
                    agent = new Agent();
                    agent.setId(agentCharge.getAgentTwoId());
                    agent.setBalance(agentCharge.getAgentTwoIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentThreeIncome() > 0) {
                    agent = new Agent();
                    agent.setId(agentCharge.getAgentThreeId());
                    agent.setBalance(agentCharge.getAgentThreeIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                int type = chargeService.updateStatus(agentCharge.getId());
                logger.info("【订单任务】代理交易ID === {} 用户订单ID ==== {} ，成功为{}", agentCharge.getId(), agentCharge.getOrderId(), type);
            }


        }
    }

    public boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

}
