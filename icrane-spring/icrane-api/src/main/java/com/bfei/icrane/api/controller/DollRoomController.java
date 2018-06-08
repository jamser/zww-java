package com.bfei.icrane.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bfei.icrane.common.util.*;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bfei.icrane.api.service.DollRoomService;
import com.bfei.icrane.api.service.GameService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.pojos.CatchDollPojo;
import com.bfei.icrane.core.pojos.DollImgPojo;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.ValidateTokenService;

@Controller
@RequestMapping(value = "/dollRoom")
@CrossOrigin
public class DollRoomController {
    private static final Logger logger = LoggerFactory.getLogger(DollRoomController.class);

    @Autowired
    private DollRoomService dollRoomService;
    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private DollService dollService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private GameService gameService;

    /**
     * 查询娃娃详情
     *
     * @param dollId 机器Id
     * @param token  登录令牌
     * @return
     */
    @RequestMapping(value = "/dollParticulars", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAccount(Integer dollId, String token) {
        try {
            /*logger.info("查询娃娃详情接口参数:dollId=" + dollId + ",token=" + token);
            if (dollId == null || StringUtils.isEmpty(token)) {
                logger.info("查询娃娃详情接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token)) {
                logger.info("查询娃娃详情接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }*/
            return dollRoomService.selectDollParticularsById(dollId);
        } catch (Exception e) {
            logger.error("查询娃娃详情接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 进入房间
     *
     * @param memberId
     * @param dollId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/enterDoll", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> enterDoll(Integer memberId, Integer dollId, String token) throws Exception {
        logger.info("进房接口参数memberId:{},dollId:{},token:{}" + memberId, dollId, token);
        Map<String, Object> resultMap = new HashedMap<>();
        try {
            // 验证token有效性
            if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return resultMap;
            }
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }
            logger.info("判断 元素是否是集合 key的成员入参getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId));
            //用户进入房间
            gameService.enterDoll(dollId, memberId);
            Member member = memberService.selectById(memberId);
            resultMap.put("memberCoins", member.getAccount().getCoins());
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);

            //logger.info("获取进房信息resultMap:{}", resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("进房出错", e);
            throw e;
        }
    }

    // 退房

    /**
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/exitDoll", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> exitDoll(Integer memberId, String token) throws Exception {
        logger.info("离开房接口参数memberId:{},token:{}" + memberId, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            // DollRoom dollRoom = dollRoomService.getDollId(memberId);
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }
            gameService.exitDoll(memberId);
            /*	清除玩家游戏状态  代码提取方法
             * int dollId;
			logger.info("检查给定 key 是否存在入参getMemberRoomKey:{}" + RedisKeyGenerator.getMemberRoomKey(memberId));
			if (redisUtil.existsKey(RedisKeyGenerator.getMemberRoomKey(memberId))) {
				logger.info("获取dollId入参:{}" + redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(memberId)));
				dollId = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(memberId)));
			} else {
				resultMap.put("success", Enviroment.RETURN_FAILE);
				resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
				resultMap.put("message", "玩家" + memberId + "不在任何房间");
				return resultMap;
			}
			//如果当前玩家还在楼主状态
			if(redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) && memberId!=null &&
					String.valueOf(memberId).equals(redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)))) {
				if(serviceFacade.endPlay(dollId, memberId)) {
					logger.info("退房的时候同时从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId),String.valueOf(memberId));
					redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
					logger.info("退房的时候同时删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(memberId));
					redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(memberId));
				}
			}
			// Doll doll = dollService.selectByPrimaryKey(dollId);
			// if(doll!=null) {
			// doll.setWatchingNumber(doll.getWatchingNumber()-1);
			//
			// }
			logger.info("退房传入参数memberId=" + memberId);
			// Integer getMessege = dollRoomService.outDollRoom(memberId,doll);
			logger.info("判断 元素是否是集合 key的成员入参getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId),
					String.valueOf(memberId));
			if (redisUtil.sIsMember(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId))) {
				// 在缓存房间集合中移除玩家记录
				logger.info("在缓存房间集合中移除玩家记录入参getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId),
						String.valueOf(memberId));
				redisUtil.remSet(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId));
				// 在缓存中删除玩家退出房间的标记
				redisUtil.delKey(RedisKeyGenerator.getMemberRoomKey(memberId));
				logger.info("清除玩家房间标记：" + RedisKeyGenerator.getMemberRoomKey(memberId));
				resultMap.put("success", Enviroment.RETURN_SUCCESS);
				resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
				resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
			} else {
				resultMap.put("success", Enviroment.RETURN_FAILE);
				resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
				resultMap.put("message", "玩家" + memberId + "已不在房间" + dollId);
			}*/

            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            //logger.info("获取退房信息resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("退房出错", e);
            throw e;
        }
    }

    /**
     * 获取房间信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getDollRoom", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollRoom(Integer dollId, Integer count, String token) throws Exception {
       // logger.info("获取房间信息接口参数：dollId:{},token:{},count:{}", dollId, token, count);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        // DollRoom dollRoom = new DollRoom();
        // int offset = 0;
        // int limit = 5;
        if (count == null || count == 0) {
            count = 5;
        }
        try {
            // 验证token有效性
            if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token)) {
                logger.info("判断token有效性：{}", token);
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            Long dollRoomCount = gameService.onLineCount(dollId);
            //logger.info("获取在线人数服务器返回：{}", dollRoomCount);
            if (dollRoomCount != 0) {
                List<String> memberHeadIdList = gameService.takeRoomMembers(dollId, count);
                List<Member> memberHeadList = new ArrayList<>();
                for (String memberHeadId : memberHeadIdList) {
                    Member memberHead = memberService.selectById(Integer.parseInt(memberHeadId));
                    memberHeadList.add(memberHead);
                }
                //logger.info("获取count个用户头像memberHeadList：{}");
                List<DollImgPojo> dollImgList = dollRoomService.getDollImg(dollId);

                Member playMember = null;
                //logger.info("获取在玩用户入参dollId：{}", dollId);
                //String playMemberId = redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId));
                String playMemberId = gameService.takeRoomHostMember(dollId);
                //logger.info("获取在玩用户playMemberId：{}", playMemberId);
                if (playMemberId != null) {
                    playMember = memberService.selectById(Integer.parseInt(playMemberId));
                    //logger.info("在玩用户playMember：{}", playMember);
                }
                Doll doll = dollService.selectByPrimaryKey(dollId);
                List<CatchDollPojo> catchDolls = dollRoomService.getCatchDoll(dollId);
                //logger.info("抓取记录catchDolls：{}", catchDolls);

                resultMap.put("dollRoomCount", dollRoomCount);
                resultMap.put("catchDollList", catchDolls);
                resultMap.put("dollImgList", dollImgList);
                resultMap.put("playMemberMap", playMember);
                resultMap.put("doll", doll);
                resultMap.put("memberHeadList", memberHeadList);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                RedisUtil redisUtil = new RedisUtil();
                resultMap.put("dollLike", redisUtil.sIsMember(RedisKeyGenerator.getLikeDollKey(Integer.valueOf(redisUtil.getString(token))), doll.getDollID()));
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
           // logger.info("获取房间信息resultMap=success");
            return resultMap;
        } catch (Exception e) {
            logger.error("获取房间信息出错", e);
            throw e;
        }
    }

    /**
     * 加入排队
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addQueue", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addQueue(Integer dollId, Integer memberId, String token) throws Exception {
        logger.info("加入排队接口参数dollId:{},memberId:{},token{}", dollId, memberId, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }

            //redisUtil.addListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
            //房间列表添加人
            gameService.addRoomQueue(dollId, memberId);

            //redisUtil.setString(RedisKeyGenerator.getMemberQueueRoomKey(memberId), String.valueOf(dollId));
            //List<String> list = redisUtil.getList(RedisKeyGenerator.getRoomQueueKey(dollId));
            List<String> list = gameService.takeRoomQueue(dollId);
            logger.info("获取排队玩家集合list:{}", list);
            if (list.size() > 0) {
                Integer memberIndex = list.indexOf(String.valueOf(memberId));
                resultMap.put("memberIndex", memberIndex);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("加入排队resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取房间信息出错", e);
            throw e;
        }
    }

    /**
     * 退出排队
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/remQueue", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> remQueue(Integer dollId, Integer memberId, String token) throws Exception {
        logger.info("退出排队接口参数dollId:{},memberId:{},token{}", dollId, memberId, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }
            /*
            logger.info("从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId),
					String.valueOf(memberId));
			redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
			logger.info("删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(memberId));
			redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(memberId));
			
			 redisUtil.setString(RedisKeyGenerator.getMemberClaw(memberId),"0",3600);//两分钟
			 redisUtil.setString(RedisKeyGenerator.getMemberSetter(memberId),"0",3600*2);//清除计算
			 */
            gameService.leaveRoomQueue(dollId, memberId);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            logger.info("退出排队resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取房间信息出错", e);
            throw e;
        }
    }

    /**
     * 查看排队
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getQueue", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getQueue(Integer memberId, String token) throws Exception {
        logger.info("查看排队接口参数memberId:{},token{}", memberId, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }
            //String dollId = redisUtil.getString(RedisKeyGenerator.getMemberQueueRoomKey(memberId));
            Integer dollId = gameService.takeMemberRoomId(memberId);
            logger.info("获取房间号dollId:{}", dollId);
            if (dollId > 0) {
                //List<String> list = redisUtil.getList(RedisKeyGenerator.getRoomQueueKey(Integer.parseInt(dollId)));
                List<String> list = gameService.takeRoomQueue(dollId);
                logger.info("获取房间里的玩家集合list:{}", list);
                Integer memberIndex = list.indexOf(String.valueOf(memberId));
                logger.info("剩余排队人数memberIndex:{}", memberIndex);
                resultMap.put("dollId", dollId);
                resultMap.put("memberIndex", memberIndex);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("resultMap:{}", resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取房间信息出错", e);
            throw e;
        }
    }

    /**
     * 获取房间抓取成功记录
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getCatchSuccuss", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getCatchSuccuss(Integer dollId, String token) throws Exception {
        logger.info("获取房间抓取成功记录：dollId:{},token:{}", dollId, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            /*if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token)) {
                logger.info("判断token有效性：{}", token);
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }*/

            List<CatchDollPojo> catchDolls = dollRoomService.getCatchDoll(dollId);
            resultMap.put("catchDollList", catchDolls);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            logger.info("获取房间信息resultMap=success");
            return resultMap;
        } catch (Exception e) {
            logger.error("获取房间信息出错", e);
            throw e;
        }
    }

}
