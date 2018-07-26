package com.bfei.icrane.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: SrvReqController 
 * @Description: 工单控制类 
 * @author perry 
 * @date 2017年9月27日 下午2:29:05 
 * @version V1.0
 */
@Controller
@RequestMapping("/srvReq")
@CrossOrigin
public class SrvReqController {
//	private static final Logger logger = LoggerFactory.getLogger(SrvReqController.class);
//	@Autowired
//	private SrvReqService srvReqService;
//	@Autowired
//	private ValidateTokenService validateTokenService;
//	/**
//	 * 新增工单
//	 */
//	@RequestMapping(value="/insertSr",method=RequestMethod.POST)
//	@ResponseBody
//	public IcraneResult insertAddr(SrvReq sr,String token) throws Exception{
//		logger.info("新增工单接口参数sr="+sr+","+"token="+token);
//		try {
//			boolean isToken = validateTokenService.validataToken(token);
//			if(isToken) {
//				Integer result = srvReqService.insertSelective(sr);
//				if(result>0) {
//					return IcraneResult.ok();
//				}else {
//					return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,Enviroment.RETURN_FAILE_MESSAGE);
//				}
//			}else {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,"400","token已失效");
//			}
//
//		} catch (Exception e) {
//			logger.error("新增工单出错",e);
//			throw e;
//		}
//	}
//	/**
//	 * 删除工单
//	 */
//	@RequestMapping(value="/deleteSr",method=RequestMethod.POST)
//	@ResponseBody
//	public IcraneResult deleteSr(Integer id,String token) throws Exception{
//		logger.info("删除工单接口参数id="+id+","+"token="+token);
//		try {
//			boolean isToken = validateTokenService.validataToken(token);
//			if(isToken) {
//				Integer result = srvReqService.deleteByPrimaryKey(id);
//				if(result>0) {
//					return IcraneResult.ok();
//				}else {
//					return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,Enviroment.RETURN_FAILE_MESSAGE);
//				}
//			}else {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,"400","token已失效");
//			}
//
//		} catch (Exception e) {
//			logger.error("删除工单出错",e);
//			throw e;
//		}
//	}
//	/**
//	 * 更新工单
//	 */
//	@RequestMapping(value="/updateSr",method=RequestMethod.POST)
//	@ResponseBody
//	public IcraneResult updateSr(SrvReq sr,String token) throws Exception{
//		try {
//			logger.info("更新工单接口参数sr="+sr+","+"token="+token);
//			boolean isToken = validateTokenService.validataToken(token);
//			if(isToken) {
//				Integer result = srvReqService.updateByPrimaryKeySelective(sr);
//				if(result>0) {
//					return IcraneResult.ok();
//				}else {
//					return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,Enviroment.RETURN_FAILE_MESSAGE);
//				}
//			}else {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,"400","token已失效");
//			}
//
//		} catch (Exception e) {
//			logger.error("更新工单出错",e);
//			throw e;
//		}
//	}
//	/**
//	 * 根据id查询工单
//	 */
//	@RequestMapping(value="/findSrById",method=RequestMethod.POST)
//	@ResponseBody
//	public IcraneResult findSrById(Integer id,String token) throws Exception{
//		logger.info("根据id查询工单接口参数id="+id+","+"token="+token);
//		try {
//			boolean isToken = validateTokenService.validataToken(token);
//			if(isToken) {
//				SrvReq sr = srvReqService.selectByPrimaryKey(id);
//				logger.info("工单sr="+sr);
//				if(sr!=null) {
//					return IcraneResult.ok(sr);
//				}else {
//					return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,Enviroment.RETURN_FAILE_MESSAGE);
//				}
//			}else {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,"400","token已失效");
//			}
//
//		} catch (Exception e) {
//			logger.error("根据id查询工单出错",e);
//			throw e;
//		}
//	}
//	/**
//	 * 分页查询所有工单
//	 */
//	@RequestMapping(value="/findSr",method=RequestMethod.POST)
//	@ResponseBody
//	public IcraneResult findSr(Integer createdBy,@RequestParam(value="page",defaultValue="1")Integer page,@RequestParam(value="pageSize",defaultValue="10")Integer pageSize,String token) throws Exception{
//			logger.info("分页查询所有工单接口参数createdBy="+createdBy+","+"page="+page+","+"pageSize="+pageSize+","+"token"+token);
//		try {
//			if(createdBy==null) {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,"用户ID不存在");
//			}
//			boolean isToken = validateTokenService.validataToken(token);
//			if(isToken) {
//				PageBean<SrvReq> pageBean = srvReqService.select(createdBy,page, pageSize);
//				logger.info("所有工单pageBean="+pageBean);
//				if(pageBean!=null) {
//					return IcraneResult.ok(pageBean);
//				}else {
//					return IcraneResult.build(Enviroment.RETURN_FAILE,Enviroment.RETURN_FAILE_CODE,Enviroment.RETURN_FAILE_MESSAGE);
//				}
//			}else {
//				return IcraneResult.build(Enviroment.RETURN_FAILE,"400","token已失效");
//			}
//
//		} catch (Exception e) {
//			logger.error("分页查询所有工单出错",e);
//			throw e;
//		}
//	}
}
