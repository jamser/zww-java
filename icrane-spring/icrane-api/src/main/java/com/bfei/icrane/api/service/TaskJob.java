package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.WXUtil;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.AgentChargeService;
import com.bfei.icrane.core.service.DollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private AccountService accountService;
    @Autowired
    private AgentChargeService agentChargeService;
    @Autowired
    private AgentWithdrawService agentWithdrawService;

    @Autowired
    private SystemPrefService systemPrefService;

    @Autowired
    private AgentService agentService;

    /**
     * 兑换每日过期娃娃
     */
//    @Scheduled(cron = "0 0 3 * * ?")
//    public void job() {
//        List<DollOrder> dollOrders = dollOrderService.selectOutTimeDolls();
//        if (dollOrders.size() > 0) {
//            for (DollOrder dollOrder : dollOrders) {
//                logger.info("过期娃娃自动兑换" + dollOrder.getId());
//                chargeService.insertChargeHistory(new Charge(), dollOrder.getOrderBy(), new Long[]{dollOrder.getId()});
//            }
//        }
//    }

    /**
     * 每日清除房间内残余信息
     */
    @Scheduled(cron = "0 0 4 * * ?")
    public void job2() {
        if (!isSystemIp()) {
            return;
        }
        //清除过期房间信息
        List<Integer> dollIds = dollService.selectDollId();
        for (Integer dollId : dollIds) {
            redisUtil.delKey(RedisKeyGenerator.getRoomKey(dollId));
        }
    }

    /**
     * 用户会员每月充值清零
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    //@Scheduled(cron = "0 0/30 * * * ?")
    public void job3() {
        if (!isSystemIp()) {
            return;
        }
        //清除过期房间信息
        logger.info("每月一号用户成长值清0¬--------");
        List<Account> payingUsers = accountService.selectPayingUser();
        for (Account payingUser : payingUsers) {
            accountService.updateMemberGrowthValueMonth(payingUser);
        }
    }

    //    @Scheduled(cron = "0/5 * *  * * ? ")
    public void test() {
        Integer agentId;
        if (redisUtil.existsKey("agent_id")) {
            agentId = Integer.valueOf(redisUtil.getString("agent_id"));
        } else {
            agentId = 1;
        }
        if (redisUtil.existsKey("AgentCodeKey" + agentId + "1_3_Key")) {
            redisUtil.delKey("AgentCodeKey" + agentId + "1_3_Key");
        }
        String index = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wxcb4254f4b131fc12&redirect_uri=http%3A%2F%2Flanao.nat300.top/icrane/api/h5login" +
                "&response_type=code&scope=snsapi_userinfo&state=agent" + agentId + "-lanaokj_null#wechat_redirect1_3";
        if (redisUtil.existsKey(index)) {
            redisUtil.delKey(index);
        }
        redisUtil.setString("agent_id", String.valueOf(agentId + 1));
    }


    @Scheduled(cron = "0 0 1 * * ?") //每天一点执行
//    @Scheduled(cron = "0/5 * *  * * ? ")
    public void agentIncomeTrans() {
        if (!isSystemIp()) {
            return;
        }
        List<AgentCharge> chargeList = agentChargeService.selectByStatus(0);
        Integer time = Integer.valueOf(systemPrefService.selectByPrimaryKey(Enviroment.BALANCE_CHANGE_TIME).getValue());
        if (time == 0) {
            return;
        }
        Calendar calendar = Calendar.getInstance();

        int count = time;

        for (int j = 1; j <= count; j++) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -j);
            Date date = calendar.getTime();
//            if(WXUtil.isHostory(date)==1||WXUtil.isHostory(date)==2){
//                count++;
//            }
            if (isWeekend(date)) {
                count++;
            }
        }
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -count);
        Date oldDate = isThursday(calendar.getTime());

        for (int i = 0; i < chargeList.size(); i++) {
            AgentCharge agentCharge = chargeList.get(i);
            logger.info("当前时间==={},订单时间=={},结算时间==={}",
                    new Date(), agentCharge.getCreateTime(), oldDate);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(agentCharge.getCreateTime());
            String format1 = sdf.format(oldDate);

            if (format.compareTo(format1) <= 0) {
                if (agentCharge.getAgentSuperIncome() > 0) {
                    Agent agent = new Agent();
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
                    Agent agent = new Agent();
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

    private boolean isSystemIp() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            String localname = ia.getHostName();
            String localip = ia.getHostAddress();
            logger.warn("本机名称是：" + localname);
            logger.warn("本机的ip是 ：" + localip);
            if (!"47.106.39.237".equals(localip)) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
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

    /**
     * 校正代理金额
     */
    public void verifyAgent() {
        List<Agent> agentList = agentService.selectByAll();
        Long sum = null;
        for (int i = 0; i < agentList.size(); i++) {
            try {
                switch (agentList.get(i).getLevel()) {
                    case 0:
                        sum = agentChargeService.selectByAgentSuperId(agentList.get(i).getId(), 1, null);
                        Agent agentSuper = agentService.selectByPrimaryKey(agentList.get(i).getId());
                        Long withdraw = agentWithdrawService.selectByWithdraw(agentList.get(i).getId());

                        Assert.isTrue(agentSuper.getBalance() + withdraw - sum == 0, "总金额异常");
                        break;
                    case 1:
                        sum = agentChargeService.selectByAgentOneId(agentList.get(i).getId(), 1, null);
                        Agent agentOne = agentService.selectByPrimaryKey(agentList.get(i).getId());
                        Long agentOnewithdraw = agentWithdrawService.selectByWithdraw(agentList.get(i).getId());
                        Assert.isTrue(agentOne.getBalance() + agentOnewithdraw - sum == 0, "总金额异常");
                        break;
                    case 2:
                        sum = agentChargeService.selectByAgentTwoId(agentList.get(i).getId(), 1, null);
                        Agent agentTwo = agentService.selectByPrimaryKey(agentList.get(i).getId());
                        Long agentTwowithdraw = agentWithdrawService.selectByWithdraw(agentList.get(i).getId());
                        Assert.isTrue(agentTwo.getBalance() + agentTwowithdraw - sum == 0, "总金额异常");
                        break;
                    case 3:
                        sum = agentChargeService.selectByAgentThreeId(agentList.get(i).getId(), 1, null);
                        Agent agentThree = agentService.selectByPrimaryKey(agentList.get(i).getId());
                        Long agentThreeithdraw = agentWithdrawService.selectByWithdraw(agentList.get(i).getId());
                        Assert.isTrue(agentThree.getBalance() + agentThreeithdraw - sum == 0, "总金额异常");
                        break;
                }
            } catch (Exception e) {
                logger.info("代理商金额异常！！ 余额={},已清算金额={},代理Id={}", String.valueOf(sum), agentList.get(i).getBalance(), agentList.get(i).getId());
            }
        }
    }
}