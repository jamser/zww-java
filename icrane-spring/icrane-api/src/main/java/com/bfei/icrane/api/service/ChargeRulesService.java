package com.bfei.icrane.api.service;

import java.util.List;
import java.util.Map;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.ChargeRules;

/**
 * @author lgq Version: 1.0 Date: 2017/9/23 Description: 用户Service接口类.
 *         Copyright (c) 2017 mwan. All rights reserved.
 */
public interface ChargeRulesService {

    /**
     * 获取消息
     */
    public List<ChargeRules> getChargeRules();


    List<ChargeRules> getChargeRulesByType(Integer rulesType);

    Map getChargeRulesBymemberId(Integer memberId);

    ResultMap getRechargeRuleByPro(Integer memberId);
}








