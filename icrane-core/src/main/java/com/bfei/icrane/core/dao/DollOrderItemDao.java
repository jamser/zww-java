package com.bfei.icrane.core.dao;

import java.util.List;

import com.bfei.icrane.core.models.vo.CatchVO;
import com.bfei.icrane.core.pojos.Rankpojo;
import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderItem;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/26
 * Description: 娃娃抓取成功后的订单明细DAO层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public interface DollOrderItemDao {
    int deleteByPrimaryKey(Long id);

    int insert(DollOrderItem record);

    int insertSelective(DollOrderItem record);

    DollOrderItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(DollOrderItem record);

    int updateByPrimaryKey(DollOrderItem record);

    List<DollOrderItem> selectByMemberId(Integer memberId);

    List<DollOrderItem> selectByMemberIdOrderStatus(@Param("memberId") Integer memberId, @Param("orderStatus") String orderStatus);

    List<DollOrderItem> selectByOrderItem(DollOrder record);

    int updateOrderId(DollOrderItem item);

    List<DollOrderItem> getOrderItemByOrderId(Long id);

    CatchVO selectByOrderStatusAndSecond();

    List<Rankpojo> selectByRankNow(@Param("memberId") Integer memberId);

    List<Rankpojo> selectByRankWeek(@Param("memberId") Integer memberId);

    List<Rankpojo> selectByRankAll(@Param("memberId") Integer memberId);
}