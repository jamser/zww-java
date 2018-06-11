package com.bfei.icrane.api.service;

import com.bfei.icrane.api.controller.AgentBankController;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.AgentChargeService;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.VipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SUN on 2018/3/5.
 * <p>
 * 定时任务
 */
@Component("taskJob")
public class TaskJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskJob.class);

    private RedisUtil redisUtil = new RedisUtil();
    @Autowired
    private DollService dollService;
    @Autowired
    private DollOrderService dollOrderService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private VipService vipService;
    @Autowired
    private ChargeService chargeService;

    @Autowired
    private AgentChargeService agentChargeService;

    @Autowired
    private SystemPrefService systemPrefService;

    @Autowired
    private AgentService agentService;

    /**
     * 兑换每日过期娃娃
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void job() {
        List<DollOrder> dollOrders = dollOrderService.selectOutTimeDolls();
        if (dollOrders.size() > 0) {
            for (DollOrder dollOrder : dollOrders) {
                logger.info("过期娃娃自动兑换" + dollOrder.getId());
                chargeService.insertChargeHistory(new Charge(), dollOrder.getOrderBy(), new Long[]{dollOrder.getId()});
            }
        }
    }

    /**
     * 每日清除房间内残余信息
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void job2() {
        //清除过期房间信息
        List<Integer> dollIds = dollService.selectDollId();
        for (Integer dollId : dollIds) {
            redisUtil.delKey(RedisKeyGenerator.getRoomKey(dollId));
        }
    }

    /**
     * 用户会员等级降级
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    //@Scheduled(cron = "0 0/30 * * * ?")
    public void job3() {
        //清除过期房间信息
        //logger.info("每月一号用户会员等级降级--------");
        List<Account> payingUsers = accountService.selectPayingUser();
        for (Account payingUser : payingUsers) {
            //logger.info("会员" + payingUser.getId() + "等级降级--------");
            //当前积分
            BigDecimal baseGrowthValue = payingUser.getGrowthValue();
            //当前等级
            Vip vip = vipService.selectVipByMemberId(payingUser.getId());
            int baseLevel = vip.getLevel();
            Vip lowervip = vipService.selectVipByLevel(baseLevel > 0 ? baseLevel - 1 : 0);
            //查询过期积分
            BigDecimal leastAllowed = lowervip.getLeastAllowed();
            BigDecimal lowerGrowthValue = baseGrowthValue.subtract(leastAllowed);
            //生成记录
            Charge charge = new Charge();
            charge.setMemberId(payingUser.getId());
            charge.setChargeDate(TimeUtil.getTime());
            charge.setType("expense");
            charge.setChargeMethod("成长值过期 -" + lowerGrowthValue);
            charge.setGrowthValue(baseGrowthValue);
            charge.setGrowthValueSum(leastAllowed);




            chargeService.insertGrowthValueHistory(charge);
            //过期积分
            payingUser.setGrowthValue(leastAllowed);
            accountService.updateMemberGrowthValue(payingUser);
        }
    }


    @Scheduled(cron = "0 0 1 * * ?") //每天一点执行
//    @Scheduled(cron = "0/5 * *  * * ? ")
    public void agentIncomeTrans() {
        List<AgentCharge> chargeList = agentChargeService.selectByStatus(0);
        Integer time = Integer.valueOf(systemPrefService.selectByPrimaryKey(Enviroment.BALANCE_CHANGE_TIME).getValue());
        if (time == 0) {
            return;
        }

        for (int i = 0; i < chargeList.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            AgentCharge agentCharge = chargeList.get(i);
            int count = time;

            for (int j = 1; j <= count; j++) {
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, -j);
                Date date = calendar.getTime();
                if (isWeekend(date)) {
                    count++;
                }
            }
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -count);
            Date oldDate = isThursday(calendar.getTime());
            logger.info("当前时间==={},订单时间=={},结算时间==={}",
                    new Date(), agentCharge.getCreateTime(), oldDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(agentCharge.getCreateTime());
            String format1 = sdf.format(oldDate);

            if (format.compareTo(format1) <= 0) {
                if (agentCharge.getAgentOneIncome() > 0) {
                    Agent   agent = new Agent();
                    agent.setId(agentCharge.getAgentSuperId());
                    agent.setBalance(agentCharge.getAgentSuperIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentOneIncome() > 0) {
                    Agent agent = new Agent();
                    agent.setId(agentCharge.getAgentOneId());
                    agent.setBalance(agentCharge.getAgentOneIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentTwoIncome() > 0) {
                    Agent   agent = new Agent();
                    agent.setId(agentCharge.getAgentTwoId());
                    agent.setBalance(agentCharge.getAgentTwoIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                if (agentCharge.getAgentThreeIncome() > 0) {
                    Agent agent = new Agent();
                    agent.setId(agentCharge.getAgentThreeId());
                    agent.setBalance(agentCharge.getAgentThreeIncome());
                    agentService.updateAgentBalance(agent);
                    logger.info("【订单任务】代理商 == {},转入余额 {}", agent.getId(), agent.getBalance());
                }
                int type = agentChargeService.updateStatus(agentCharge.getId());
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


    public Date isThursday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.DAY_OF_WEEK) - 1 == 5) {
            cal.add(Calendar.DAY_OF_MONTH, +2);
            return cal.getTime();
        }
        return date;
    }
}