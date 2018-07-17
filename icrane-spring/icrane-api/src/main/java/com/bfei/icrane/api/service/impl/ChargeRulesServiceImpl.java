package com.bfei.icrane.api.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.dao.RechargeRuleMapper;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.models.RechargeRule;
import com.bfei.icrane.core.models.Vip;
import com.bfei.icrane.core.pojos.RechargeRulePojp;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.VipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfei.icrane.api.service.ChargeRulesService;
import com.bfei.icrane.core.dao.ChargeRulesDao;
import com.bfei.icrane.core.models.ChargeRules;


/**
 * @author lgq Version: 1.0 Date: 2017年9月19日date Description: 用户Service接口实现类.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("ChargeRulesService")
public class ChargeRulesServiceImpl implements ChargeRulesService {
    private static final Logger logger = LoggerFactory.getLogger(ChargeRulesServiceImpl.class);
    @Autowired
    private ChargeRulesDao chargeRulesDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private VipService vipService;
    @Autowired
    private RechargeRuleMapper rechargeRuleMapper;

    @Override
    public List<ChargeRules> getChargeRules() {
        // TODO Auto-generated method stub
        List<ChargeRules> cr = chargeRulesDao.getChargeRules();
        //logger.info("返回 cr:{}",cr);
        return cr;
    }

    @Override
    public List<ChargeRules> getChargeRulesByType(Integer rulesType) {
        if (rulesType == null) {
            return getChargeRules();
        } else {
            return chargeRulesDao.getChargeRulesByType(rulesType);
        }
    }

    @Override
    public Map getChargeRulesBymemberId(Integer memberId) {
        HashMap<String, Object> map = new HashMap<>();
        Set firstCharge = new HashSet();
        Set weeksMonthly = new HashSet();
        Set superTicket = new HashSet();
        Set coins = new HashSet();
        Set mixture = new HashSet();
        Set nextDiscount = new HashSet();
        map.put("firstCharge", firstCharge);
        map.put("weeksMonthly", weeksMonthly);
        map.put("superTicket", superTicket);
        map.put("coins", coins);
        map.put("mixture", mixture);
        if (vipService.getNext(memberId) != null) {
            map.put("nextDiscount", vipService.getNext(memberId).getDiscount());
        } else {
            map.put("nextDiscount", vipService.getMax().getDiscount());
        }
        List<ChargeRules> chargeRules = getChargeRules();
        for (ChargeRules chargeRule : chargeRules) {
            Integer chargeType = chargeRule.getChargeType();
            if (chargeType == 0) {
                if (chargeRule.getCoinsCharge() + chargeRule.getCoinsCharge() == 0) {
                    superTicket.add(chargeRule);
                } else if (chargeRule.getSuperTicketCharge() + chargeRule.getSuperTicketOffer() == 0) {
                    coins.add(chargeRule);
                } else {
                    mixture.add(chargeRule);
                }
            } else if (chargeType == 1) {

            } else if (chargeType == 2 || chargeType == 3) {
                weeksMonthly.add(chargeRule);
            } else if (chargeType == 4 && !accountService.select(memberId).getCoinFirstCharge()) {
                firstCharge.add(chargeRule);
            }
        }
        List<Vip> allVip = vipService.getAll();
        Account account = accountService.selectById(memberId);
        Vip basevip = account.getVip();
        map.put("next", -1);
        for (Vip vip : allVip) {
            if (vip.getLevel() == basevip.getLevel() + 1) {
                BigDecimal nextGrowthValue = allVip.get(basevip.getLevel() + 1).getLeastAllowed();
                map.put("next", nextGrowthValue.subtract(account.getGrowthValue()));
            }
        }
        return map;
    }

    @Override
    public ResultMap getRechargeRuleByPro(Integer memberId) {

        RechargeRulePojp rechargeRulePojp = new RechargeRulePojp();
        Account account = accountService.selectById(memberId);
        List<RechargeRule> rechargeRules = rechargeRuleMapper.selectByAll();
        rechargeRulePojp.setRechargePrice(account.getGrowthValueMonth().multiply(new BigDecimal(100)).intValue());

        for (int i = 0; i < rechargeRules.size(); i++) {
            if (rechargeRules.get(i).getPrice().compareTo(new BigDecimal(account.getGrowthValueMonthLevel())) == 0) {
                if (i == rechargeRules.size() - 1) {
                    rechargeRulePojp.setRechargeLevel(rechargeRules.get(i).getPrice().multiply(new BigDecimal(100)).intValue());
                    rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(i).getCoin());
                } else {
                    rechargeRulePojp.setRechargeLevel(rechargeRules.get(i + 1).getPrice().multiply(new BigDecimal(100)).intValue());
                    rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(i + 1).getCoin());
                }
            }
        }
        if (StringUtils.isEmpty(rechargeRulePojp.getRechargeLevel())) {
            rechargeRulePojp.setRechargeLevel(rechargeRules.get(0).getPrice().multiply(new BigDecimal(100)).intValue());
            rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(0).getCoin());
        }
        rechargeRulePojp.setRechargeLevelMax(rechargeRules.get(rechargeRules.size() - 1).getPrice().multiply(new BigDecimal(100)).intValue());
        return new ResultMap("操作成功", rechargeRulePojp);
    }
}
