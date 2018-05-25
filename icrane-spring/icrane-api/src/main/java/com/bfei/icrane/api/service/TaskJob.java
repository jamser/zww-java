package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.VipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
}