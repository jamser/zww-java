package com.bfei.icrane.api.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.pojos.RechargeRulePojp;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.RechargeRuleService;
import com.bfei.icrane.game.GameProcessEnum;
import com.bfei.icrane.game.GameProcessUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bfei.icrane.api.service.DollRoomService;
import com.bfei.icrane.core.pojos.CatchDollPojo;
import com.bfei.icrane.core.pojos.DollImgPojo;
import org.springframework.util.ObjectUtils;

/**
 * @author lgq Version: 1.0 Date: 2017年9月23日date Description: 用户Service接口实现类.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("DollRoomService")
public class DollRoomServiceImpl implements DollRoomService {
    private static final Logger logger = LoggerFactory.getLogger(DollRoomServiceImpl.class);
    @Autowired
    private DollRoomDao dollRoomDao;
    @Autowired
    private DollDao dollDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private ChargeDao chargeDao;
    @Autowired
    private MachineProbabilityDao machineDao;
    @Autowired
    private CatchHistoryDao catchHistoryDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private RechargeRuleMapper rechargeRuleMapper;

    RedisUtil redisUtil = new RedisUtil();

    @Override
    @Transactional
    public Integer insertDollRoom(DollRoom doolRoom, Doll doll) {
        // TODO Auto-generated method stub
        logger.info("insertDollRoom 参数doolRoom:{},doll:{}", doolRoom, doll);
        Integer resultUp = dollDao.updateByPrimaryKeySelective(doll);
        logger.info("resultUp:{}", resultUp);
        Integer resultIn = dollRoomDao.insertDollRoom(doolRoom);
        logger.info("resultIn:{}", resultIn);
        return resultIn;
    }

    @Override
    public Integer outDollRoom(Integer memberId, Doll doll) {
        // TODO Auto-generated method stub
        logger.info("outDollRoom 参数memberId:{},doll:{}", memberId, doll);
        Integer resultUp = dollDao.updateByPrimaryKeySelective(doll);
        logger.info("resultUp:{}", resultUp);
        Integer result = dollRoomDao.outDollRoom(memberId);
        logger.info("result:{}", result);
        return result;
    }

    @Override
    public DollRoom getDollRoomCount(Integer dollId) {
        // TODO Auto-generated method stub
        logger.info("getDollRoomCount 参数dollId:{}", dollId);
        return dollRoomDao.getDollRoomCount(dollId);
    }

    @Override
    public List<DollRoom> getMemberHead(Integer dollId, int offset, int limit) {
        // TODO Auto-generated method stub
        logger.info("getMemberHead 参数dollId:{},offset:{},limit:{}", dollId, offset, limit);
        List<DollRoom> dr = dollRoomDao.getMemberHead(dollId, offset, limit);
        logger.info("result:{}", dr);
        return dr;
    }

    @Override
    public DollRoom getPlayMember(Integer dollId) {
        // TODO Auto-generated method stub
        logger.info("getPlayMember 参数dollId:{}", dollId);
        DollRoom dr = dollRoomDao.getPlayMember(dollId);
        logger.info("result:{}", dr);
        return dr;
    }

    @Override
    public List<DollImgPojo> getDollImg(Integer dollId) {
        // TODO Auto-generated method stub
//        logger.info("getDollImg 参数dollId:{}", dollId);
        return dollRoomDao.getDollImg(dollId);
    }

    @Override
    public List<CatchDollPojo> getCatchDoll(Integer dollId) {
        // TODO Auto-generated methodF stub
//        logger.info("getCatchDoll 参数dollId:{}", dollId);
        return dollRoomDao.getCatchDoll(dollId);
    }

    @Override
    @Transactional
    public boolean startPlay(Integer dollId, Integer memberId) {
        // TODO Auto-generated method stub
        //dollRoomDao.setPlayFlag(dollId, memberId);
        logger.info("startPlay 参数dollId:{},memberId:{}", dollId, memberId);
        Doll doll = new Doll();
        doll.setId(dollId);
        doll.setMachineStatus("游戏中");
        doll.setModifiedDate(TimeUtil.getTime());
//        doll.setModifiedBy(memberId);
        if (dollDao.updateByPrimaryKeySelective(doll) > 0) {
            redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId), 60 * 5);
            //redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "游戏中");

            //查找机器概率  房主概率
            MachineProbability probability = machineDao.findByDollId(dollId);
            String p1 = "15";
            String p2 = "10";
            String p3 = "5";
            Integer probabi = 100;//默认金额标准
            String baseNum = "10";//默认基数
            if (probability != null) {
                p1 = probability.getProbability1() == null ? p1 : String.valueOf(probability.getProbability1().intValue());
                p2 = probability.getProbability2() == null ? p2 : String.valueOf(probability.getProbability2().intValue());
                p3 = probability.getProbability3() == null ? p3 : String.valueOf(probability.getProbability3().intValue());
                probabi = probability.getProbabilityRulesId() == null ? probabi : probability.getProbabilityRulesId();
                baseNum = probability.getBaseNum() == null ? baseNum : String.valueOf(probability.getBaseNum());
            }
            redisUtil.setString(RedisKeyGenerator.getMachineP1(dollId), p1);
            redisUtil.setString(RedisKeyGenerator.getMachineP2(dollId), p2);
            redisUtil.setString(RedisKeyGenerator.getMachineP3(dollId), p3);
            redisUtil.setString(RedisKeyGenerator.getMachineBaseNum(dollId), baseNum);
            Integer chargeSum = machineDao.findMemberCharge(memberId);
            chargeSum = chargeSum == null ? 0 : chargeSum;
//            redisUtil.setString(RedisKeyGenerator.getMachineCharge(dollId),String.valueOf(chargeSum));
//            //标记房间类型
//            Doll doll1 = dollDao.selectByPrimaryKey(dollId);
//            redisUtil.setString(RedisKeyGenerator.getMachineType(dollId),String.valueOf(doll1.getMachineType()));
//            Member member =  memberDao.selectById(memberId);
//            Timestamp loginDate = member.getLastLoginDate();
//            Timestamp registerDate = member.getRegisterDate();
//            int rs = TimeUtil.getTimeDifference( loginDate,registerDate);
//            redisUtil.setString(RedisKeyGenerator.getMemberNew(dollId),String.valueOf(0));
//            if (rs<60) {//新用户标致
//            	redisUtil.setString(RedisKeyGenerator.getMemberNew(dollId),String.valueOf(1));
//            }
            if (chargeSum == 0) {
                redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p1);
            } else if (chargeSum > 0 && chargeSum <= probabi) {
                redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p2);
            } else if (chargeSum > probabi) {
                redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p3);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean startPlay(Integer dollId, Member member) {
        // TODO Auto-generated method stub
        //dollRoomDao.setPlayFlag(dollId, memberId);
        Integer memberId = member.getId();
        logger.info("startPlay 参数dollId:{},memberId:{}", dollId, memberId);
        Doll doll = new Doll();
        doll.setId(dollId);
        doll.setMachineStatus("游戏中");
        doll.setModifiedDate(TimeUtil.getTime());
//        doll.setModifiedBy(memberId);
        if (dollDao.updateByPrimaryKeySelective(doll) > 0) {
//			redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId));
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "游戏中");
            if (!redisUtil.getString((RedisKeyGenerator.getRoomHostKey(dollId))).equals(String.valueOf(memberId))) {
                return false;
            }
            //扣费操作
            Account baseAccount = member.getAccount();
            Account account = new Account();
            account.setId(baseAccount.getId());
            account.setCoins(-doll.getPrice());
            member.setModifiedDate(TimeUtil.getTime());
            member.setModifiedBy(member.getId());
            memberDao.updateByPrimaryKeySelective(member);
            accountService.updateMemberCoin(account);//hi币扣减

            //生成消费记录
            Charge chargeRecord = new Charge();
            chargeRecord.setChargeDate(TimeUtil.getTime());
            chargeRecord.setChargeMethod("游戏房间(" + doll.getName() + ")消费");
            chargeRecord.setCoins(baseAccount.getCoins());
            chargeRecord.setCoinsSum(-doll.getPrice());
            chargeRecord.setDollId(doll.getId());
            chargeRecord.setMemberId(member.getId());
            chargeRecord.setType("expense");
            chargeDao.insertChargeHistory(chargeRecord);
        }
        //查找机器概率  房主概率
        MachineProbability probability = machineDao.findByDollId(dollId);
        String p1 = "15";
        String p2 = "10";
        String p3 = "5";
        Integer probabi = 100;//默认金额标准
        if (probability != null) {
            p1 = probability.getProbability1() == null ? p1 : String.valueOf(probability.getProbability1());
            p2 = probability.getProbability2() == null ? p2 : String.valueOf(probability.getProbability2());
            p3 = probability.getProbability3() == null ? p3 : String.valueOf(probability.getProbability3());
            probabi = probability.getProbabilityRulesId() == null ? probabi : probability.getProbabilityRulesId();
        }
        redisUtil.setString(RedisKeyGenerator.getMachineP1(dollId), p1);
        redisUtil.setString(RedisKeyGenerator.getMachineP2(dollId), p2);
        redisUtil.setString(RedisKeyGenerator.getMachineP3(dollId), p3);
        Integer chargeSum = machineDao.findMemberCharge(member.getId());
        if (chargeSum == 0) {
            redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p1);
        } else if (chargeSum > 0 && chargeSum <= probabi) {
            redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p2);
        } else if (chargeSum > probabi) {
            redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p3);
        } else {
            redisUtil.setString(RedisKeyGenerator.getMachineHost(dollId), p1);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean consumePlay(Doll doll, Member member, String state) {
        //logger.info("consumePlay 参数doll:{},member:{}", doll.getId(), member.getId());
        if (doll.getPrice() < 0) {
            return false;
        }
        // Integer num = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberClaw(member.getId())));
        //扣费计数
        Integer num = GameProcessUtil.getInstance().addCountGameLock(member.getId(), doll.getId(), GameProcessEnum.GAME_CONSUME);
        logger.info("consumePlay 参数doll={},member={},num={},state={}", doll.getId(), member.getId(), num, state);
        if (num == 1) {//扣费一次
            //扣费操作
            Account baseAccount = member.getAccount();
            if (doll.getPrice() > baseAccount.getCoins()) {
                return false;
            }
            Account account = new Account();
            account.setId(baseAccount.getId());
            member.setModifiedDate(TimeUtil.getTime());
            member.setModifiedBy(member.getId());
            memberDao.updateByPrimaryKeySelective(member);
            //生成消费记录
            Charge chargeRecord = new Charge();

            Integer currCoins = baseAccount.getCoins();
            //扣费操作
            //account.setCoins(currCoins - doll.getPrice());
            account.setCoins(-doll.getPrice());

            if ("1".equals(state)) {//异常结束
                account.setCoins(0);
            } else {
                accountService.updateMemberCoin(account);
            }

            //accountService.updateById(account);
            chargeRecord.setChargeDate(TimeUtil.getTime());
            switch (doll.getMachineType()) {
                case 0:
                    chargeRecord.setChargeMethod("普通房间(" + doll.getName() + ")消费");
                    break;
                case 1:
                    chargeRecord.setChargeMethod("练习房间(" + doll.getName() + ")消费");
                    break;
                case 2:
                    chargeRecord.setChargeMethod("化妆房间(" + doll.getName() + ")消费");
                    break;
                case 3:
                    chargeRecord.setChargeMethod("数码房间(" + doll.getName() + ")消费");
                    break;
                default:
                    chargeRecord.setChargeMethod("无效房间(" + doll.getName() + ")消费");
                    break;
            }
            chargeRecord.setCoins(currCoins);
            chargeRecord.setCoinsSum(-doll.getPrice());
            if ("1".equals(state)) {//异常结束
                chargeRecord.setCoinsSum(0);
                chargeRecord.setChargeMethod("异常币已返回");
            }
            chargeRecord.setDollId(doll.getId());
            chargeRecord.setMemberId(member.getId());
            chargeRecord.setType("expense");
            // chargeDao.insertChargeHistory(chargeRecord);

            Integer recordNum = GameProcessUtil.getInstance().addCountGameLock(member.getId(), doll.getId(), GameProcessEnum.GAME_CHARGE_HISTORY);
            logger.info("recordNum:" + recordNum + "正常结束生成消费计数");
            if (recordNum == 1) {//记录一次
                chargeDao.insertChargeHistory(chargeRecord);
            }
        }
        return true;
    }

//    @Override
//    @Transactional
//    public boolean startPlay(Integer dollId, Integer memberId) {
//        // TODO Auto-generated method stub
//        //dollRoomDao.setPlayFlag(dollId, memberId);
//        logger.info("startPlay 参数dollId:{},memberId:{}", dollId, memberId);
//        Doll doll = new Doll();
//        doll.setId(dollId);
//        doll.setMachineStatus("游戏中");
//        doll.setModifiedDate(TimeUtil.getTime());
//        doll.setModifiedBy(memberId);
//        if (dollDao.updateByPrimaryKeySelective(doll) > 0) {
//            redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId), 1);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "游戏中");
////            if (!redisUtil.getString((RedisKeyGenerator.getRoomHostKey(dollId))).equals(String.valueOf(memberId))) {
////                return false;
////            }
//        }
//        return true;
//    }


    @Override
    public Integer checkPlaying(Integer dollId, Integer memberId) {
        // 1  不能玩    0  能玩
        logger.info("checkPlaying 参数dollId:{}", dollId);
        //先从缓存获取房间楼主标记
        String dollHostKey = RedisKeyGenerator.getRoomHostKey(dollId);
        //判断是否有房主
        if (redisUtil.existsKey(dollHostKey)) {
            Integer hostMemberId = Integer.parseInt(redisUtil.getString(dollHostKey));
            //如果房主是自己
            //表示可以玩
            if (memberId.equals(hostMemberId)) {
                //redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId));
                return 0;
            }
            return 1;
        } else {
            //空闲状态 设置房主
            //redisUtil.setString(RedisKeyGenerator.getRoomHostKey(dollId), String.valueOf(memberId));
            return 0;
        }
    }

    @Override
    @Transactional
    public boolean endRound(Integer dollId, Integer memberId, Integer catchFlag, String state) {
        //String gameNum = StringUtils.getCatchHistoryNum().replace("-", "").substring(0, 20);
        String gameNum = GameProcessUtil.getInstance().getGameNum(memberId, dollId);
        return endRound(dollId, memberId, catchFlag, gameNum, state);
    }

    @Override
    @Transactional
    public boolean endRound(Integer dollId, Integer memberId, Integer catchFlag, String gameNum, String state) {
        // TODO Auto-generated method stub
        //dollRoomDao.clearPlayFlagByDollId(dollId);
        Doll machine = dollDao.selectByPrimaryKey(dollId);
        //logger.info("endRound 参数dollId:{},memberId:{},catchFlag:{}", dollId, memberId, catchFlag);

        // Integer num = 0;
        // if (redisUtil.existsKey(RedisKeyGenerator.getGameCatchHistory(dollId))) {
        // 	num = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getGameCatchHistory(dollId))) + 1;
        // }
        // redisUtil.setString(RedisKeyGenerator.getGameCatchHistory(dollId), String.valueOf(num), 60 * 5);

        Integer num = GameProcessUtil.getInstance().addCountGameLock(memberId, dollId, GameProcessEnum.GAME_HISTORY);
        if (num <= 1) {
            //生成抓取记录
            CatchHistory catchHistory = new CatchHistory();
            catchHistory.setCatchDate(TimeUtil.getTime());
            if (catchFlag > 0) {
                catchHistory.setCatchStatus("抓取成功");
            } else {
                catchHistory.setCatchStatus("抓取失败");
            }
            if ("1".equals(state)) {
                catchHistory.setCatchStatus("游戏异常,币已返回");
            }
            catchHistory.setDollId(dollId);
            catchHistory.setMemberId(memberId);
            //String gameNum = StringUtils.getCatchHistoryNum().replace("-", "").substring(0,20);
            catchHistory.setGameNum(gameNum);
            catchHistory.setMachineType(machine.getMachineType());
            catchHistory.setDollCode(machine.getDollID());
            catchHistory.setDollName(machine.getName());
            catchHistory.setDollUrl(machine.getTbimgRealPath());
            catchHistoryDao.insertCatchHistory(catchHistory);

            //用户抓取次数加1
            Member member = memberDao.selectById(memberId);
            member.setCatchNumber(member.getCatchNumber() + 1);
            memberDao.updateByPrimaryKeySelective(member);

            //生成抓取次数奖励
//            insertByCatchLevel(member);
        }

        String machineStatus = machine.getMachineStatus();
        if ("维修中".equals(machineStatus) || "维护中".equals(machineStatus) || "未上线".equals(machineStatus)) {
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "维修中");
        }
        return true;
    }

    private void insertByCatchLevel(Member member) {
        RechargeRule rechargeRule = getRechargeCoin(member);
        if (!ObjectUtils.isEmpty(rechargeRule)) {
            //生成消费记录
            Charge chargeRecord = new Charge();
            chargeRecord.setMemberId(member.getId());
            chargeRecord.setCoins(member.getCoins());
            chargeRecord.setCoinsSum(rechargeRule.getCoin());
            chargeRecord.setType("income");
            chargeRecord.setChargeDate(TimeUtil.getTime());
            chargeRecord.setChargeMethod("抓取次数累积奖励");
            chargeDao.insertChargeHistory(chargeRecord);
            //加币操作
            Account baseAccount = new Account();
            baseAccount.setId(member.getAccount().getId());
            baseAccount.setCoins(rechargeRule.getCoin());
            accountService.updateMemberCoin(baseAccount);//hi币
        }
    }

    public RechargeRule getRechargeCoin(Member member) {

        List<RechargeRule> rechargeRules = rechargeRuleMapper.selectByAll(Enviroment.CATCH_TYPE);
        for (int i = 1; i <= rechargeRules.size(); i++) {
            Integer catchNumber = member.getCatchNumber();
            if ((i == rechargeRules.size() && catchNumber >= rechargeRules.get(i - 1).getPrice().intValue()) ||
                    catchNumber >= rechargeRules.get(i - 1).getPrice().intValue() &&
                            catchNumber < rechargeRules.get(i).getPrice().intValue()) {

                int i1 = member.getCatchNumLevel().compareTo(rechargeRules.get(i - 1).getPrice().intValue());
                if (i1 == 0) {
                    return null;
                }
                return rechargeRules.get(i - 1);

            }
        }
        return null;
    }


    /**
     * 获取房间娃娃详情
     *
     * @param dollId
     * @return
     */
    @Override
    public ResultMap selectDollParticularsById(Integer dollId) {
        DollParticulars dollParticulars = dollRoomDao.selectDollParticularsById(dollId);
        if (dollParticulars == null) {
            dollParticulars = new DollParticulars();
        }
        List<DollImgPojo> dollImgList = getDollImg(dollId);
        Map<String, Object> map = new HashMap<>();
        map.put("dollParticulars", dollParticulars);
        map.put("dollImgList", dollImgList);
        logger.info("查询娃娃详情接口参数成功");
        return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, map);
    }

    @Override
    @Transactional
    public void endPlayByCatchCount(Integer memberId, Integer dollId) {
        //  判断五分钟内是否三次抓取成功
        Doll dollR = dollDao.selectByPrimaryKey(dollId);
        if (!dollR.getMachineType().equals(2)) {
            return;
        }
        List<CatchHistory> catchHistories = catchHistoryDao.selectByDollId(dollId);
        Doll doll = new Doll();
        doll.setId(dollId);
        if (catchHistories.size() >= 3) {
            doll.setMachineStatus("维修中");
            dollDao.updateClean(doll);
            redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
            Doll machine = dollDao.selectByPrimaryKey(dollId);
            String machineStatus = machine.getMachineStatus();
            if ("维修中".equals(machineStatus) || "维护中".equals(machineStatus) ||
                    "未上线".equals(machineStatus)) {
                redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "维修中");
            } else {
                redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "空闲中");
            }
            logger.info("dollId={}房间频繁抓中，将房间设为维修中", dollId);
        }
    }

    @Override
    public ResultMap getCatchProParams(Integer memberId) {
        RechargeRulePojp rechargeRulePojp = new RechargeRulePojp();
        Member member = memberDao.selectById(memberId);
        List<RechargeRule> rechargeRules = rechargeRuleMapper.selectByAll(Enviroment.RECHARGE_TYPE);
        rechargeRulePojp.setRechargePrice(member.getCatchNumber());

        for (int i = 0; i < rechargeRules.size(); i++) {
            if (rechargeRules.get(i).getPrice().intValue() - (member.getCatchNumLevel()) == 0) {
                if (i == rechargeRules.size() - 1) {
                    rechargeRulePojp.setRechargeLevel(rechargeRules.get(i).getPrice().intValue());
                    rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(i).getCoin());
                } else {
                    rechargeRulePojp.setRechargeLevel(rechargeRules.get(i + 1).getPrice().intValue());
                    rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(i + 1).getCoin());
                }
            }
        }
        if (StringUtils.isEmpty(rechargeRulePojp.getRechargeLevel())) {
            rechargeRulePojp.setRechargeLevel(rechargeRules.get(0).getPrice().intValue());
            rechargeRulePojp.setRechargeLevelCoin(rechargeRules.get(0).getCoin());
        }
        rechargeRulePojp.setRechargeLevelMax(rechargeRules.get(rechargeRules.size() - 1).getPrice().intValue());
        return new ResultMap("操作成功", rechargeRulePojp);
    }

    @Override
    @Transactional
    public boolean endPlay(Integer dollId, Integer memberId) {
        // TODO Auto-generated method stub
        //dollRoomDao.clearPlayFlagByDollId(dollId);
        // logger.info("endPlay 参数dollId:{},memberId:{}", dollId, memberId);

        Doll doll = new Doll();
        doll.setId(dollId);
        doll.setMachineStatus("空闲中");

        dollDao.updateClean(doll);
        redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(dollId));
        Doll machine = dollDao.selectByPrimaryKey(dollId);
        String machineStatus = machine.getMachineStatus();
        if ("维修中".equals(machineStatus) || "维护中".equals(machineStatus) ||
                "未上线".equals(machineStatus)) {
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "维修中");
        } else {
            redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), "空闲中");
        }
        return true;
    }

    @Override
    public DollRoom getDollId(Integer memberId) {
        // TODO Auto-generated method stub
        logger.info("endPlay 参数memberId:{}", memberId);
        return dollRoomDao.getDollId(memberId);
    }


}
