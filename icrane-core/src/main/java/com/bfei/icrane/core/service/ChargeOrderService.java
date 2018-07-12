package com.bfei.icrane.core.service;


import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.vo.ChannelChargeOrder;
import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.ChargeOrder;
import com.bfei.icrane.core.models.ChargeRules;

import java.util.Date;
import java.util.List;

public interface ChargeOrderService {

    /**
     * 创建订单
     *
     * @param order
     * @return
     */
    int createChareOrder(ChargeOrder order);

    Integer createChareOrder(int chargeruleid, Double dprice, int memberId, String orderNo);

    /**
     * 订单成功
     *
     * @param orderNo
     * @return
     */
    ChargeOrder orderSuccess(String orderNo, Double fee);

    /**
     * 订单失效
     *
     * @param orderNo
     * @return
     */
    int orderFailure(String orderNo);

    ChargeRules queryRule(Integer ruleId);

    /**
     * 查询订单list
     */
    PageBean<ChargeOrder> getChargeOrderList(int page, int pageSize);

    /**
     * 订单按条件查询
     */
    PageBean<ChargeOrder> selectChargeOrderBy(String memberName, String id, Integer chargeName, Integer chargeState, String startTime, String endTime, int page, int pageSize);

    /**
     * 订单按userid查询
     */
    PageBean<ChargeOrder> selectChargeOrderByUserid(int page, int pageSize, Integer userid);

    Double selectChargeNumByUserid(Integer memberId);

    /**
     * 查询渠道充值记录
     */
    PageBean<ChannelChargeOrder> selectChannelChargeOrderBy(String lastLoginFrom, String registerChannel, String memberName, String memberId, Integer chargerelueid, Integer chargeState, String startTime, String endTime, int page, int pageSize);

    //渠道充值人数
    Integer selectChannelChargeNum(String lastLoginFrom, String registerChannel, String memberName, String memberId, Integer chargerelueid, Integer chargeState, String startTime, String endTime);

    //渠道充值金额
    Double selectChannelChargePrice(String lastLoginFrom, String registerChannel, String memberName, String memberId, Integer chargerelueid, Integer chargeState, String startTime, String endTime);

    int selectmemberIdByOrder_no(String out_trade_no);

    Integer insertChargeHistory(Charge charge);

    ChargeOrder selectByOrderNo(String orderNo);
}
