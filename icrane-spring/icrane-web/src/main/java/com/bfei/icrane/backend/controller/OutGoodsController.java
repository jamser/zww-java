package com.bfei.icrane.backend.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bfei.icrane.core.models.DollOrderGoods;
import com.bfei.icrane.core.models.vo.OutGoods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bfei.icrane.backend.service.OrderService;
import com.bfei.icrane.backend.service.OutGoodsService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollOrder;
import com.bfei.icrane.core.models.DollOrderItem;

/**
 * @ClassName: OutGoodsController 
 * @Description: 发货管理
 * @author perry 
 * @date 2017年10月24日 下午1:51:06 
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/goodsManage")
public class OutGoodsController {
	private final Logger logger = LoggerFactory.getLogger(OutGoodsController.class);

	@Autowired
	private OrderService orderService;

	
	@RequestMapping("/list")
	public String list(String memberId, String phone, @RequestParam(defaultValue="1")Integer page, @RequestParam(defaultValue="10")Integer pageSize, HttpServletRequest request)throws Exception {
		logger.info("查询发货列表输入参数:phone={},page={},pageSize={}",phone,page,pageSize);
		try {
			if("".equals(memberId)){
				memberId=null;
			}
			if("".equals(phone)){
				phone=null;
			}
			PageBean<OutGoods> pageBean = orderService.getOrdersByStatus(page, pageSize,phone,memberId);
			logger.info("查询发货列表结果:{}",pageBean.getList().toString());
			request.setAttribute("pageBean", pageBean);
			request.setAttribute("phone", phone);
			request.setAttribute("memberId", memberId);
			return "goods/goods_list";
		} catch (Exception e) {
			logger.error("查询发货列表SystemError:"+e);
			throw e;
		}
	}

	@RequestMapping("/outDetailList")
	public String outDetailList(String phone,@RequestParam(defaultValue="1")Integer page,@RequestParam(defaultValue="10")Integer pageSize,HttpServletRequest request)throws Exception {
		logger.info("查询发货列表详细信息输入参数:phone={},page={},pageSize={}",phone,page,pageSize);
		try {
			PageBean<DollOrderGoods> pageBean = orderService.getAllOutOrdersByStatus(page, pageSize,phone);
			logger.info("查询发货列表详细信息结果:{}",pageBean.getList().toString());
			request.setAttribute("pageBean", pageBean);
			request.setAttribute("phone", phone);
			return "goods/goods_outing_list";
		} catch (Exception e) {
			logger.error("查询发货列表详细信息SystemError:"+e);
			throw e;
		}
	}

	@RequestMapping("/dollOrder")
	public String dollOrder(Long id,HttpServletRequest request)throws Exception {
		logger.info("查询订单详情输入参数:id={}",id);
		try {
			List<DollOrderItem> list = orderService.getOrderItemByOrderId(id);
			logger.info("查询订单详情结果 :{}",list.toString());
			request.setAttribute("itemList", list);	
			return "goods/goods_item";
		} catch (Exception e) {
			logger.error("查询订单详情SystemError:"+e);
			throw e;
		}
	}
	
	@RequestMapping("/toEdit")
	public String toEdit(Integer id,HttpServletRequest request)throws Exception {
		logger.info("编辑发货订单输入参数:id={}",id);
		request.setAttribute("orderId", id);	
		return "goods/goods_edit";
	}
	
	@RequestMapping(value="/orderUpdate",method=RequestMethod.POST)
	@ResponseBody
	public String dollUpdate(DollOrder dollOrder) throws Exception{
		logger.info("更新快递信息传入参数dollOrder：{}",dollOrder.toString());
		try {
			int result = orderService.updateByPrimaryKeySelective(dollOrder);
			logger.info("更新快递信息结果：{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("更新快递信息SystemError："+e);
			throw e;
		}
	}
	
	@RequestMapping("/outList")
	public String outList(@RequestParam(defaultValue="0")Integer outGoodsId,String phone,@RequestParam(defaultValue="1")Integer page,@RequestParam(defaultValue="10")Integer pageSize,HttpServletRequest request)throws Exception {
		logger.info("查询已发货列表输入参数:outGoosId={},phone={},page={},pageSize={}",outGoodsId,phone,page,pageSize);
		try {
			PageBean<OutGoods> pageBean = orderService.getOutOrdersByStatus(page, pageSize,phone,outGoodsId);
			logger.info("查询已发货列表结果:{}",pageBean.getList().toString());
			request.setAttribute("pageBean", pageBean);
			request.setAttribute("phone", phone);
			request.setAttribute("outGoodsId",outGoodsId);
			return "goods/outgoods_list";
		} catch (Exception e) {
			logger.error("查询已发货列表SystemError:"+e);
			throw e;
		}
	}
}
