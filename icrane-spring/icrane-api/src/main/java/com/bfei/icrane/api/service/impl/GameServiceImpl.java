package com.bfei.icrane.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bfei.icrane.api.service.*;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.OemMapper;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bfei.icrane.api.facade.ServiceFacade;
import com.bfei.icrane.core.pojos.MemberInfoPojo;
import com.bfei.icrane.game.GameProcessUtil;
import com.bfei.icrane.game.GameStatusEnum;

@Service("gameService")
public class GameServiceImpl implements GameService {
    protected static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    RedisUtil redisUtil = new RedisUtil();

    @Autowired
    private DollRoomService dollRoomService;
    @Autowired
    private DollService dollService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ServiceFacade serviceFacade;
    @Autowired
    private DivinationService divinationService;
    @Autowired
    private ChargeOrderService chargeOrderService;
    @Autowired
    private SystemPrefService systemPrefService;
    @Autowired
    private CatchHistoryService catchHistoryService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private OemMapper oemMapper;

    @Override
    public void enterDoll(Integer dollId, Integer memberId) {

        //如果用户已在房间则直接设置用户房间key的value
        //lcc  如果不在 房间就添加 用户到房间
        if (!redisUtil.sIsMember(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId))) {
//            logger.info("在缓存中设置房间包含的玩家集合getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId));
            // 在缓存中设置房间包含的玩家集合
            redisUtil.addSet(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId));
        }
        // 在缓存中设置玩家对应的房间id
//        logger.info("在缓存中设置玩家对应的房间id入参 getMemberRoomKey:{},dollId:{}", RedisKeyGenerator.getMemberRoomKey(memberId), String.valueOf(dollId));
        //如果玩家还在其他房间则清除其他房间key下的set
        if (redisUtil.existsKey(RedisKeyGenerator.getMemberRoomKey(memberId))) {
            int otherDollId = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(memberId)));
            if (otherDollId != dollId) {
                redisUtil.remSet(RedisKeyGenerator.getRoomKey(otherDollId), String.valueOf(memberId));
            }
        }
        //如果 房主存在  查看 房主状态是否正常
        if (redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) && "游戏中".equals(RedisKeyGenerator.getRoomStatusKey(dollId))) {
            String roomMemberId = redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId));
            if (redisUtil.existsKey(RedisKeyGenerator.getMemberRoomKey(Integer.parseInt(roomMemberId)))) {//房主 缓存key存在
                String roomMemberDId = redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(Integer.parseInt(roomMemberId)));
                if (!String.valueOf(dollId).equals(roomMemberDId)) {//错误的房主信息
                    // redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
                    //关闭掉 卡住的房主
                    dollRoomService.endPlay(dollId, Integer.parseInt(roomMemberId));
                }
            }
            // else {//过时的房主信息
            //关闭掉 卡住的房主
            //redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
            // dollRoomService.endPlay(dollId,Integer.parseInt(roomMemberId));
            // }
        }
        //更新用户所在房间
        redisUtil.setString(RedisKeyGenerator.getMemberRoomKey(memberId), String.valueOf(dollId));
    }

    @Override
    public void exitDoll(Integer memberId) {
        Integer dollId = 0;
        //logger.info("检查给定 key 是否存在入参getMemberRoomKey:{}" + RedisKeyGenerator.getMemberRoomKey(memberId));
        if (redisUtil.existsKey(RedisKeyGenerator.getMemberRoomKey(memberId))) {
            //logger.info("获取dollId入参:{}" + redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(memberId)));
            dollId = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(memberId)));
        }
        //如果当前玩家还在楼主状态
        if (redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) && memberId != null &&
                String.valueOf(memberId).equals(redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)))) {
            if (dollRoomService.endPlay(dollId, memberId)) {
                //logger.info("退房的时候同时从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
                redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(memberId));
                //logger.info("退房的时候同时删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(memberId));
                redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(memberId));
            }
        }
        //logger.info("退房传入参数memberId=" + memberId);
        // Integer getMessege = dollRoomService.outDollRoom(memberId,doll);
        //logger.info("判断 元素是否是集合 key的成员入参getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId),String.valueOf(memberId));
        if (redisUtil.sIsMember(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId))) {
            // 在缓存房间集合中移除玩家记录
            //logger.info("在缓存房间集合中移除玩家记录入参getRoomKey:{},memberId:{}" + RedisKeyGenerator.getRoomKey(dollId),String.valueOf(memberId));
            redisUtil.remSet(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId));
            // 在缓存中删除玩家退出房间的标记
            redisUtil.delKey(RedisKeyGenerator.getMemberRoomKey(memberId));
            //logger.info("清除玩家房间标记：" + RedisKeyGenerator.getMemberRoomKey(memberId));
        }
    }

    /**
     * 获取房间在线人数
     * 房间id
     */
    @Override
    public Long onLineCount(Integer dollId) {
        return redisUtil.getSCard(RedisKeyGenerator.getRoomKey(dollId));
    }

    @Override
    public Integer takeMemberRoomId(Integer userId) {
        String dollId = redisUtil.getString(RedisKeyGenerator.getMemberQueueRoomKey(userId));
        if (dollId == null || "".equals(dollId)) {
            return 0;
        }
        return Integer.parseInt(dollId);
    }

    @Override
    public List<String> takeRoomMembers(Integer dollId, Integer count) {
        return redisUtil.srandMember(RedisKeyGenerator.getRoomKey(dollId), count);
    }

    @Override
    public String takeRoomHostMember(Integer dollId) {
        return redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId));
    }

    @Override
    public Integer takeToken2Member(String token) {
        String userId = redisUtil.getString(token);
        if (StringUtils.isEmpty(userId)) {
            return 0;
        }
        return Integer.parseInt(userId);
    }

    @Override
    public void addRoomQueue(Integer dollId, Integer userId) {
        logger.info("添加memberId到集合入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId),
                String.valueOf(userId));
        redisUtil.addListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(userId));
        logger.info("设置用户排队房间入参getMemberQueueRoomKey:{},memberId:{}",
                RedisKeyGenerator.getMemberQueueRoomKey(userId), String.valueOf(userId));
        redisUtil.setString(RedisKeyGenerator.getMemberQueueRoomKey(userId), String.valueOf(dollId));
    }

    /**
     * 获取当前房间人员列表
     */
    @Override
    public List<String> takeRoomQueue(Integer dollId) {
        return redisUtil.getList(RedisKeyGenerator.getRoomQueueKey(dollId));
    }

    @Override
    public void leaveRoomQueue(Integer dollId, Integer userId) {
        logger.info("从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId),
                String.valueOf(userId));
        redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(userId));
        logger.info("删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(userId));
        redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(userId));
    }

    @Override
    public GameStatusEnum checkPlaying(Integer dollId, Integer userId) {

        Account account = accountService.select(userId);
        Doll doll = dollService.selectByPrimaryKey(dollId);
        //检测游戏币不足
//        if (doll.getMachineType() != 2) {
            if (doll.getPrice() > account.getCoins()) {
                return GameStatusEnum.GAME_PRICE_NOT_ENOUGH;
            }
//        } else {
//            if (doll.getPrice() > account.getSuperTicket()) {
//                return GameStatusEnum.GAME_PRICE_NOT_SuperTicket;
//            }
//        }
        //数据库状态 空闲  但是 缓存为  维修中  同步数据库空闲状态
        if ("空闲中".equals(doll.getMachineStatus()) && "维修中".equals(takeRoomState(dollId))) {
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "空闲中");
        }
        //数据库状态 维修中 改为维修中
        //数据库中的状态如果是维修中或者未上线
        //缓存中设置为维修中
        //doll对象状态设置为维修中
        //删除缓存中房主
        //返回检测结果维修中
        if ("维修中".equals(doll.getMachineStatus()) || "维护中".equals(doll.getMachineStatus()) || "未上线".equals(doll.getMachineStatus())) {
            logger.info("机器维护中，无法开始游戏");
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "维修中");
            doll.setMachineStatus("维修中");
            redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));//删除房主
            return GameStatusEnum.GAME_MAINTAINED;
        }

        //缓存为维修中
        //返回检测结果维修中
        if ("维修中".equals(takeRoomState(dollId))) {//缓存状态为weixiu
            logger.info("机器维护中，无法开始游戏");
            return GameStatusEnum.GAME_MAINTAINED;
        }

        //检测房间是否有房主
        //返回检测结果游戏中
        //if (dollRoomService.checkPlaying(dollId, userId) > 0) {
        //     logger.info("玩家" + userId + "在娃娃机" + dollId + "的房间有人已经开始游戏，无法开始游戏");
        //     return GameStatusEnum.GAME_PLAYING;
        // }
        //如果已经有人在玩
        String roomHostKey = RedisKeyGenerator.getRoomHostKey(dollId);
        if (redisUtil.existsKey(roomHostKey) && !redisUtil.getString(roomHostKey).equals(String.valueOf(userId))) {
            logger.info("玩家" + userId + "在娃娃机" + dollId + "的房间有人已经开始游戏，无法开始游戏");
            return GameStatusEnum.GAME_PLAYING;
        }
        return GameStatusEnum.GAME_GO;
    }

    @Override
    public GameStatusEnum startPlay(Integer dollId, Integer userId) {

        GameStatusEnum result = checkPlaying(dollId, userId);
        if (result.getCode() == GameStatusEnum.GAME_GO.getCode()) {//检查通过
            //开始游戏前 清除房间 过时状态
            //roomCleanState(dollId,userId);
            if (dollRoomService.startPlay(dollId, userId)) {
                //if (dollRoomService.startPlay(dollId, member)) {
                //设置用户当前下抓id
//                logger.info("玩家" + userId + "在娃娃机" + dollId + "的游戏开始成功");
                return GameStatusEnum.GAME_START_SUCCESS;
            } else {
                logger.info("玩家" + userId + "在娃娃机" + dollId + "的游戏开始失败");
                return GameStatusEnum.GAME_START_FAIL;
            }
        } else {
            return result;//检查不通过
        }
    }

    @Override
    public void roomCleanState(Integer dollId, Integer userId) {
        //如果 房主存在  查看 房主状态是否正常
        if ("游戏中".equals(RedisKeyGenerator.getRoomStatusKey(dollId))) {
            if (redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId))) {//房主存在  但是卡房间了
                String roomMemberId = redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId));
                if (redisUtil.existsKey(RedisKeyGenerator.getMemberRoomKey(Integer.parseInt(roomMemberId)))) {//房主 缓存key存在
                    String roomMemberDId = redisUtil.getString(RedisKeyGenerator.getMemberRoomKey(Integer.parseInt(roomMemberId)));
                    if (!roomMemberDId.equals(String.valueOf(dollId))) {//错误的房主信息
                        // redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
                        //关闭掉 卡住的房主
                        dollRoomService.endPlay(dollId, Integer.parseInt(roomMemberId));
                    }
                } else {//过时的房主信息
                    //关闭掉 卡住的房主
                    //redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
                    dollRoomService.endPlay(dollId, Integer.parseInt(roomMemberId));
                }
            }
        }
    }

    @Override
    public GameStatusEnum consumePlay(Integer dollId, Integer userId, String state) {
        /**
         * 下抓计数
         */
        if (consume(dollId, userId, state)) {
            return GameStatusEnum.GAME_CONSUME_SUCCESS;
        } else {
            return GameStatusEnum.GAME_CONSUME_FAIL;
        }

    }

    private boolean consume(Integer dollId, Integer userId, String state) {
        /*
         * 扣费代码 逻辑优化
        Integer num = 0;
        if (redisUtil.existsKey(RedisKeyGenerator.getMemberClaw(userId))) {
            num = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberClaw(userId))) + 1;
            redisUtil.setString(RedisKeyGenerator.getMemberClaw(userId), String.valueOf(num), 60 * 5);
        } else {
            redisUtil.setString(RedisKeyGenerator.getMemberClaw(userId), "1", 60 * 5);
        }*/
        Member member = memberService.selectById(userId);
        Doll doll = dollService.selectByPrimaryKey(dollId);
        if (doll.getPrice() > member.getCoins()) {
            return false;
        }
        return dollRoomService.consumePlay(doll, member, state);//扣费
    }

    @Override
    public GameStatusEnum endRound(Integer dollId, Integer userId, Integer gotDoll, String state) {

        if (serviceFacade.endRound(dollId, userId, gotDoll, state)) {
            return GameStatusEnum.GAME_END_ROUND_SUCCESS;
        } else {
            return GameStatusEnum.GAME_END_FAIL;
        }
    }

    @Override
    public GameStatusEnum endRound(Integer dollId, Integer userId, Integer gotDoll, String gameNum, String state) {
        /*
         * 游戏扣费流程优化   游戏上机成功后  会补发下抓   未上机成功的 不会扣费 扣费标准以 机器返回抓取成功结果为准
        String claw = redisUtil.getString(RedisKeyGenerator.getGameResult(dollId));
        if ("1".equals(claw)) {
            logger.info("++++++++++下过抓子");
        } else {
            logger.info("++++++++++未下抓子");
            gotDoll = 0;//没下爪子抓中不算
        }
        */
        /*
        Integer catchDoll =Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getUserGameCatch(userId))) ;
        String ServerGameNum = this.takeGameNum(dollId);
        if(!ServerGameNum.equals(gameNum)) {
        	 logger.info("++++++++++游戏编号异常:"+gameNum);
        	return GameStatusEnum.GAME_END_FAIL;
        }
        logger.info("++++++++++服务端抓取结果"+catchDoll);
        //正常结束标志
        redisUtil.setString(RedisKeyGenerator.getGameEndState(dollId), "1");
        */
        //按照局数编号查看抓中结果
        Integer catchDoll = GameProcessUtil.getInstance().getGameCatchDoll(gameNum);
        //异常结束  不算抓中
        if ("1".equals(state)) {
            catchDoll = 0;
        }
        if (serviceFacade.endRound(dollId, userId, catchDoll, gameNum, state)) {
            return GameStatusEnum.GAME_END_ROUND_SUCCESS;
        } else {
            return GameStatusEnum.GAME_END_FAIL;
        }
    }

    @Override
    public GameStatusEnum end(Integer dollId, Integer userId) {
        /*
        String endStateStr = redisUtil.getString(RedisKeyGenerator.getGameEndState(dollId))==null?"0":redisUtil.getString(RedisKeyGenerator.getGameEndState(dollId));
    	Integer endState = Integer.parseInt(endStateStr);
    	String catchDollStr = redisUtil.getString(RedisKeyGenerator.getUserGameCatch(userId))==null?"0":redisUtil.getString(RedisKeyGenerator.getUserGameCatch(userId));
    	Integer catchDoll =Integer.parseInt(catchDollStr) ;
    	String gameNum = this.takeGameNum(dollId);
    	if (endState==0) {//游戏未正常结束
    		serviceFacade.endPlay(dollId, userId, catchDoll, gameNum);//结束游戏
    		logger.info("++++++++++服务端抓取结果,异常结束补填记录:"+catchDoll);
    	}*/
        if (!redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) ||
                String.valueOf(userId).equals(redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)))) {
            if (serviceFacade.endPlay(dollId, userId)) {
                //logger.info("从队列中移除memberId入参getRoomQueueKey:{},memberId:{}", RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(userId));
                redisUtil.remListQueue(RedisKeyGenerator.getRoomQueueKey(dollId), String.valueOf(userId));
                //logger.info("删除用户排队房间入参getMemberQueueRoomKey:{}", RedisKeyGenerator.getMemberQueueRoomKey(userId));
                redisUtil.delKey(RedisKeyGenerator.getMemberQueueRoomKey(userId));

            } else {
                return GameStatusEnum.GAME_END_FAIL;

            }
        } else {
            logger.info("用户:{}不是娃娃机:{}的楼主，不做结束游戏的更新操作！", userId, dollId);

        }
        return GameStatusEnum.GAME_END;
    }

    @Override
    public void addMemberInfoPojo(Integer memberId, MemberInfoPojo memberInfoPojo) {
        String memberInfoKey = RedisKeyGenerator.getMemberInfoKey(memberId);
        redisUtil.addHashSet(memberInfoKey, "name", memberInfoPojo.getName());
        redisUtil.addHashSet(memberInfoKey, "gender", memberInfoPojo.getGender());
        redisUtil.addHashSet(memberInfoKey, "iconRealPath", memberInfoPojo.getIconRealPath());
        redisUtil.addHashSet(memberInfoKey, "memberID", memberInfoPojo.getMemberID());
        redisUtil.addHashSet(memberInfoKey, "vip", memberInfoPojo.getLevel());
    }

    @Override
    public boolean existsMemberInfoKey(Integer userId) {
        String memberInfoKey = RedisKeyGenerator.getMemberInfoKey(userId);
        return redisUtil.existsKey(memberInfoKey);
    }

    @Override
    public MemberInfoPojo takeMemberInfoPojo(Integer userId) {
        MemberInfoPojo memberInfoPojo = new MemberInfoPojo();
        String memberInfoKey = RedisKeyGenerator.getMemberInfoKey(userId);
        memberInfoPojo.setId(userId);
        memberInfoPojo.setName(redisUtil.getHashSet(memberInfoKey, "name"));
        memberInfoPojo.setGender(redisUtil.getHashSet(memberInfoKey, "gender"));
        memberInfoPojo.setIconRealPath(redisUtil.getHashSet(memberInfoKey, "iconRealPath"));
        memberInfoPojo.setMemberID(redisUtil.getHashSet(memberInfoKey, "memberID"));
        memberInfoPojo.setLevel(redisUtil.getHashSet(memberInfoKey, "level"));
        return memberInfoPojo;
    }

    /**
     * 获取占卜图片URL
     *
     * @param dollId
     * @param memberId
     * @return
     */
    @Override
    public String getDivinationUrl(Integer dollId, Integer memberId) {
        //根据dollId查询divination主题
        DivinationTopic divinationTopic = divinationService.getByDollId(dollId);
        //随机分享结果
        DivinationImage divination = divinationService.divination(divinationTopic.getId());
        return divination.getDivinationTopicImg();
    }


    /**
     * 生成分享图片
     *
     * @param dollId
     * @param memberId
     * @return
     */
    @Override
    public Map<String, Object> getShareUrl(Integer dollId, Integer memberId, Integer version) {
        //根据dollId查询divination主题
        DivinationTopic divinationTopic = divinationService.getByDollId(dollId);
        //先查询redis
        String divinationUrl = redisUtil.getString(RedisKeyGenerator.getdivinationKey(divinationTopic.getId(), memberId));
        String shareUrl = redisUtil.getString(RedisKeyGenerator.getShareImgKey(divinationTopic.getId(), memberId));
        Map<String, Object> map = new HashMap<>();
        //如果redis中有就从redis中查询
        if (StringUtils.isNotEmpty(divinationUrl) && StringUtils.isNotEmpty(shareUrl)) {
            map.put("divinationUrl", divinationUrl);
            map.put("shareUrl", shareUrl);
            logger.info("缓存取出图片" + shareUrl);
            return map;
        }
        //如果没有就拼接一张并上传阿里云
        //获取模板
        ImageHandleHelper imageHandleHelper = new ImageHandleHelper();
        //拼接
        Member member = memberService.selectById(memberId);
        divinationUrl = getDivinationUrl(dollId, memberId);
        SystemPref SHARE_QR_CARD;
        if (version == 2) {
            SHARE_QR_CARD = systemPrefService.selectByPrimaryKey("SHARE_QR_CARD_XYJ");
            divinationTopic.setModeUrl(systemPrefService.selectByPrimaryKey("MODE_URL_" + divinationTopic.getId()).getValue());
        } else {
            SHARE_QR_CARD = systemPrefService.selectByPrimaryKey("SHARE_QR_CARD");

        }
        String qrUrl;
        if (SHARE_QR_CARD != null && StringUtils.isNotEmpty(SHARE_QR_CARD.getValue())) {
            qrUrl = SHARE_QR_CARD.getValue();
        } else {
            qrUrl = "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/20180604144534.png";
        }
        try {
            shareUrl = imageHandleHelper.getImgUrl(divinationTopic, divinationUrl, member, qrUrl);
            //缓存地址到redis
            redisUtil.setString(RedisKeyGenerator.getShareImgKey(divinationTopic.getId(), memberId), shareUrl, divinationTopic.getWxpireTime());
            redisUtil.setString(RedisKeyGenerator.getdivinationKey(divinationTopic.getId(), memberId), divinationUrl, divinationTopic.getWxpireTime());
            logger.info("重新生成图片" + shareUrl);
            map.put("divinationUrl", divinationUrl);
            map.put("shareUrl", shareUrl);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public String getShareUrl(Integer memberId, Integer version) {
        //先查询redis
        String shareUrl = redisUtil.getString(RedisKeyGenerator.getShareUrlKey(memberId) + QRCodeUtil.shareIMGversion + version);
        //如果redis中有就从redis中查询
        if (StringUtils.isNotEmpty(shareUrl)) {
            return shareUrl;
        }
        ImageHandleHelper imageHandleHelper = new ImageHandleHelper();
        //拼接
        Member member = memberService.selectById(memberId);
        SystemPref SHARE_QR_CARD;
        if (version == null) {
            SHARE_QR_CARD = systemPrefService.selectByPrimaryKey("SHARE_QR_CARD");
        } else if (version == 2) {
            SHARE_QR_CARD = systemPrefService.selectByPrimaryKey("SHARE_QR_CARD_XYJ");
        } else {
            SHARE_QR_CARD = systemPrefService.selectByPrimaryKey("SHARE_QR_CARD");
        }
        String qrUrl;
        if (SHARE_QR_CARD != null && StringUtils.isNotEmpty(SHARE_QR_CARD.getValue())) {
            qrUrl = SHARE_QR_CARD.getValue();
        } else {
            qrUrl = "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/20180604144534.png";
        }
        try {
            shareUrl = imageHandleHelper.getshareUrl(member, qrUrl, version);
            //缓存地址到redis
            redisUtil.setString(RedisKeyGenerator.getShareUrlKey(memberId) + QRCodeUtil.shareIMGversion + version, shareUrl, 2147483647
            );
            logger.info("重新生成图片" + shareUrl);
            return shareUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shareUrl;
    }


    @Override
    public Integer chackShare(Integer memberId, String gameNum) {
        //查询redis中是否存在该游戏编码
        String shareToken = redisUtil.getString(RedisKeyGenerator.getShareKey(memberId, gameNum));
        if (StringUtils.isEmpty(shareToken)) {
            return 2;
        }
        //如果有就得币
        //然后删除该游戏编码
        redisUtil.delKey(RedisKeyGenerator.getShareKey(memberId, gameNum));
        return 1;
    }

    @Override
    public Map<String, Object> Share(Integer memberId) {
        Account account = accountService.select(memberId);
        Charge charge = new Charge();
        charge.setCoins(account.getCoins());
        MathUtil mathUtil = new MathUtil();
        SystemPref MIN_SHARE_COIN = systemPrefService.selectByPrimaryKey("MIN_SHARE_COIN");
        if (MIN_SHARE_COIN == null) {
            MIN_SHARE_COIN = new SystemPref();
            MIN_SHARE_COIN.setValue("5");
        }
        SystemPref MAX_SHARE_COIN = systemPrefService.selectByPrimaryKey("MAX_SHARE_COIN");
        if (MAX_SHARE_COIN == null) {
            MAX_SHARE_COIN = new SystemPref();
            MAX_SHARE_COIN.setValue("60");
        }
        SystemPref AVERAGE_SHARE_COIN = systemPrefService.selectByPrimaryKey("AVERAGE_SHARE_COIN");
        if (AVERAGE_SHARE_COIN == null) {
            AVERAGE_SHARE_COIN = new SystemPref();
            AVERAGE_SHARE_COIN.setValue("10");
        }
        int share = mathUtil.getShare(Double.valueOf(MIN_SHARE_COIN.getValue()), Double.valueOf(MAX_SHARE_COIN.getValue()), Double.valueOf(AVERAGE_SHARE_COIN.getValue()));
        charge.setCoinsSum(share);
        charge.setChargeMethod("分享成功,奖励游戏币数量 " + share);
        charge.setMemberId(memberId);
        charge.setType("income");
        charge.setChargeDate(TimeUtil.getTime());
        //保存流水
        chargeOrderService.insertChargeHistory(charge);
        Map<String, Object> map = new HashMap<>();
        map.put("coins", account.getCoins() + share);
        map.put("shareCoins", share);
        return map;
    }


    @Override
    public Map<String, String> getQRCodeImgUrl(Integer memberId, Integer version, String index) {
        Member member = memberService.selectById(memberId);
        Map<String, String> map = new HashMap<>();
        if (member == null) {
            map.put("message", "查询不到该用户");
            return map;
        }
        Oem oem = oemMapper.selectByCode(member.getRegisterChannel());
        if(oem == null){
            oem = oemMapper.selectByCode("lanaokj");
        }
        String shareUrl = QRCodeUtil.getshareUrl(member.getMemberID(), member.getRegisterChannel(), index,oem);
        //先查询redis
//        String shareImgUrl = redisUtil.getString(shareUrl + QRCodeUtil.shareIMGversion);
//        //如果redis中有就从redis中查询
//        if (StringUtils.isNotEmpty(shareImgUrl)) {
            map.put("shareImgUrl", null);
            map.put("shareUrl", shareUrl);
            return map;
//        }
//        ImageHandleHelper imageHandleHelper = new ImageHandleHelper();
//        //拼接
//        String qrUrl = QRCodeUtil.getQrUrl(member, null, index,oem);
//        if (StringUtils.isEmpty(qrUrl)) {
//            qrUrl = "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/20180604144534.png";
//        }
//        try {
//            shareImgUrl = imageHandleHelper.getshareUrl(member, qrUrl, version);
//            //缓存地址到redis
//            redisUtil.setString(shareUrl + QRCodeUtil.shareIMGversion, shareImgUrl, 2147483647);
//            map.put("shareImgUrl", shareImgUrl);
//            map.put("shareUrl", shareUrl);
//            return map;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return map;
    }

    @Override
    public Map<String, String> getAgentImgUrl(Integer agentId, Integer version, String index) {
        Agent agent = agentService.selectByPrimaryKey(agentId);
        Map<String, String> map = new HashMap<>();
        if (agent == null) {
            map.put("message", "查询不到该代理");
            return map;
        }
        //贴牌
        String channel = "lanaokj";
        Oem oem = null;
        if(agent.getLevel() == 0){//为贴牌商
            if(agent.getIsOem()){
                oem  = oemMapper.selectByPrimaryKey(agent.getId());
                if(oem != null) channel = oem.getCode();
            }
        }else if(agent.getLevel() != 0){
            Agent superAgent = agentService.selectByPrimaryKey(agent.getAgentId());
            if(superAgent.getIsOem()){
                oem = oemMapper.selectByPrimaryKey(superAgent.getId());
                if(oem != null) channel = oem.getCode();
            }
        }
        if(oem == null){
            oem = oemMapper.selectByCode(channel);
        }
        String shareUrl = QRCodeUtil.getshareUrl("agent" + agent.getId(), channel, index,oem);
        //先查询redis
        String shareImgUrl = redisUtil.getString(shareUrl + QRCodeUtil.shareIMGversion);
        //如果redis中有就从redis中查询
        if (StringUtils.isNotEmpty(shareImgUrl)) {
            map.put("shareImgUrl", shareImgUrl);
            map.put("shareUrl", shareUrl);
            return map;
        }
        //拼接
        String qrUrl = QRCodeUtil.getAgentUrl(agent, null, index,oem);
        if (StringUtils.isEmpty(qrUrl)) {
            qrUrl = "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/20180604144534.png";
        }
        try {
            //缓存地址到redis
            redisUtil.setString(shareUrl + QRCodeUtil.shareIMGversion, qrUrl, 2147483647);
            map.put("shareImgUrl", qrUrl);
            map.put("shareUrl", shareUrl);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Integer gameResult(Integer memberId, Integer dollId) {
        return GameProcessUtil.getInstance().getGameCatchDoll(catchHistoryService.getRecenGameNum(memberId, dollId));
    }

    @Override
    public String takeRoomState(Integer dollId) {
        return redisUtil.getString(RedisKeyGenerator.getRoomStatusKey(dollId));
    }

    @Override
    public String takeGameNum(Integer userId, Integer dollId) {
        return redisUtil.getString(RedisKeyGenerator.getRoomGameNumKey(userId, dollId));
    }
}