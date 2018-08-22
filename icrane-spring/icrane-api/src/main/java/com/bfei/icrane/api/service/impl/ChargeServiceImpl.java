package com.bfei.icrane.api.service.impl;

import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.RiskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author lgq Version: 1.0 Date: 2017年9月22日date Description: 用户Service接口实现类.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("ChargeService")
@Transactional
public class ChargeServiceImpl implements ChargeService {
    private static final Logger logger = LoggerFactory.getLogger(ChargeServiceImpl.class);
    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private SystemPrefDao systemPrefDao;

    @Autowired
    private DollService dollService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private DollOrderService dollOrderService;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RiskManagementService riskManagementService;

    @Autowired
    private RechargeRuleMapper rechargeRuleMapper;

    @Autowired
    private OemTemplateMapper oemTemplateMapper;


    @Override
    public Charge getChargeRules(Double chargePrice) {
        // TODO Auto-generated method stub
        logger.info("getChargeRules参数chargePrice:{}", chargePrice);
        Charge charge = chargeDao.getChargeRules(chargePrice);
        logger.info("返回charge:{}", charge);
        return charge;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Integer insertChargeHistory(Charge charge) {
        try {
            // charge.setCoinsSum(charge.getCoinsSum()+charge.getCoins());
            logger.info("insertChargeHistory参数charge:{}", charge);
            charge.setChargeDate(TimeUtil.getTime());
            //if (charge.getDollId() == null) {
            //	charge.setChargeMethod("现金充值");
            //}
            //加币加钻石加成长值
            chargeDao.updateMemberCount(charge);
            Account account = accountService.select(charge.getMemberId());


            Integer result;
            if (charge.getSuperTicketSum() == null) {
                charge.setSuperTicketSum(0);
            }
            if (charge.getCoinsSum() == null) {
                charge.setCoinsSum(0);
            }
            if (charge.getSuperTicketSum() <= 0 && charge.getCoinsSum() > 0) {//普通金币礼包记录
                result = chargeDao.insertChargeHistory(charge);
                insertGrowthHistory(charge, account);
                if (charge.getPrepaidAmt() != null && charge.getPrepaidAmt() > 0) {
                    charge.setType("income");
                    charge.setGrowthValue(account.getGrowthValue());
                    charge.setGrowthValueSum(account.getGrowthValue().add(new BigDecimal(charge.getPrepaidAmt())));
                    chargeDao.insertGrowthValueHistory(charge);
                }
                return result;
            }

            if (charge.getSuperTicketSum() == 0 && charge.getCoinsSum() == 0) {//普通金币礼包记录
                result = chargeDao.insertChargeHistory(charge);
                insertGrowthHistory(charge, account);
                if (charge.getPrepaidAmt() != null && charge.getPrepaidAmt() > 0) {
                    charge.setType("income");
                    charge.setGrowthValue(account.getGrowthValue());
                    charge.setGrowthValueSum(account.getGrowthValue().add(new BigDecimal(charge.getPrepaidAmt())));
                    chargeDao.insertGrowthValueHistory(charge);
                }
                return result;
            }
//            if (charge.getSuperTicketSum() > 0 && charge.getCoinsSum() <= 0) {//普通纯钻石礼包记录
//                charge.setCoinsSum(charge.getSuperTicketSum());
//                charge.setCoins(charge.getSuperTicket());
//                charge.setType("s" + charge.getType());
//                result = chargeDao.insertChargeHistory(charge);
//                insertGrowthHistory(charge, account);
//                if (charge.getPrepaidAmt() != null && charge.getPrepaidAmt() > 0) {
//                    charge.setType("income");
//                    charge.setGrowthValue(account.getGrowthValue());
//                    charge.setGrowthValueSum(account.getGrowthValue().add(new BigDecimal(charge.getPrepaidAmt())));
//                    chargeDao.insertGrowthValueHistory(charge);
//                }
//                return result;
//            }
//            if (charge.getSuperTicketSum() > 0 && charge.getCoinsSum() > 0) {//混合礼包记录
//                chargeDao.insertChargeHistory(charge);
//                insertGrowthHistory(charge, account);
//                charge.setCoinsSum(charge.getSuperTicketSum());
//                charge.setCoins(charge.getSuperTicket());
//                charge.setType("s" + charge.getType());
//                result = chargeDao.insertChargeHistory(charge);
//                if (charge.getPrepaidAmt() != null && charge.getPrepaidAmt() > 0) {
//                    charge.setType("income");
//                    charge.setGrowthValue(account.getGrowthValue());
//                    charge.setGrowthValueSum(account.getGrowthValue().add(new BigDecimal(charge.getPrepaidAmt())));
//                    chargeDao.insertGrowthValueHistory(charge);
//                }
//                return result;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //累计充值
    private void insertGrowthHistory(Charge charge, Account account) {
        RechargeRule rechargeRule = getRechargeCoin(account);
        if (!ObjectUtils.isEmpty(rechargeRule)) {

            logger.info("用户id={},当月累计充值={}", charge.getMemberId(), account.getGrowthValueMonth());
            //生成消费记录
            Charge chargeRecord = new Charge();
            chargeRecord.setMemberId(charge.getMemberId());
            chargeRecord.setCoins(charge.getCoins() + charge.getCoinsSum());
            chargeRecord.setCoinsSum(rechargeRule.getCoin());
            chargeRecord.setPrepaidAmt(charge.getPrepaidAmt());
            chargeRecord.setType("income");
            chargeRecord.setChargeDate(TimeUtil.getTime());
            chargeRecord.setChargeMethod("充值累积奖励");
            chargeDao.insertChargeHistory(chargeRecord);
            //加币操作
            Account baseAccount = new Account();
            baseAccount.setId(account.getId());
            baseAccount.setCoins(rechargeRule.getCoin());
            baseAccount.setGrowthValueMonthLevel(rechargeRule.getPrice().intValue());
            accountService.updateMemberCoin(baseAccount);//hi币
        }
    }

    private RechargeRule getRechargeCoin(Account account) {

        List<RechargeRule> rechargeRules = rechargeRuleMapper.selectByAll(Enviroment.RECHARGE_TYPE);
        for (int i = 1; i <= rechargeRules.size(); i++) {
            BigDecimal growthValueMonth = account.getGrowthValueMonth();
            if ((i == rechargeRules.size() && growthValueMonth.intValue() >= rechargeRules.get(i - 1).getPrice().intValue()) ||
                    (growthValueMonth.intValue() >= rechargeRules.get(i - 1).getPrice().intValue() &&
                            growthValueMonth.intValue() < rechargeRules.get(i).getPrice().intValue())) {
                int i1 = new BigDecimal(account.getGrowthValueMonthLevel()).compareTo(rechargeRules.get(i - 1).getPrice());
                if (i1 == 0) {
                    return null;
                }
                return rechargeRules.get(i - 1);

            }
        }
        return null;
    }

    @Override
    public Integer insertChargeHistory(Charge charge, Integer memberId, Long[] orderIds) {
        //logger.info("insertChargeHistory参数charge:{},memberId:{},orderIds:{}", charge, memberId, orderIds);
        List<DollOrder> list = new ArrayList<>();
       /* for (int orderId : orderIds) {
            DollOrder dollOrder = dollOrderService.selectByPrimaryKey(orderId);
            if ("寄存中".equals(dollOrder.getStatus())) {//娃娃兑换bug  筛选兑换的娃娃
                dollOrder.setStatus("已兑换");
                dollOrder.setModifiedDate(TimeUtil.getTime());
                dollOrder.setModifiedBy(dollOrder.getOrderBy());
                dollOrderService.updateByPrimaryKeySelective(dollOrder);
                sumCoins += dollOrder.getDollRedeemCoins();
                *//*Map<String, DollOrder> dollMap = new HashMap<>();
                dollMap.put("dollOrder", dollOrder);*//*
                list.add(dollOrder);

                *//*List<DollOrderItem> orderItem = dollOrder.getOrderItems();
                for (DollOrderItem dollOrderItem : orderItem) {
                    Map<String, DollOrderItem> dollMap = new HashMap<>();
                    dollMap.put("dollItem", dollOrderItem);
                    list.add(dollMap);
                }*//*
            }
        }*/
        Integer sumCoins = 0;
        Integer result = 0;
        List<DollOrder> dollOrders = dollOrderService.selectListByPrimaryKey(orderIds);
        for (DollOrder dollOrder : dollOrders) {
            if ("寄存中".equals(dollOrder.getStatus())) {//娃娃兑换bug  筛选兑换的娃娃
                dollOrder.setStatus("已兑换");
                dollOrder.setModifiedDate(TimeUtil.getTime());
                dollOrder.setModifiedBy(dollOrder.getOrderBy());
                dollOrderService.updateByPrimaryKeySelective(dollOrder);
                sumCoins += dollOrder.getDollRedeemCoins();
                /*Map<String, DollOrder> dollMap = new HashMap<>();
                dollMap.put("dollOrder", dollOrder);*/
                list.add(dollOrder);

                /*List<DollOrderItem> orderItem = dollOrder.getOrderItems();
                for (DollOrderItem dollOrderItem : orderItem) {
                    Map<String, DollOrderItem> dollMap = new HashMap<>();
                    dollMap.put("dollItem", dollOrderItem);
                    list.add(dollMap);
                }*/
            }
        }
        if (list.size() > 0) {
            for (DollOrder dollOrder : list) {
                Integer dollId = dollOrder.getOrderItems().getDoll().getId();
                Doll doll = dollService.selectByPrimaryKey(dollId);
                Account account = accountService.select(memberId);
                charge.setMemberId(memberId);
                charge.setCoins(account.getCoins());
                //charge.setCoinsSum(doll.getRedeemCoins() + member.getCoins());
                charge.setCoinsSum(dollOrder.getDollRedeemCoins());
                charge.setChargeDate(TimeUtil.getTime());
                charge.setType("income");
                charge.setDollId(doll.getId());
                charge.setChargeMethod("由<" + doll.getName() + ">兑换获取");

                //charge.setCoins(doll.getRedeemCoins());
                // charge.setCoinsSum(charge.getCoinsSum()+charge.getCoins());
                charge.setChargeDate(TimeUtil.getTime());
                //	if (charge.getDollId() == null) {
                //		charge.setChargeMethod("现金充值");
                //	}
                chargeDao.updateMemberCount(charge);
                result = chargeDao.insertChargeHistory(charge);
            }
        }
        //logger.info("返回result:{}", result);
        if (result != 0) {
            //logger.info("返回CoinsSum:{}", sumCoins);
            return sumCoins;
        } else {
            return 0;
        }
    }

    @Override
    public Integer changeCount(List<DollOrder> dollOrderList) {
        logger.info("changeCount 参数:{}", dollOrderList);
        Charge charge = new Charge();
        DollOrder dOrder = new DollOrder();
        //  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Integer result = 0;
        for (DollOrder dollOrder : dollOrderList) {
            /*List<DollOrderItem> orderItem = dollOrder.getOrderItems();
            for (DollOrderItem dollOrderItem : orderItem) {
                Map<String, Integer> dollMap = new HashMap<String, Integer>();
                dollMap.put("orderId", dollOrder.getId());
                dollMap.put("memberId", dollOrder.getOrderBy());
                dollMap.put("dollId", dollOrderItem.getDoll().getId());
                dollOrderItem.getDoll().getId();
                list.add(dollMap);
            }*/
            DollOrderItem orderItem = dollOrder.getOrderItems();
/*            Map<String, Object> dollMap = new HashMap<String, Object>();
            dollMap.put("orderId", dollOrder.getId());
            dollMap.put("memberId", dollOrder.getOrderBy());
            dollMap.put("dollId", orderItem.getDoll().getId());
            list.add(dollMap);*/

            dOrder.setStatus("已兑换");
            dOrder.setId(dollOrder.getId());
            result = dollOrderService.updateByPrimaryKeySelective(dOrder);
            Doll doll = dollService.selectByPrimaryKey(orderItem.getDoll().getId());
            Member member = memberService.selectById(dollOrder.getOrderBy());
            charge.setMemberId(dollOrder.getOrderBy());
            //charge.setCoinsSum(doll.getRedeemCoins() + member.getCoins());
            charge.setCoinsSum(doll.getRedeemCoins());
            charge.setChargeDate(TimeUtil.getTime());
            charge.setType("income");
            charge.setDollId(doll.getId());
            charge.setChargeMethod("由<" + doll.getName() + ">兑换获取");
            //charge.setCoins(doll.getRedeemCoins());
            charge.setCoins(member.getAccount().getCoins());
            // charge.setCoinsSum(charge.getCoinsSum()+charge.getCoins());
            charge.setChargeDate(TimeUtil.getTime());
            //	if (charge.getDollId() == null) {
            //		charge.setChargeMethod("现金充值");
            //	}
            chargeDao.updateMemberCount(charge);
            result = chargeDao.insertChargeHistory(charge);

        }
/*        Integer result = 0;
        for (Map<String, Object> map : list) {
            dOrder.setStatus("已兑换");
            dOrder.setId((Long)map.get("orderId"));
            result = dollOrderService.updateByPrimaryKeySelective(dOrder);
            Integer dollId = (Integer)map.get("dollId");
            Doll doll = dollService.selectByPrimaryKey(dollId);
            Member member = memberService.selectById((Integer)map.get("memberId"));
            charge.setMemberId((Integer)map.get("memberId"));
            //charge.setCoinsSum(doll.getRedeemCoins() + member.getCoins());
            charge.setCoinsSum(doll.getRedeemCoins());
            charge.setChargeDate(TimeUtil.getTime());
            charge.setType("income");
            charge.setDollId(doll.getId());
            charge.setChargeMethod("由<" + doll.getName() + ">兑换获取");
            //charge.setCoins(doll.getRedeemCoins());
            charge.setCoins(member.getAccount().getCoins());
            // charge.setCoinsSum(charge.getCoinsSum()+charge.getCoins());
            charge.setChargeDate(TimeUtil.getTime());
            //	if (charge.getDollId() == null) {
            //		charge.setChargeMethod("现金充值");
            //	}
            chargeDao.updateMemberCount(charge);
            result = chargeDao.insertChargeHistory(charge);
        }*/
        logger.info("resul:{}", result);
        return result;
    }

    @Override
    public List<Charge> getChargeHistory(Integer memberId) {
        // TODO Auto-generated method stub
        logger.info("getChargeHistory 参数:{}", memberId);
        return chargeDao.getChargeHistory(memberId);
    }

    @Override
    public void inviteChargeFirst(Integer memberId) {
        Map<String, Integer> parameterMap = new HashMap<String, Integer>();
        parameterMap.put("memberId", memberId);
        parameterMap.put("result", 0);
        chargeDao.inviteChargeFirst(parameterMap);
    }

    @Override
    public ResultMap getSuccessfulRechargeRecords(Integer memberId, String mchOrderNo) {
        Charge charge = null;
        int i = 0;
        while (i < 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i += 1;
            if (chargeDao.getChargeByMchOrderNo(mchOrderNo) != null) {
                //查询成功
                Map<String, Object> map = new HashMap<>();
                Account account = accountService.select(memberId);
                map.put("coins", account.getCoins());
                map.put("superTicket", account.getSuperTicket());
                return new ResultMap(Enviroment.PAY_SUCCESS, map);
            }
        }
        //查询失败
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_MESSAGE_TIMEOUT);
    }

    @Override
    public ResultMap invite(Integer memberId, String inviteCode) {
        //被邀请人信息
        Member member = memberService.selectById(memberId);
        if (null == member) {
            logger.info("邀请异常,无此用户");
        }
        //验证是否已经被邀请过
        if (member.isInviteFlgWeb() == true) {
            logger.info("邀请异常=" + Enviroment.RECEIVED_INVITATION_AWARD);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RECEIVED_INVITATION_AWARD);
        }
        //邀请人信息
        Member memberInvite = memberService.selectByMemberID(inviteCode);
        //查询邀请人是否存在
        if (memberInvite == null) {
            logger.info("邀请异常=" + Enviroment.INVALID_INVITATION_CODE);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.INVALID_INVITATION_CODE);
        }
        //邀请码是否是自己
        if (memberId == memberInvite.getId()) {
            logger.info("邀请异常=" + Enviroment.PROHIBIT_INVITING_ONESELF);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.PROHIBIT_INVITING_ONESELF);
        }
        //检测双方设备号
        if (riskManagementService.isOneIMEI(memberId, memberInvite.getId())) {
            logger.info("邀请异常=" + Enviroment.ISONEIMEI_ONESELF);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.ISONEIMEI_ONESELF);
        }
        //生成娃娃币流水
        Charge invicteCharge = new Charge();
        invicteCharge.setCode(Enviroment.CODE_INVITE_BONUS);
        invicteCharge.setInviteMemberId(memberInvite.getId());
        invicteCharge.setMemberId(memberId);
        invicteCharge.setCoins(memberInvite.getAccount().getCoins());
        invicteCharge.setType(Enviroment.TYPE_INCOME);
        //邀请奖励
        SystemPref INVITE_BONUS = systemPrefDao.selectByPrimaryKey(Enviroment.CODE_INVITE_BONUS);
//        //邀请人上限
//        SystemPref INVITE_BONUS_COUNT = systemPrefDao.selectByPrimaryKey(Enviroment.CODE_INVITE_BONUS_COUNT);

        invicteCharge.setInviteCoinsSum(Integer.valueOf(INVITE_BONUS.getValue()));//邀请人获取赠送币数量
        invicteCharge.setChargeMethod("由邀请好友获取,邀请好友id" + memberService.selectById(invicteCharge.getMemberId()).getMemberID());

//        if (INVITE_BONUS_COUNT.getType() == 1) {
//            Integer inviteLimit = INVITE_BONUS_COUNT == null ? 0 : Integer.parseInt(INVITE_BONUS_COUNT.getValue());
//            //查询邀请次数
//            Integer inviteNum = chargeDao.getChargeByInviteCode(inviteCode);
//            if (inviteNum >= inviteLimit && inviteLimit > 0) {
//                //邀请人获取赠送币数量
//                invicteCharge.setInviteCoinsSum(0);
//                invicteCharge.setChargeMethod("邀请好友奖励达到上限,邀请好友id" + memberService.selectById(invicteCharge.getMemberId()).getMemberID());
//            }
//        }
        invicteCharge.setCoinsSum(Integer.valueOf(INVITE_BONUS.getValue()));//邀请获取娃娃币


        //邀请人获取奖励
        invicteCharge.setChargeDate(TimeUtil.getTime());
        //更新邀请人币数
        chargeDao.updateInviteMemberCount(invicteCharge);
        //生成邀请人记录
        chargeDao.insertInviteChargeHistory(invicteCharge);
        //被邀请人获取奖励
//        chargeDao.updateMemberCount(invicteCharge);
        //标记被邀请人被邀请状态
        memberDao.updateInviteStatus(memberId);
//        chargeDao.insertChargeHistory(invicteCharge);
        //生成邀请记录
        ShareInvite invite = new ShareInvite();
        invite.setInviteCode(inviteCode);
        invite.setInviteMemberId(String.valueOf(invicteCharge.getInviteMemberId()));
        invite.setInvitedId(String.valueOf(memberId));//被邀请人id
        invite.setCreateDate(new Date());
        invite.setState(0);
        chargeDao.insertInvite(invite);
        memberDao.updateChannel(memberId, memberInvite.getRegisterChannel(), memberInvite.getId(), memberService.selectRank(memberInvite.getId()) + 1);
        logger.info("邀请成功=" + Enviroment.INVALID_SUCCESS_MESSAGE);
        return new ResultMap(Enviroment.INVALID_SUCCESS_MESSAGE);
    }

    @Override
    public ResultMap whoInvite(Integer memberId) {
        try {
            String invite = chargeDao.whoInvite(String.valueOf(memberId));
            if (StringUtils.isNotEmpty(invite)) {
                Map<String, String> map = new HashMap<>();
                map.put("invite", invite);
                logger.info("展示邀请人接口成功inviteid=" + invite);
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, map);
            }
            logger.info("展示邀请人接口失败");
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, "没有查询到邀请人的邀请码");
        } catch (Exception e) {
            logger.error("展示邀请人失败异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    @Override
    public ResultMap howInvite(Integer memberId) {
        try {
            Integer invitenumber = chargeDao.howInvite(String.valueOf(memberId));
            if (invitenumber != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("inviteNumber", invitenumber);
                logger.info("展示邀请人接口成功");
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, map);
            }
            logger.info("展示邀请人接口失败");
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, "没有查询到您的邀请人数");
        } catch (Exception e) {
            logger.error("展示邀请人失败异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    @Override
    public int inviteOne(int id, String channel) {
        //channel是否合法
        try {
            Member member = memberService.selectById(id);
            String channelName = null;
            channelName = memberService.verifyChannel(channel);
            if (StringUtils.isEmpty(channelName)) {
                return 2;
            }
            //生成娃娃币流水
            Charge invicteCharge = new Charge();
            invicteCharge.setCode(Enviroment.CODE_INVITE_BONUS);
            invicteCharge.setInviteMemberId(null);
            invicteCharge.setMemberId(id);
            invicteCharge.setCoins(member.getAccount().getCoins());
            invicteCharge.setType(Enviroment.TYPE_INCOME);
            //邀请奖励
            SystemPref INVITE_BONUS = systemPrefDao.selectByPrimaryKey(Enviroment.CODE_INVITE_BONUS);
            //邀请人上限
            //查询邀请次数
            invicteCharge.setCoinsSum(Integer.valueOf(INVITE_BONUS.getValue()));//邀请获取娃娃币
            invicteCharge.setChargeMethod(channelName + "注册惊喜礼包");
            //邀请人获取奖励
            invicteCharge.setChargeDate(TimeUtil.getTime());
            //被邀请人获取奖励
            chargeDao.updateMemberCount(invicteCharge);
            //标记被邀请人被邀请状态
            memberDao.updateInviteStatus(id);
            chargeDao.insertChargeHistory(invicteCharge);

            //生成邀请记录
            ShareInvite invite = new ShareInvite();
            invite.setInviteCode(null);
            invite.setInviteMemberId(String.valueOf(invicteCharge.getInviteMemberId()));
            invite.setInvitedId(String.valueOf(id));//被邀请人id
            invite.setCreateDate(new Date());
            invite.setState(0);
            chargeDao.insertInvite(invite);
            memberDao.updateChannel(id, channel, null, 1);
            logger.info("邀请成功=" + Enviroment.INVALID_SUCCESS_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public void insertGrowthValueHistory(Charge charge) {
        chargeDao.insertGrowthValueHistory(charge);
    }
}
