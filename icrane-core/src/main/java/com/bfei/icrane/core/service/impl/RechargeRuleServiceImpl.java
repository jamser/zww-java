package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.RechargeRuleMapper;
import com.bfei.icrane.core.models.RechargeRule;
import com.bfei.icrane.core.service.RechargeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moying on 2018/7/16.
 */
@Service
public class RechargeRuleServiceImpl implements RechargeRuleService {

    @Autowired
    private RechargeRuleMapper rechargeRuleMapper;

    @Override
    public List<RechargeRule> selectByAll(Integer type) {
        return rechargeRuleMapper.selectByAll(type);
    }
}
