package com.bfei.icrane.core.dao;

import java.util.Date;
import java.util.List;

import com.bfei.icrane.core.models.Banner;
import com.bfei.icrane.core.models.vo.ChannelChargeOrder;
import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.core.models.ChargeOrder;

public interface ChargeOrderDao {

	/**
	 * 查找订单
	 * @param id
	 * @return
	 */
	ChargeOrder selectByPrimaryKey(Long id);
	/**
	 * 查询订单列表
	 * @return
	 */
	 List<ChargeOrder> getChargeOrder();
	/**
	 * 用订单编号查找订单
	 * @param orderNo
	 * @return
	 */
	ChargeOrder selectByOrderNo(String orderNo);
	/**
	 * 创建充值订单
	 * @param chargeOrder
	 * @return
	 */
	int insertSelective(ChargeOrder chargeOrder);
	/**
	 * 订单成功
	 * @param orderNo
	 * @return
	 */
	int orderSuccess(@Param("orderNo") String orderNo,@Param("updateDate") Date updateDate);
	/**
	 * 订单失效
	 * @param orderNo
	 * @return
	 */
	int orderFailure(@Param("orderNo") String orderNo,@Param("updateDate") Date updateDate);


	/**
	 * 后台查询列表
	 */
	List<ChargeOrder> selectChargeOrderList(@Param("begin")int begin,@Param("pageSize")int pageSize);

	/**
	 * 分页总条数
	 * @return
	 */
	int totalCount();


	/**
	 * 条件查询
	 */
	List<ChargeOrder> selectChargeOrderBy(@Param("name")String name, @Param("member_id")Integer member_id,  @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime, @Param("begin")int begin,@Param("pageSize")int pageSize);
	int totalCount1(@Param("name")String name, @Param("member_id")Integer member_id, @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime);

	/**
	 * 按userid条件查询
	 */
	List<ChargeOrder> selectChargeOrderByUserid(@Param("member_id")Integer member_id, @Param("begin")int begin,@Param("pageSize")int pageSize);
	int totalCountUserid(@Param("member_id")Integer member_id);
	Double totalPriceUserid(@Param("member_id")Integer member_id);


	/**
	 * 渠道充值列表
	 */
	List<ChannelChargeOrder> selectChannelChargeOrderBy(@Param("lastLoginFrom")String lastLoginFrom,@Param("registerChannel")String registerChannel, @Param("name")String name, @Param("member_id")String member_id, @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime, @Param("begin")int begin, @Param("pageSize")int pageSize);
	int totalCountChannel(@Param("lastLoginFrom")String lastLoginFrom,@Param("registerChannel")String registerChannel,@Param("name")String name, @Param("member_id")String member_id, @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime);

	//渠道充值人数
	Integer selectChannelChargeNum(@Param("lastLoginFrom")String lastLoginFrom,@Param("registerChannel")String registerChannel,@Param("name")String name, @Param("member_id")String member_id, @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime);

	//渠道充值金额
	Double selectChannelChargePrice(@Param("lastLoginFrom")String lastLoginFrom,@Param("registerChannel")String registerChannel,@Param("name")String name, @Param("member_id")String member_id, @Param("chargerelueid")Integer chargerelueid, @Param("state")Integer state, @Param("startTime")String startTime, @Param("endTime")String endTime);


	int selectmemberIdByOrder_no(String out_trade_no);

}
