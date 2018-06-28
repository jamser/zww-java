package com.bfei.icrane.game;

import java.util.Random;

import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.service.DollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bfei.icrane.api.controller.WebSocketController;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 游戏过程  状态管理
 */
public class GameProcessUtil {

    private static final Logger logger = LoggerFactory.getLogger(GameProcessUtil.class);

    private static final Integer CACHE_TIME = 3 * 60;

    private static final Integer USER_STRONG = 60 * 60 * 24;//1天 保留

    private volatile static GameProcessUtil _instance;

    private RedisUtil redisUtil = new RedisUtil();

    public static GameProcessUtil getInstance() {
        synchronized (GameProcessUtil.class) {
            if (_instance == null) {
                return (new GameProcessUtil());
            }
        }
        return _instance;
    }

    @Autowired
    private DollService dollService;


    /**
     * 是否补发   当前游戏编号存在    当前命令次数为 0
     *
     * @param userId
     * @return
     */
    public boolean hasGameLock(Integer userId, Integer dollId, GameProcessEnum keyType) {
        boolean flag = false;
        if (hasGameNum(userId, dollId)) {
            Integer num = 0;
            num = countGameLock(userId, dollId, keyType);
            if (num == 0) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 获取 对应业务 redis key
     *
     * @param dollId
     * @param keyType
     * @return
     */
    public String getKey(Integer userId, Integer dollId, GameProcessEnum keyType) {
        String key = "";
        switch (keyType) {
            case GAME_CLAW://下抓计数
                key = RedisKeyGenerator.getGameClaw(getGameNum(userId, dollId));
                break;
            case GAME_CONSUME://扣费计数
                key = RedisKeyGenerator.getGameConsume(getGameNum(userId, dollId));
                break;
            case GAME_SETTER://下单计数
                key = RedisKeyGenerator.getGameSetter(getGameNum(userId, dollId));
                break;
            case GAME_HISTORY://游戏记录计数
                key = RedisKeyGenerator.getGameCatchHistory(getGameNum(userId, dollId));
                break;
            case GAME_CHARGE_HISTORY://
                key = RedisKeyGenerator.getGameChargeHistory(getGameNum(userId, dollId));
                break;
            case GAME_CATCH://抓中结果计数
                key = RedisKeyGenerator.getGameCatch(getGameNum(userId, dollId));
                break;
            case GAME_END://游戏结束计数
                key = RedisKeyGenerator.getGameEndState(getGameNum(userId, dollId));
                break;
            case GAME_IDLE://机器空闲计数
                key = RedisKeyGenerator.getGameIDLE(dollId);
                break;
            case GAME_COIN://游戏结束计数
                key = RedisKeyGenerator.getGameCoin(getGameNum(userId, dollId));
                break;
            case GAME_READY://机器ready计数
                key = RedisKeyGenerator.getGameREADY(dollId);
                break;
            case GAME_CLAW2://回复下抓
                key = RedisKeyGenerator.getGameClaw2(getGameNum(userId, dollId));
                break;
            default:
                break;
        }
        return key;
    }

    /**
     * 获取 计数
     *
     * @param userId
     * @return
     */
    public Integer countGameLock(Integer userId, Integer dollId, GameProcessEnum keyType) {
        String key = getKey(userId, dollId, keyType);
        String result = redisUtil.getString(key);
        return result == null ? null : Integer.parseInt(result);
    }

    /**
     * 初始化 业务计数器
     *
     * @param dollId
     * @param keyType
     */
    public void initCountGameLock(Integer userId, Integer dollId, GameProcessEnum keyType) {
        Integer num = 0;
        String key = getKey(userId, dollId, keyType);
        redisUtil.setString(key, String.valueOf(num), CACHE_TIME);
    }

    /**
     * 次数加 1  并返回加之后的计数
     */
    public Integer addCountGameLock(Integer userId, Integer dollId, GameProcessEnum keyType) {
        Integer num = 0;
        String key = getKey(userId, dollId, keyType);
        // logger.info("当前使用key:"+key);
        if (redisUtil.existsKey(key)) {
            num = Integer.parseInt(redisUtil.getString(key)) + 1;
            redisUtil.setString(key, String.valueOf(num), CACHE_TIME);
            //logger.info("使用key自增:"+key+"num="+num);
        } else {
            num = 1;
            redisUtil.setString(key, String.valueOf(num), CACHE_TIME);
        }
        if (keyType.GAME_CONSUME.equals(keyType)) {
            logger.info("当前使用key:" + key + ",num=" + num + ",userId=" + userId + ",dollId=" + dollId);
        }
        return num;
    }

    /**
     * 站机
     *
     * @param userId
     * @param dollId
     * @return
     */
    public boolean standMachine(String userId, Integer dollId) {
        String roomHostKey = RedisKeyGenerator.getRoomHostKey(dollId);
        if (redisUtil.existsKey(roomHostKey) && !redisUtil.getString(roomHostKey).equals(userId)) {
            logger.info("玩家" + redisUtil.getString(roomHostKey) + "已抢到机器");
            return true;
        }
        return false;
    }

    /**
     * 开始游戏
     *
     * @param userId
     * @param dollId
     */
    public void onOpen(String userId, Integer dollId) {
        String roomHostKey = RedisKeyGenerator.getRoomHostKey(dollId);
        //有效token
        redisUtil.setString(roomHostKey, userId, 60 * 5);
        //redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId), 60 * 5);
        redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "游戏中");
        // redisUtil.setString(RedisKeyGenerator.getGameResult(dollId), "0", 60 * 5);//游戏结果
        // redisUtil.setString(RedisKeyGenerator.getUserGameCatch(Integer.parseInt(userId)), "0", 60*2 );//游戏结果
        logger.info("将玩家" + userId + "设到缓存中的楼主key");
        //初始化 空闲 机器空闲状态
        initCountGameLock(Integer.parseInt(userId), dollId, GameProcessEnum.GAME_IDLE);//游戏空闲指令计数0
        //logger.info("初始化" + dollId + "IDLE指令计数0");
    }

    /**
     * 机器是否能投币
     *
     * @param dollId
     * @return
     */
    public boolean canPlay(Integer dollId) {
        //return redisUtil.getString(RedisKeyGenerator.getRoomStatusKey(dollId));
        String roomStatus = redisUtil.getString(RedisKeyGenerator.getRoomStatusKey(dollId));
        if ("维修中".equals(roomStatus)) {
            //webSocketMap.get(dollId).close();//清除状态
            //redisUtil.setString(RedisKeyGenerator.getGameResult(dollId), "-1", 60 * 5);//游戏结果
            return false;
        }
        return true;
    }

    /**
     * 投币状态
     *
     * @param userId
     * @param dollId
     */
    public synchronized boolean onCoin(Integer userId, Integer dollId) {
        Integer num = countGameLock(userId, dollId, GameProcessEnum.GAME_COIN);
        if (num == 0 || num == null) {//有ready返回后不收coin
            addCountGameLock(userId, dollId, GameProcessEnum.GAME_COIN);
            //redisUtil.setString(RedisKeyGenerator.getUserGameCatch(userId), "0", 60 * 2 );//抓中初始化
            redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(userId), 60 * 5);
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "游戏中");
            //redisUtil.setString(RedisKeyGenerator.getMemberClaw(userId), "0", 60 * 5);//两分钟
            //redisUtil.setString(RedisKeyGenerator.getMemberSetter(userId), "0", 60 * 5);//未结算计数
            //redisUtil.setString(RedisKeyGenerator.getGameResult(dollId), "0", 60 * 5);//游戏结果
            //redisUtil.setString(RedisKeyGenerator.getGameCatchHistory(dollId), "0", 60 * 5);//游戏记录保存次数
            // redisUtil.setString(RedisKeyGenerator.getGameEndState(dollId), "0");//未结束
            redisUtil.delKey(RedisKeyGenerator.getRoomGameNumKey(userId, dollId));
            /*机器空闲后等待 初始化 机器就绪指令  等待就绪指令发送*/
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_READY);//游戏就绪指令计数0
            logger.info("用户ID" + userId + "向" + dollId + "投币");
            return true;
        }

        return false;
    }


    /**
     * 游戏编号 是否存在
     *
     * @param dollId
     * @return
     */
    public boolean hasGameNum(Integer userId, Integer dollId) {
        String gameNum = getGameNum(userId, dollId);
        if (!"".equals(gameNum)) {
            return true;
        }
        return false;
    }

    /**
     * 获取游戏编号
     *
     * @param dollId
     */
    public String getGameNum(Integer userId, Integer dollId) {
        String gameNum = "";
        if (redisUtil.existsKey(RedisKeyGenerator.getRoomGameNumKey(userId, dollId))) {
            gameNum = redisUtil.getString(RedisKeyGenerator.getRoomGameNumKey(userId, dollId));
        }
        return gameNum;
    }

    /**
     * 抓中娃娃结果
     *
     * @param gameNum
     * @return
     */
    public Integer getGameCatchDoll(String gameNum) {
        String key = RedisKeyGenerator.getGameCatch(gameNum);
        Integer catchDoll = 0;
        if (redisUtil.existsKey(key)) {
            catchDoll = Integer.parseInt(redisUtil.getString(key));
        }
        return catchDoll;
    }

    /**
     * 下抓
     *
     * @param userId
     * @param dollId
     */
    public void onClaw(Integer userId, Integer dollId) {
        //redisUtil.setString(RedisKeyGenerator.getGameResult(dollId), "1", 60 * 5);//下过抓指令
        addCountGameLock(userId, dollId, GameProcessEnum.GAME_CLAW);//下抓计数
    }

    private Integer getRedisValue(String key, Integer defaultNum) {
        if (redisUtil.existsKey(key)) {
            return Integer.parseInt(redisUtil.getString(key));
        }
        return defaultNum;
    }

    /**
     * 获取强弱抓 概率
     * 强弱抓概率 控制     1小时内 最多连续2次抢抓
     */
    public String contrClaw(Integer userId, Integer dollId) {
        String message = "weakClaw";
        String baseNumKey = RedisKeyGenerator.getMachineBaseNum(dollId);
        Integer baseNum = 10;
        baseNum = getRedisValue(baseNumKey, baseNum);
//        String p1Key = RedisKeyGenerator.getMachineP1(dollId);
//        Integer p1 = 15;
//        p1 = getRedisValue(p1Key, p1);

//        String chargeKey = RedisKeyGenerator.getMachineCharge(dollId);
        Integer chargeSum = 0;
//        chargeSum = getRedisValue(chargeKey, chargeSum);//当前房主充值金额

//        String memberNewKey = RedisKeyGenerator.getMemberNew(dollId);
//        Integer memberNew = 0;
//        memberNew = getRedisValue(memberNewKey, memberNew);//当前房主充值金额

        //下抓次数计数
        Integer clawNum = 0;
        String clawNumKey = RedisKeyGenerator.getMemberClawNum(userId, dollId);
        //连续强抓控制
        Integer strongClawNum = 0;
        String strongClawNumKey = RedisKeyGenerator.getMemberStrongClawNum(userId);
        if (redisUtil.existsKey(clawNumKey)) {
            clawNum = Integer.parseInt(redisUtil.getString(clawNumKey));
        } else {//1天
            redisUtil.setString(clawNumKey, "0", USER_STRONG);
        }

        if (redisUtil.existsKey(strongClawNumKey)) {
            strongClawNum = Integer.parseInt(redisUtil.getString(strongClawNumKey));
        } else {
            redisUtil.setString(strongClawNumKey, "0");
        }

        String clawPro = redisUtil.getString(RedisKeyGenerator.getMachineHost(dollId));
        //新用户  未充值  只出现一次强抓
//        logger.info("【contrClaw方法】参数 chargeSum={},memberNew={},userId={},message={},memberNewKey={},strongClawNumKey={},newMemberCatchNum={}", chargeSum, memberNew, userId, message, memberNewKey, strongClawNumKey, newMemberCatchNum);
//        if (chargeSum == 0 && memberNew == 1 && strongClawNum == 0 && newMemberCatchNum <= 2 && redisUtil.existsKey(RedisKeyGenerator.getMemberToyNum(userId))) {//新用户 存在抓中保证
////            Integer machineType = Integer.valueOf(redisUtil.getString(RedisKeyGenerator.getMachineType(dollId)));
////            if (machineType == 2 || machineType == 3) {
////                logger.info("新用户化妆房或数码房第一次弱抓machineType={}", machineType);
////                return "weakClaw";
////            }
//            //默认首次弱抓
//            if (newMemberCatchNum == 0) {
//                return "weakClaw";
//            }
//            Random random = new Random();
//            int i = random.nextInt(2);
//            if (i == 0 && newMemberCatchNum < 2) {
//                return "weakClaw";
//            }
//
//
//            strongClawNum = Integer.parseInt(redisUtil.getString(strongClawNumKey)) + 1;
//            redisUtil.setString(strongClawNumKey, String.valueOf(strongClawNum));
//            logger.info("新用户 存在保证抓中,强抓xxxxxxxclawPro" + clawPro + ", strong:" + clawNum + ",message:strongClaw" + message + ",strongClawNum=" + strongClawNum);
//            //logger.info("只出现一次强抓xxxxxxxclawPro"+clawPro+", strong:"+clawNum+",message:strongClaw"+message+",strongClawNum="+strongClawNum+",本次随机数="+p1Num);
//            return "strongClaw";
//                /*} else {
//                    logger.info("只出现一次强抓xxxxxxxclawPro"+clawPro+", strong:"+clawNum+",message:strongClaw"+message+",strongClawNum="+strongClawNum+",本次随机数="+p1Num);
//	        		return "weakClaw";
//	        	}*/
//        } else {
//            logger.info("已经抓中娃娃后,未充值只出现弱抓");
//        }
          /*  else if (chargeSum==0 && strongClawNum>0) {
             logger.info("只出现一次强抓xxxxxxxclawPro"+clawPro+", strong:"+clawNum+",message:weakClaw"+message+",strongClawNum="+strongClawNum);
	        	message = "weakClaw";
	        } */
        logger.info("【contrClaw方法】参数 chargeSum={},clawPro={}, baseNum={},clawNumKey={},clawNum={},strongClawNumKey={}", chargeSum, clawPro, baseNum, clawNumKey, clawNum, strongClawNumKey);
//        if (chargeSum > 0) {//充钱用户
            Integer range = Integer.parseInt(clawPro);
            if (clawNum <= 0) {//产生随机数
                Random r1 = new Random();
                clawNum = r1.nextInt(range) + baseNum;//在基数情波动范围出现
                logger.info("userId={},dollId={},clawNum={},range={},baseNum={}", userId, dollId, clawNum, range, baseNum);
                redisUtil.setString(clawNumKey, String.valueOf(clawNum), USER_STRONG);
            }

            if (redisUtil.existsKey(clawNumKey)) {//clawNum ==0 局之后 出现强抓
                clawNum = Integer.parseInt(redisUtil.getString(clawNumKey)) - 1;
                logger.info("计数值 userId={},dollId={},clawNum={}", userId, dollId, clawNum);
                if (clawNum == 0 || range == 0) {
                    logger.info("出现强抓userId={},dollId={},clawNum={}", userId, dollId, clawNum);
                    message = "strongClaw";
                } else {
                    message = "weakClaw";
                }
                redisUtil.setString(clawNumKey, String.valueOf(clawNum), USER_STRONG);
                strongClawNum = Integer.parseInt(redisUtil.getString(strongClawNumKey)) + 1;
                redisUtil.setString(strongClawNumKey, String.valueOf(strongClawNum), USER_STRONG);
            } else {//容错处理
                message = "weakClaw";
            }
//        }
        return message;
    }

    /**
     * 游戏结束状态控制
     */
    public void onClose(Integer userId, Integer dollId) {
        redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
        redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "空闲中");
        //redisUtil.delKey(RedisKeyGenerator.getRoomGameNumKey(dollId));
        //redisUtil.setString(RedisKeyGenerator.getGameResult(dollId), "0", 60 * 5);
        //redisUtil.setString(RedisKeyGenerator.getMemberClaw(userId), "0", 60 * 5);//5分钟
        //redisUtil.setString(RedisKeyGenerator.getMemberSetter(userId), "0", 60 * 5);//未结算计数
        //redisUtil.setString(RedisKeyGenerator.getGameEndState(dollId), "0");//重置
        //重置 空闲 可转发空闲命令状态
        initCountGameLock(userId, dollId, GameProcessEnum.GAME_IDLE);//游戏空闲指令计数0
        initCountGameLock(userId, dollId, GameProcessEnum.GAME_READY);//游戏空闲指令计数0
    }
    /****************************************游戏返回指令控制************************************************/
    /**
     * 收到机器空闲指令 并计数 重复指令不转发
     *
     * @param dollId
     * @return
     */
    public boolean getIdle(Integer userId, Integer dollId) {
        Integer num = addCountGameLock(userId, dollId, GameProcessEnum.GAME_IDLE);
        Integer readyNum = countGameLock(userId, dollId, GameProcessEnum.GAME_READY);
        if (WebSocketController.printer) {
            logger.info(userId + "==========向[" + dollId + "]转发机器指令idle:GAME_IDLE" + num + ",readyNum=" + readyNum);
        }
        if (num <= 4 && (readyNum == null || readyNum == 0)) {
            if (WebSocketController.printer) {
                logger.info(userId + "==========向[" + dollId + "]转发机器指令idle:成功转发idle");
            }
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_COIN);//游戏投币指令计数0
            return true;
        }
        return false;
    }

    /**
     * 产生游戏编号   机器返回 ready 指令为基准
     *
     * @param dollId
     */
    public synchronized boolean getReady(Integer userId, Integer dollId) {
        Integer coinNum = countGameLock(userId, dollId, GameProcessEnum.GAME_COIN);
        Integer num = countGameLock(userId, dollId, GameProcessEnum.GAME_READY);
        if (num == 0 && coinNum > 0) {
            addCountGameLock(userId, dollId, GameProcessEnum.GAME_READY);
            String gameNum = StringUtils.getCatchHistoryNum().replace("-", "").substring(0, 20);
            redisUtil.setString(RedisKeyGenerator.getRoomGameNumKey(userId, dollId), gameNum, CACHE_TIME);//设置游戏编号
            //产生游戏编号 后初始化
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_CLAW);//下抓计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_CONSUME);//扣费计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_SETTER);//抓中下单计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_HISTORY);//保存游戏历史计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_CHARGE_HISTORY);//保存游戏消费历史计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_CATCH);//游戏抓中计数0
            initCountGameLock(userId, dollId, GameProcessEnum.GAME_END);//游戏结束计数0
            return true;
        }
        return false;
    }

    /**
     * 发出过下抓指令
     * 获得机器下抓指令
     * 控制机器指令转发次数 防止重复发送
     *
     * @param dollId
     * @return
     */
    public boolean getClaw(Integer userId, Integer dollId) {
        Integer num = addCountGameLock(userId, dollId, GameProcessEnum.GAME_CLAW2);
        if (num > 1) {
            return false;
        }
        //重置 机器空闲状态
        initCountGameLock(userId, dollId, GameProcessEnum.GAME_IDLE);//游戏空闲指令计数0
        initCountGameLock(userId, dollId, GameProcessEnum.GAME_READY);//游戏空闲指令计数0
        return true;
    }

    /**
     * 抓中娃娃
     *
     * @param dollId
     */
    public boolean getCatch(Integer userId, Integer dollId) {
        Integer num = countGameLock(userId, dollId, GameProcessEnum.GAME_CATCH);
        logger.info("抓中娃娃标示 num={}", num);
        if (null == num || num == 0) {
            addCountGameLock(userId, dollId, GameProcessEnum.GAME_CATCH);
            redisUtil.delKey(RedisKeyGenerator.getMemberToyNum(userId));//获得抓中娃娃指令后删除用户抓中保障
            return true;
        }
        return false;
    }


}
