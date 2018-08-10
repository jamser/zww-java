package com.bfei.icrane.api.service;

import java.util.List;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.Member;
import org.springframework.web.bind.annotation.RequestParam;

import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderItem;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/27
 * Description: 娃娃发货或寄存订单业务接口.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public interface DollOrderService {

    List<DollOrder> selectListByOrderIds(Long[] orderIds);

    /**
     * 获取订单
     */
    DollOrder selectByPrimaryKey(Long id);

    List<DollOrder> selectListByPrimaryKey(Long[] orderIds);

    /**
     * 根据用户id获取订单详情
     */
    List<DollOrderItem> selectItemsByMemberId(Integer memberId);

    List<DollOrderItem> selectItemsByMemberIdOrderStatus(Integer memberId, String orderStatus);

    Integer insertOrder(Integer memberId, Integer dollId, Integer dollNum);

    int updateByPrimaryKeySelective(DollOrder record);

    DollOrder selectByOrderIds(Long[] orderIds);

    List<DollOrder> selectByOrderNotIn(DollOrder record);

    List<DollOrderItem> selectByOrderItem(DollOrder record);

    int updateOrderId(DollOrder record, List<DollOrder> dollOrderNotIn, List<DollOrderItem> item, Integer addrId, Integer[] orderIds);

    /**
     * 申请发货
     *
     * @param memberId
     * @param orderIds
     * @param addrId
     * @return
     */
    ResultMap sendOrder(Integer memberId, Long[] orderIds, Integer addrId, String note);

    List<DollOrder> selectExpireOrder();

    List<DollOrder> selectOutTimeDolls();

    ResultMap beforeSendDoll(Integer memberId, Long[] orderIds);

    /**
     * 获取抓中排行
     * @param type
     * @param memberId
     * @return
     */
    ResultMap getCatchSuccessRanks(Integer type, Integer memberId);

    /**
     * 获取个人抓中数据
     * @param memberId
     * @return
     */
    ResultMap getCatchSuccessRanksByMember(Integer memberId);


    /**
     * 兑换
     * @param memberId
     * @param orderIds
     */
     ResultMap dollExchange(Integer memberId, Long[] orderIds);
}