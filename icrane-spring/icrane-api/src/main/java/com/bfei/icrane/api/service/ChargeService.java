package com.bfei.icrane.api.service;


import java.util.List;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.DollOrder;

/**
 * @author lgq Version: 1.0 Date: 2017/9/22 Description: 用户Service接口类.
 *         Copyright (c) 2017 mwan. All rights reserved.
 */
public interface ChargeService {

    /**
     * 获取规则
     */
    Charge getChargeRules(Double chargePrice);

    Integer insertChargeHistory(Charge charge);

    Integer insertChargeHistory(Charge charge, Integer memberId, Long[] orderIds);

    List<Charge> getChargeHistory(Integer memberId);

    Integer changeCount(List<DollOrder> orderList);

    void inviteChargeFirst(Integer memberId);

    ResultMap getSuccessfulRechargeRecords(Integer memberId, String mchOrderNo);

    ResultMap invite(Integer memberId, String inviteCode);

    /**
     * 展示邀请人
     *
     * @param memberId
     * @return
     */
    ResultMap whoInvite(Integer memberId);

    /**
     * 展示邀请人数
     *
     * @param memberId
     * @return
     */
    ResultMap howInvite(Integer memberId);

    int inviteOne(int id, String channel);

    void insertGrowthValueHistory(Charge charge);
}








