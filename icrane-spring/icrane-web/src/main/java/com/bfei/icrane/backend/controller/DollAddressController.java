package com.bfei.icrane.backend.controller;

import com.bfei.icrane.backend.service.DollAddressService;
import com.bfei.icrane.backend.service.DollTopicService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollAddress;
import com.bfei.icrane.core.models.DollTopic;
import com.bfei.icrane.core.service.DollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/dollAddressManage")
public class DollAddressController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private DollAddressService dollAddressService;

	@RequestMapping("/list")
	public String list(@RequestParam(defaultValue="1")Integer page,@RequestParam(defaultValue="50")Integer pageSize,HttpServletRequest request)throws Exception {
		logger.info("查询机器地址输入参数:page={},pageSize={}",page,pageSize);
		try {
			PageBean<DollAddress> pageBean = dollAddressService.getDollAddressList(page, pageSize);
			logger.info("查询机器地址列表结果:{}",pageBean.getList().toString());
			request.setAttribute("pageBean", pageBean);
			return "doll/doll_address_list";
		} catch (Exception e) {
			logger.error("查询机器地址列表SystemError:"+e);
			throw e;
		}
	}


	@RequestMapping("/dollAddressToAdd")
	public String toAdd(HttpServletRequest request)throws Exception{
		return "doll/doll_address_add";
	}

	//添加
	@RequestMapping(value="/dollAddressAdd",method=RequestMethod.POST)
	@ResponseBody
	public String dollAddressAdd(DollAddress dollAddress)throws Exception{
		logger.info("新增机器地址传入参数：{}",dollAddress.toString());
		try {
			int result = dollAddressService.insertSelective(dollAddress);
			logger.info("新增机器地址结果:{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("新增机器地址SystemError："+e);
			throw e;
		}
	}


	/**
	* @Title: dollDel 
	* @Description: 删除机器地址
	 */
	@RequestMapping(value="/dollAddressDel",method=RequestMethod.POST)
	@ResponseBody
	public String dollDel(Integer id) throws Exception{
		logger.info("删除机器地址id：{}",id);
		try {
			int result = dollAddressService.deleteByPrimaryKey(id);
			logger.error("删除机器地址结果：{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("删除机器地址SystemError："+e);
			throw e;
		}
	}

	//编辑
	@RequestMapping("/toEditDollAddress")
	public String toEditMember(Integer id,HttpServletRequest request) throws Exception{
		logger.info("跳转编辑地址id:{}",id);
		try {
			DollAddress dollAddress = dollAddressService.getDollAddressById(id);
			logger.info("编辑地址查询用户:{}",dollAddress!=null?dollAddress.toString():"无此主题");
			request.setAttribute("dollAddress",dollAddress);
			return "doll/doll_address_edit";
		} catch (Exception e) {
			logger.error("编辑地址SystemError："+e);
			throw e;
		}
	}

	/**
	 * 地址更新
	 * @param
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/dollAddressUpdate",method=RequestMethod.POST)
	@ResponseBody
	public String dollTopicUpdate(DollAddress dollAddress,HttpServletRequest request) throws Exception{
		logger.info("更新地址单传入参数member：{}",dollAddress!=null?dollAddress.toString():null);
		try {
			int result = dollAddressService.updateByPrimaryKeySelective(dollAddress,request);
			logger.info("更新地址结果：{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("更新地址SystemError："+e);
			throw e;
		}
	}


}

