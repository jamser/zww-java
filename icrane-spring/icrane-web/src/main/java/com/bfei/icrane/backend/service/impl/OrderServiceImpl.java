package com.bfei.icrane.backend.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bfei.icrane.core.dao.DollOrderGoodsDao;
import com.bfei.icrane.core.dao.OutGoodsListDao;
import com.bfei.icrane.core.models.DollOrderGoods;
import com.bfei.icrane.core.models.vo.OutGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bfei.icrane.backend.service.OrderService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.dao.DollOrderDao;
import com.bfei.icrane.core.dao.DollOrderItemDao;
import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderItem;

@Service("OrderService")
public class OrderServiceImpl implements OrderService{
	
	@Autowired
	private DollOrderDao dollOrderDao;
	@Autowired
	private DollOrderItemDao dollOrderItemDao;
	@Autowired
	private OutGoodsListDao outGoodsListDao;
	@Autowired
	private DollOrderGoodsDao dollOrderGoodsDao;
	@Override
	public PageBean<OutGoods> getOrdersByStatus(int page,int pageSize,String phone,String memberId) {
		PageBean<OutGoods> pageBean = new PageBean<OutGoods>();
		pageBean.setPage(page);
		pageBean.setPageSize(pageSize);
		int totalCount = 0;
		totalCount=outGoodsListDao.totalCount(phone,memberId);
		pageBean.setTotalCount(totalCount);
		int totalPage=0;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		pageBean.setTotalPage(totalPage);
		int begin = (page - 1) * pageSize;
		List<OutGoods> list = outGoodsListDao.getOrdersByStatus(begin, pageSize, phone, memberId);
		pageBean.setList(list);
		int start=page%10==0?(page-1)/10*10+1:page/10*10+1;
		int end=page%10==0?((page-1)/10*10+10>totalPage?totalPage:(page-1)/10*10+10):(page/10*10+10>totalPage?totalPage:page/10*10+10);
		pageBean.setStart(start);
		pageBean.setEnd(end);
		return pageBean;
	}

	@Override
	public List<DollOrderItem> getOrderItemByOrderId(Long id) {
		return dollOrderItemDao.getOrderItemByOrderId(id);
	}

	@Override
	public int updateByPrimaryKeySelective(DollOrder dollOrder) {
		dollOrder.setStatus("已发货");
		dollOrder.setModifiedDate(new Date());
		dollOrder.setDeliverDate(new Date());
		return dollOrderDao.updateByPrimaryKeySelective(dollOrder);
	}

	@Override
	public PageBean<OutGoods> getOutOrdersByStatus(int page, int pageSize, String phone, int outGoodsId) {
		PageBean<OutGoods> pageBean = new PageBean<OutGoods>();
		pageBean.setPage(page);
		pageBean.setPageSize(pageSize);
		int totalCount = 0;
		totalCount= outGoodsListDao.dollOrderTotalCount(phone,outGoodsId);
		pageBean.setTotalCount(totalCount);
		int totalPage=0;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		pageBean.setTotalPage(totalPage);
		int begin = (page - 1) * pageSize;
		List<OutGoods> list = outGoodsListDao.getOutOrdersByStatus(begin, pageSize,phone,outGoodsId);
		pageBean.setList(list);
		int start=page%10==0?(page-1)/10*10+1:page/10*10+1;
		int end=page%10==0?((page-1)/10*10+10>totalPage?totalPage:(page-1)/10*10+10):(page/10*10+10>totalPage?totalPage:page/10*10+10);
		pageBean.setStart(start);
		pageBean.setEnd(end);
		return pageBean;
	}

	@Override
	public PageBean<DollOrderGoods> getAllOutOrdersByStatus(int page, int pageSize, String phone) {
		PageBean<DollOrderGoods> pageBean = new PageBean<DollOrderGoods>();
		pageBean.setPage(page);
		pageBean.setPageSize(pageSize);
		int totalCount = 0;
		totalCount= dollOrderGoodsDao.totalCountAll(phone);
		pageBean.setTotalCount(totalCount);
		int totalPage=0;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		pageBean.setTotalPage(totalPage);
		int begin = (page - 1) * pageSize;
		List<DollOrderGoods> list = dollOrderGoodsDao.getAllOrdersByStatus(begin, pageSize, phone);
		pageBean.setList(list);
		int start=page%10==0?(page-1)/10*10+1:page/10*10+1;
		int end=page%10==0?((page-1)/10*10+10>totalPage?totalPage:(page-1)/10*10+10):(page/10*10+10>totalPage?totalPage:page/10*10+10);
		pageBean.setStart(start);
		pageBean.setEnd(end);
		return pageBean;
	}
}
