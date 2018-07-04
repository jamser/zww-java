package com.bfei.icrane.backend.controller;

import com.bfei.icrane.backend.service.MemberWhiteService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.MemberWhite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/memberWhite")
public class MemberWhiteController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private MemberWhiteService memberService;
	
	@RequestMapping("/list")
	public String list(String name,@RequestParam(defaultValue="1")Integer page,@RequestParam(defaultValue="50")Integer pageSize,HttpServletRequest request)throws Exception {
		logger.info("查询用户列表输入参数:name={},page={},pageSize={}",name,page,pageSize);
		try {
			PageBean<MemberWhite> pageBean = memberService.getUserList(page, pageSize,name);
			logger.info("查询用户列表结果:{}",pageBean.getList().toString());
			request.setAttribute("pageBean", pageBean);
			request.setAttribute("name", name);
			return "user/user_white_list";
		} catch (Exception e) {
			logger.error("查询用户列表SystemError:"+e);
			throw e;
		}
	}


	@RequestMapping("/memberWhiteToAdd")
	public String toAdd()throws Exception{
		return "user/user_white_add";
	}

	//添加
	@RequestMapping(value="/memberWhiteAdd",method=RequestMethod.POST)
	@ResponseBody
	public String memberWhiteInsert(MemberWhite memberWhite)throws Exception{
		logger.info("新增白名单传入参数：{}",memberWhite.toString());
		try {
			int result = memberService.insertSelective(memberWhite);
			logger.info("新增白名单结果:{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("新增白名单SystemError："+e);
			throw e;
		}
	}


	/**
	* @Title: dollDel 
	* @Description: 删除白名单
	 */
	@RequestMapping(value="/memberDel",method=RequestMethod.POST)
	@ResponseBody
	public String dollDel(Integer id) throws Exception{
		logger.info("删除白名单id：{}",id);
		try {
			int result = memberService.memberDel(id);
			logger.error("删除白名单结果：{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("删除白名单SystemError："+e);
			throw e;
		}
	}

	//编辑
	@RequestMapping("/toEditMemberWhite")
	public String toEditMember(Integer id,HttpServletRequest request) throws Exception{
		logger.info("跳转编辑用户id:{}",id);
		try {
			MemberWhite member = memberService.getMemberById(id);
			logger.info("编辑用户查询用户:{}",member!=null?member.toString():"无此白名单");
			request.setAttribute("member",member);
			return "user/user_white_edit";
		} catch (Exception e) {
			logger.error("编辑用户SystemError："+e);
			throw e;
		}

	}

	/**
	 * 用户更新
	 * @param member
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/memberUpdate",method=RequestMethod.POST)
	@ResponseBody
	public String memberUpdate(MemberWhite member,HttpServletRequest request) throws Exception{
		logger.info("更新白名单传入参数member：{}",member!=null?member.toString():null);
		try {
			int result = memberService.updateByPrimaryKeySelective(member,request);
			logger.info("更新白名单结果：{}",result>0?"success":"fail");
			if(result>0) {
				return "1";
			}else {
				return "0";
			}
		} catch (Exception e) {
			logger.error("更新白名单SystemError："+e);
			throw e;
		}
	}


}

