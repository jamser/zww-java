package com.bfei.icrane.backend.service;

import java.util.List;

import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderGoods;
import com.bfei.icrane.core.models.DollOrderItem;
import com.bfei.icrane.core.models.vo.OutGoods;

public interface OrderService {
	public PageBean<OutGoods> getOrdersByStatus(int page,int pageSize,String phone,String memberId);
	public List<DollOrderItem> getOrderItemByOrderId(Long id);
	public int updateByPrimaryKeySelective(DollOrder dollOrder);
	public PageBean<OutGoods> getOutOrdersByStatus(int page, int pageSize, String phone, int outGoodsId);

	public PageBean<DollOrderGoods> getAllOutOrdersByStatus(int page, int pageSize, String phone);


}
