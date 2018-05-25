package com.bfei.icrane.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.core.models.DollOrder;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/26 Description: 娃娃抓取成功后的订单DAO层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public interface DollOrderDao {
    int deleteByPrimaryKey(Long id);

    int insert(DollOrder record);

    int insertSelective(DollOrder record);

    DollOrder selectByPrimaryKey(Long id);

    //发货申请
    int sendDoll(@Param("addressId") Integer addressId, @Param("orderIds") Long[] orderIds);

    int updateByPrimaryKeySelective(DollOrder record);

    int updateByPrimaryKey(DollOrder record);

    int insertOrder(DollOrder record);

    DollOrder selectByOrderNum(String orderNum);

    DollOrder selectByOrderIds(Long[] orderIds);

    List<DollOrder> selectListByOrderIds(Long[] orderIds);

    List<DollOrder> selectByOrderNotIn(DollOrder record);

    List<DollOrder> selectExpireOrder();

    List<DollOrder> getOrdersByStatus(@Param("begin") int begin, @Param("pageSize") int pageSize, @Param("phone") String phone);

    int totalCount(@Param("phone") String phone);

    List<DollOrder> getOutOrdersByStatus(@Param("begin") int begin, @Param("pageSize") int pageSize, @Param("phone") String phone, @Param("outGoodsId") int outGoodsId);

    int totalCountOutOrders(@Param("phone") String phone, @Param("outGoodsId") int outGoodsId);

    List<DollOrder> selectListByPrimaryKey(Long[] orderIds);

    List<DollOrder> selectOutTimeDolls();

}
