package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.facade.ServiceFacade;
import com.bfei.icrane.api.service.DollMonitorService;
import com.bfei.icrane.api.service.DollRoomService;
import com.bfei.icrane.api.service.GameService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.pojos.MemberInfoPojo;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.game.GameStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/29
 * Description: 抓娃娃游戏过程控制层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/game")
@CrossOrigin
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    static PropFileManager propFileMgr = new PropFileManager("interface.properties");
    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private DollMonitorService dollMonitorService;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private GameService gameService;
    @Autowired
    private DollRoomService dollRoomService;
    RedisUtil redisUtil = new RedisUtil();

    //开始本轮游戏建立socket前调用的接口
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult startGame(@RequestParam Integer memberId, @RequestParam Integer dollId, @RequestParam String token) throws Exception {
        logger.info("开始本轮游戏建立socket前调用的接口参数memberId=" + memberId + "," + "dollId=" + dollId + "," + "token=" + token);
        try {
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
//            Integer userId = gameService.takeToken2Member(token);
//            if (userId > 0) {//兼容memberId 传错了 userId
//                memberId = userId;
//            }
            GameStatusEnum result = gameService.startPlay(dollId, memberId);
            return getResult(result, memberId, dollId);

			/*if(!doll.getMachineStatus().equals("空闲中")) { //娃娃机状态不在空闲中
                if(redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) &&
						redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)).equals(String.valueOf(memberId))) {
			} else {
				}
			}*/

            //Thread.sleep(500);
            //产生扣费记录
            /*
            if (dollRoomService.startPlay(dollId, memberId)) {
				//if (dollRoomService.startPlay(dollId, member)) {
				 //设置用户当前下抓id
				logger.info("玩家"+memberId+"在娃娃机"+dollId+"的游戏开始成功");
				Map<String, String> socketMap = new HashMap<String, String>();
				socketMap.put("socketUrl", serviceFacade.getSocketUrl(dollId));
				return IcraneResult.ok(socketMap);
			} else {
				logger.info("玩家"+memberId+"在娃娃机"+dollId+"的游戏开始失败");
				return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
			}*/
        } catch (Exception e) {
            logger.error("调用游戏开始接口出错！", e);
            throw e;
        }
    }

    public IcraneResult getResult(GameStatusEnum result, Integer memberId, Integer dollId) {
        switch (result) {
            case GAME_PRICE_NOT_ENOUGH:
                logger.info("玩家" + memberId + "在娃娃机" + dollId + "的游戏不足，无法开始游戏");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "您的游戏币不足，请先充值！");
            case GAME_PRICE_NOT_SuperTicket:
                logger.info("玩家" + memberId + "在娃娃机" + dollId + "的钻石不足，无法开始游戏");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "您的钻石不足，请先充值！");
            case GAME_MAINTAINED:
                logger.info("机器维护中，无法开始游戏");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "机器抢救中，无法开始游戏");
            case GAME_PLAYING:
                logger.info("玩家" + memberId + "在娃娃机" + dollId + "的房间有人已经开始游戏，无法开始游戏");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "当前还有玩家在操作，不能开始游戏！");
            case GAME_START_SUCCESS:
                Map<String, String> socketMap = new HashMap<>();
                socketMap.put("socketUrl", serviceFacade.getSocketUrl(dollId));
                return IcraneResult.ok(socketMap);
            case GAME_START_FAIL:
                logger.info("玩家" + memberId + "在娃娃机" + dollId + "的游戏开始失败");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            default:
                logger.info("玩家" + memberId + "在娃娃机" + dollId + "的游戏开始失败");
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
        }
    }

    /**
     * 再来一局
     *
     * @param memberId
     * @param dollId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/again", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult againGame(@RequestParam Integer memberId, @RequestParam Integer dollId, @RequestParam String token) throws Exception {
        logger.info("开始本轮游戏建立socket前调用的接口参数memberId=" + memberId + "," + "dollId=" + dollId + "," + "token=" + token);
        try {
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            GameStatusEnum result = gameService.checkPlaying(dollId, memberId);
            return getResult(result, memberId, dollId);
        } catch (Exception e) {
            logger.error("调用游戏再一次接口出错！", e);
            throw e;
        }
    }


    /**
     * 娃娃机下抓接口
     *
     * @param memberId
     * @param dollId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/claw", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult consumeGame(@RequestParam Integer memberId, @RequestParam Integer dollId, @RequestParam String token, String state) throws Exception {
        // logger.info("客户端接收到claw的消息后，立即调用此接口,扣费操作，生成消费记录参数memberId=" + memberId + "," + "dollId=" + dollId + "," + "token=" + token);
        try {
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            if (redisUtil.getString("claw" + memberId + dollId) != null) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE1, "操作频繁");
            }
            redisUtil.setString("claw" + memberId + dollId, "", Enviroment.ACCESS_SENDDOLL_TIME);

            String gameNum = gameService.takeGameNum(memberId, dollId);
            if (gameNum == null || "".equals(gameNum)) {
                logger.info("claw==>userId={},dollId={},gameNum={}", memberId, dollId, gameNum);
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "游戏开始异常");//游戏开始异常 不扣费
            }
            GameStatusEnum result = gameService.consumePlay(dollId, memberId, state);
            Member member = memberService.selectById(memberId);
            switch (result) {
                case GAME_CONSUME_SUCCESS:
                    Map<String, Integer> memberCoinsMap = new HashMap<String, Integer>();
                    //memberCoinsMap.put("gameNum", gameNum);
                    memberCoinsMap.put("memberCoins", member.getAccount().getCoins());
                    memberCoinsMap.put("memberSuperTicket", member.getAccount().getSuperTicket());
                    return IcraneResult.ok(memberCoinsMap);
                case GAME_CONSUME_FAIL:
                default:
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
            /*
            //扣费操作，生成消费记录
			Member member = memberService.selectById(memberId);
			Doll doll = dollService.selectByPrimaryKey(dollId);
			
			//if(member.getAccount().getCoins() < doll.getPrice()) {
			//	return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "娃娃币不足，清先充值！");
			//}  开始的时候判断   
			if (dollRoomService.consumePlay(doll, member)) {
				Map<String, Integer> memberCoinsMap = new HashMap<String, Integer>();
				memberCoinsMap.put("memberCoins", member.getAccount().getCoins());
				return IcraneResult.ok(memberCoinsMap);
			} else {
				return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
			
			}
			*/
        } catch (Exception e) {
            logger.error("调用游戏抓取后消费接口出错！", e);
            throw e;
        }
    }

    //客户端收到”idle”或者”gotToy”消息后，立即调用此接口
    @RequestMapping(value = "/endRound", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult endRound(@RequestParam Integer memberId, @RequestParam Integer dollId, Integer gotDoll, @RequestParam String token, String state, Integer version) throws Exception {
        //logger.info("结束本轮游戏endRound token="+token);
        logger.info("结束本轮游戏保持socket连接并调用的接口memberId=" + memberId + "," + "dollId=" + dollId + "," + "gotDoll" + gotDoll + "," + "token=" + token + ",state=" + state);
        try {
            //验证token有效性
            if (!validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }

            if (redisUtil.getString("endRound" + memberId + dollId) != null) {
                logger.info("endRound频繁访问userId={},dollId={}", memberId, dollId);
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE1, "操作频繁");
            }
            redisUtil.setString("endRound" + memberId + dollId, "", Enviroment.ACCESS_SENDDOLL_TIME);
            /*Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }*/
            //String gameNum = StringUtils.getCatchHistoryNum().replace("-", "").substring(0, 20);
            String gameNum = gameService.takeGameNum(memberId, dollId);
            GameStatusEnum result = gameService.endRound(dollId, memberId, gotDoll, gameNum, state);
            switch (result) {
                case GAME_END_ROUND_SUCCESS:
                    //频繁抓中娃娃处理
                    dollRoomService.endPlayByCatchCount(memberId, dollId);
                    Map<String, Object> gameNumMap = new HashMap<>();
                    gameNumMap.put("gameNum", gameNum);
                    String stsTokenUrl = propFileMgr.getProperty("aliyun.sts");
                    if (stsTokenUrl != null) {
                        gameNumMap.put("stsTokenUrl", stsTokenUrl + "?userId=" + memberId + "&token=" + token);
                    }
//                    Integer machineType = dollService.selectTypeById(dollId);
//                    switch (machineType) {
//                        case 0:
//                        case 1:
//                        case 2:
//                            break;
//                        case 3:
//                            if (gotDoll == 1) {
//                                RedisUtil redisUtil = new RedisUtil();
//                                //分享标记
//                                redisUtil.setString(RedisKeyGenerator.getShareKey(memberId, gameNum), "1", 24 * 60 * 60 * 30);
//                                gameNumMap.putAll(gameService.getShareUrl(dollId, memberId, version));
//                            }
//                            break;
//                        default:
//                            break;
//                    }
                    return IcraneResult.ok(gameNumMap);
                case GAME_END_FAIL:
                default:
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
            /*
            if (serviceFacade.endRound(dollId, memberId, gotDoll)) {
				return IcraneResult.ok();
			} else {
			}*/
        } catch (Exception e) {
            logger.error("调用本轮游戏结束接口出错！", e);
            throw e;
        }
    }

    //结束本次游戏关闭socket后调用的接口
    @RequestMapping(value = "/end", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult endGame(Integer memberId, Integer dollId, String token) throws Exception {
        logger.info("结束本次游戏关闭");
        // logger.info("结束本次游戏关闭socket后调用的接口memberId=" + memberId + "," + "dollId=" + dollId + "," + "token=" + token);
        try {
            //验证token有效性
            if (token == null || "".equals(token) ||
                    !validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
//            Integer userId = gameService.takeToken2Member(token);
//            if (userId > 0) {//兼容memberId 传错了 userId
//                memberId = userId;
//            }
            GameStatusEnum result = gameService.end(dollId, memberId);
            switch (result) {
                case GAME_END:
                    return IcraneResult.ok();
                case GAME_END_FAIL:
                default:
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
            /*
            if(!redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) ||
					String.valueOf(memberId).equals(redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)))) {
				if (serviceFacade.endPlay(dollId, memberId)) {
					logger.info("从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId),
							String.valueOf(memberId));
					redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
					logger.info("删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(memberId));
					redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(memberId));
					return IcraneResult.ok();
				} else {
					return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
				
				}
			}else {
				logger.info("用户:{}不是娃娃机:{}的楼主，不做结束游戏的更新操作！", memberId, dollId);
				return IcraneResult.ok();
			}*/

        } catch (Exception e) {
            logger.error("调用游戏结束接口出错！", e);
            throw e;
        }
    }

    //刷新指定数量的在线用户的基本信息（头像）
    @RequestMapping(value = "/refreshMembers", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult refreshMembers(Integer members, Integer dollId, String token) throws Exception {

        try {
            //验证token有效性
           /* if (token == null || "".equals(token) ||
                    !validateTokenService.validataToken(token)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }*/

            if (members == null) {
                members = 5;
            }
            //logger.info("刷新指定数量" + members + "的在线用户的基本信息（头像）" + "," + "token=" + token);
            //List<String> memberIds = redisUtil.srandMember(RedisKeyGenerator.getRoomKey(dollId), members);
            List<String> memberIds = gameService.takeRoomMembers(dollId, members);
            //String memberInfoKey;
            List<MemberInfoPojo> resultData = new ArrayList<MemberInfoPojo>();
            for (String memberHeadId : memberIds) {
                int memberId = Integer.parseInt(memberHeadId);
                //memberInfoKey = RedisKeyGenerator.getMemberInfoKey(memberId);
                MemberInfoPojo memberInfoPojo = new MemberInfoPojo();
                //if(redisUtil.existsKey(memberInfoKey)){
                if (gameService.existsMemberInfoKey(memberId)) {
                    memberInfoPojo = gameService.takeMemberInfoPojo(memberId);
                    resultData.add(memberInfoPojo);
                } else {
                    Member member = memberService.selectById(memberId);
                    if (member != null) {
                        memberInfoPojo.setId(memberId);
                        memberInfoPojo.setName(member.getName());
                        memberInfoPojo.setGender(member.getGender());
                        memberInfoPojo.setIconRealPath(member.getIconRealPath());
                        memberInfoPojo.setMemberID(member.getMemberID());
                        memberInfoPojo.setLevel(String.valueOf(member.getAccount().getVip().getLevel()));
                        resultData.add(memberInfoPojo);
                        gameService.addMemberInfoPojo(memberId, memberInfoPojo);//加入缓存
                    }
                }
            }
            return IcraneResult.ok(resultData);
        } catch (Exception e) {
            logger.error("调用刷新指定数量的在线用户的基本信息接口出错！", e);
            throw e;
        }
    }

    //客户端检测到socket连接异常后，调用此接口
    @RequestMapping(value = "/alert", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult gameAlert(Integer memberId, Integer dollId, String token) throws Exception {
        logger.info("客户端检测到socket连接异常后调用的报警接口dollId=" + dollId + "," + "token=" + token);
        try {
            //验证token有效性
            if (token == null || "".equals(token) ||
                    !validateTokenService.validataToken(token, memberId)) {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Integer userId = gameService.takeToken2Member(token);
            if (userId > 0) {//兼容memberId 传错了 userId
                memberId = userId;
            }

            if (dollMonitorService.gameAlert(memberId, dollId)) {
                return IcraneResult.ok();
            } else {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);

            }
        } catch (Exception e) {
            logger.error("调用游戏状态报警接口出错！", e);
            throw e;
        }
    }

    //获取游戏结果
    @RequestMapping(value = "/gameResult", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap gameResult(Integer memberId, Integer dollId, String token) throws Exception {
        try {
            if (memberId == null || dollId == null || StringUtils.isEmpty(token)) {
                logger.info("获取游戏结果接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("gameResult", gameService.gameResult(memberId, dollId));
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, map);
        } catch (Exception e) {
            logger.error("调用游戏状态报警接口出错！", e);
            throw e;
        }
    }

    /**
     * 分享得币
     *
     * @param memberId 用户UserId
     * @param token    用户Token
     * @param gameNum  游戏编号
     * @return
     */
//    @RequestMapping(value = "/share", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap share(Integer memberId, String token, String gameNum) {
//        try {
//            logger.info("分享得币参数memberId=" + memberId + ",token" + token + ",gameNum" + gameNum);
//            //验证参数
//            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(gameNum)) {
//                logger.info("分享得币接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //访问间隔限制
//            RedisUtil redisUtil = new RedisUtil();
//            if (redisUtil.getString("share" + memberId) != null) {
//                logger.info("分享得币失败=" + Enviroment.PLEASE_SLOW_DOWN);
//                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.PLEASE_SLOW_DOWN);
//            }
//            redisUtil.setString("share" + memberId, "", 20);
//            //验证token
//            Integer iMemberId = Integer.valueOf(memberId);
//            if (!validateTokenService.validataToken(token, iMemberId)) {
//                logger.info("分享得币接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            Integer result = gameService.chackShare(memberId, gameNum);
//            switch (result) {
//                case 1:
//                    logger.info("分享得币成功=" + Enviroment.YOU_ALREADY_SHARED);
//                    Map<String, Object> share = gameService.Share(memberId);
//                    return new ResultMap("分享成功", share);
//                case 2:
//                    logger.info("分享得币失败=" + Enviroment.YOU_ALREADY_SHARED);
//                    return new ResultMap(Enviroment.FAILE_CODE, Enviroment.YOU_ALREADY_SHARED);
//                default:
//                    logger.info("分享得币失败=" + Enviroment.ERROR);
//                    return new ResultMap(Enviroment.FAILE_CODE, Enviroment.ERROR);
//            }
//        } catch (Exception e) {
//            logger.error("分享得币出错", e);
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.SHARE_EXCEPTION);
//        }
//    }

}




