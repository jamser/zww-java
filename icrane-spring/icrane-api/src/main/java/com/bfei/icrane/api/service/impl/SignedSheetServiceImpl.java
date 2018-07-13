package com.bfei.icrane.api.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.pojos.TurnAnglePojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.api.service.SignedSheetService;
import com.bfei.icrane.core.dao.ChargeDao;
import com.bfei.icrane.core.dao.MemberChargeComboDao;
import com.bfei.icrane.core.dao.MemberDao;
import com.bfei.icrane.core.dao.SignedSheetDao;
import com.bfei.icrane.core.dao.SystemPrefDao;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.models.MemberChargeCombo;
import com.bfei.icrane.core.models.SignedSheet;
import com.bfei.icrane.core.models.SystemPref;
import com.bfei.icrane.core.service.AccountService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 签到的实现类
 */
@Service("signedSheetService")
public class SignedSheetServiceImpl implements SignedSheetService {

    private static final Logger logger = LoggerFactory.getLogger(SignedSheetServiceImpl.class);

    @Autowired
    private SystemPrefDao systemPrefDao;
    @Autowired
    SignedSheetDao signedSheetDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private ChargeDao chargeDao;
    @Autowired
    MemberChargeComboDao memberChargeComboDao;
    @Autowired
    private ChargeService chargeService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AccountService accountService;


    @Override
    public void insert(SignedSheet signedSheet) {
        logger.info("insert 参数wr:{}", signedSheet);
        //签到记录插入
        signedSheetDao.insert(signedSheet);
        //赠送签到奖励
        String code = "SIGN_BONUS";
        logger.info("getSystemPref 参数code:{}", code);
        SystemPref signBouns = systemPrefDao.selectByPrimaryKey(code);
        Member memb = memberDao.selectByMemberID(signedSheet.getMemberId());
        Charge charge = new Charge();
        charge.setCode(code);
        SystemPref systemPref = systemPrefDao.selectByPrimaryKey(charge.getCode());
        charge.setCoins(memb.getAccount().getCoins());//记录签到奖励前
        //Integer coinsSum = charge.getCoins() + memb.getCoins();
        charge.setCoinsSum(Integer.valueOf(systemPref.getValue()));
        charge.setChargeMethod(signBouns.getName());
        charge.setMemberId(memb.getId());
        charge.setType("income");
        charge.setChargeDate(TimeUtil.getTime());

        chargeDao.insertChargeHistory(charge);
        chargeDao.updateMemberCount(charge);
    }

    @Override
    public void insert(String memberId) {
        //赠送签到奖励
        Member memb = memberDao.selectById(Integer.valueOf(memberId));
        SignedSheet signedSheet = selectOneByMemberID(memberId);
        Charge charge = new Charge();
        //流水种类
        charge.setCode("SIGN_BONUS");
        //记录签到奖励前
        charge.setCoins(memb.getAccount().getCoins());
        charge.setCoinsSum(signedSheet.getSignedCoin());
        charge.setChargeMethod(signedSheet.getChargeMethod());
        charge.setMemberId(Integer.valueOf(memberId));
        charge.setType("income");
        charge.setChargeDate(TimeUtil.getTime());
        //保存流水
        chargeDao.insertChargeHistory(charge);
        //账户增值
        chargeDao.updateMemberCount(charge);
    }

    @Override
    public SignedSheet selectOneByMemberID(String memberId) {
        // TODO Auto-generated method stub
        logger.info("selectByMemberID 参数wr:{}", memberId);
        return signedSheetDao.selectOneByMemberID(memberId);
    }

    @Override
    public void giveCoins(String memberId) {
        Member memb = memberDao.selectByMemberID(memberId);
        //时长包套餐
        MemberChargeCombo combo = memberChargeComboDao.selectMemberEffect(memb.getId(), 1);
        if (combo != null) {
            Date currDate = new Date();
            //计算截止时间
            Date limitDate = TimeUtil.plusDay(combo.getChargeDateStart(), combo.getChargeDateLimit());
            //充值当天不赠送,次日赠送  超过了时长包期限不赠送
            if (!TimeUtil.isSameDate(combo.getChargeDateStart(), currDate) && currDate.before(limitDate)) {
                //给购买时长包    添加娃娃币
                if (combo != null && combo.getMemberState() == 0 && combo.getGiveTimes() < combo.getChargeDateLimit()) {
                    Integer times = combo.getGiveTimes();
                    combo.setGiveTimes(times + 1);
                    if (times == combo.getChargeDateLimit()) {
                        //赠送完成
                        combo.setMemberState(1);
                    }
                    if (!currDate.before(limitDate)) {//逾期失效
                        combo.setMemberState(1);
                    }
                    memberChargeComboDao.giveCoins(combo);
                    logger.info("赠送参数combo=" + combo);
                    //赠送
                    Charge charge = new Charge();
                    charge.setMemberId(memb.getId());
                    charge.setCoins(combo.getCoinsGive());
                    charge.setCoinsSum(combo.getCoinsGive());
                    charge.setType("income");
                    charge.setChargeDate(TimeUtil.getTime());
                    charge.setChargeMethod(combo.getChargeName() + "领取");
                    logger.info("充值参数charge=" + charge);
                    Integer result = chargeService.insertChargeHistory(charge);
                    logger.info("充值结果result=" + result);
                }
            }
        }
    }

    /**
     * 连续签到
     *
     * @param memberId 用户userId
     */
    @Override
    public void updateByMemberId(String memberId) {
        signedSheetDao.updateByMemberId(memberId);
        insert(memberId);
    }

    @Override
    public void updateNewDayByMemberId(String memberId) {
        signedSheetDao.updateNewDayByMemberId(memberId);
        insert(memberId);
    }

    @Override
    public void createSignedSheet(String memberId, String memberName) {
        SignedSheet signedSheet = new SignedSheet();
        signedSheet.setSigned(1);
        signedSheet.setCreateTime(new Date());
        signedSheet.setSignedDate(new Date());
        signedSheet.setMemberId(memberId);
        signedSheet.setMemberName(memberName);
        signedSheetDao.insert(signedSheet);
        insert(memberId);
    }

    @Override
    public ResultMap sign(String memberId) {
        //获取用户信息及签到信息
        Member member = memberService.selectById(Integer.valueOf(memberId));
        SignedSheet existSigned = selectOneByMemberID(memberId);
        //判断是否第一次签到
        if (existSigned == null) {
            //创建签到信息
            createSignedSheet(memberId, member.getName());
            List<Object> list = new ArrayList<>();
            list.add(selectOneByMemberID(memberId));
            logger.info("连续签到接口签到成功memberId=" + memberId);
            return new ResultMap(Enviroment.SIGN_IN_SUCCESS, list);
        }
        //获取用户签到时间
        Calendar checkdateCalendar = Calendar.getInstance();
        checkdateCalendar.setTime(existSigned.getSignedDate());
        //获取今天凌晨时间
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0, 0);
        //获取昨天凌晨时间
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(yesterday.get(Calendar.YEAR), yesterday.get(Calendar.MONTH), yesterday.get(Calendar.DATE) - 1, 0, 0, 0);
        //判断用户签到时间是否是在今天凌晨之前
        if (checkdateCalendar.before(today)) {
            //如果上次签到是昨天凌晨之前,或者连续签到满七天，说明没有连续签到
            if (checkdateCalendar.before(yesterday) || selectOneByMemberID(memberId).getSigned() >= 7) {
                //将签到天数归为1,重新开始
                updateNewDayByMemberId(memberId);
            } else {
                //连续签到
                updateByMemberId(memberId);
            }
            List<Object> list = new ArrayList<>();
            list.add(selectOneByMemberID(memberId));
            logger.info("连续签到接口签到成功memberId=" + memberId);
            return new ResultMap(Enviroment.SIGN_IN_SUCCESS, list);
        } else {
            logger.info("连续签到接口签到失败" + Enviroment.HAVE_BEEN_SIGN);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.HAVE_BEEN_SIGN);
        }
    }

    @Override
    public ResultMap hassign(String memberId, String head) {
        Member member = memberService.selectById(Integer.valueOf(memberId));
        //获取周卡
        MemberChargeCombo weeksCombo = memberChargeComboDao.selectByUserIdAndChargeType(Integer.valueOf(memberId), 2);
        if (weeksCombo == null) {
            weeksCombo = new MemberChargeCombo();
        }
        //月卡
        MemberChargeCombo monthCombo = memberChargeComboDao.selectByUserIdAndChargeType(Integer.valueOf(memberId), 3);
        if (monthCombo == null) {
            monthCombo = new MemberChargeCombo();
        }
        //账户信息
        Account baseaccount = member.getAccount();
        Account account = new Account();
        account.setId(baseaccount.getId());
        account.setBitState(baseaccount.getBitState());
        //获取签到记录
        SignedSheet existSigned = selectOneByMemberID(memberId);
        //判断是否第一次签到
        Map<String, Object> stringMap = new HashMap<>();
        //周卡月卡有效测试
        if (TimeUtil.getEndTime(weeksCombo) <= 0 && account.getWeeksCardEnd()) {
            account.removeState(BitStatesUtils.WEEKS_CARD);
        }
        if (TimeUtil.getEndTime(monthCombo) <= 0 && account.getMonthCardend()) {
            account.removeState(BitStatesUtils.MONTH_CARD);
        }
        accountService.updateBitStatesById(account);
        // accountService.updateById(account);
        // 周卡状态
        stringMap.put("weeksstatus", baseaccount.getTodyWeeksCard());
        stringMap.put("weeksdays", TimeUtil.getEndTime(weeksCombo));
        stringMap.put("weeksdayscoins", weeksCombo.getCoinsGive());
        stringMap.put("weeksdate", TimeUtil.getdate(weeksCombo));
        // 月卡状态
        stringMap.put("monthstatus", baseaccount.getTodyMonthCard());
        stringMap.put("monthdays", TimeUtil.getEndTime(monthCombo));
        stringMap.put("monthdayscoins", monthCombo.getCoinsGive());
        stringMap.put("monthdate", TimeUtil.getdate(monthCombo));
        if (existSigned == null) {
            giveCoins(member.getMemberID());
            ResultMap resultMap = new ResultMap(Enviroment.N_IN);
            List<Object> list = new ArrayList<>();
            stringMap.put("signdays", 0);
            stringMap.put("signstatus", Enviroment.N_IN_CODE);
            list.add(stringMap);
            resultMap.setResultData(list);
            logger.info("未签到,可以签到用户" + memberId + member.getName());
            return resultMap;
        }
        //获取当前时间
        Calendar checkdateCalendar = Calendar.getInstance();
        //获取用户签到时间
        Date checkDate = existSigned.getSignedDate();
        //设置为用户签到时间
        checkdateCalendar.setTime(checkDate);
        //获取今天凌晨时间
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0, 0);
        //获取昨天凌晨时间
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(yesterday.get(Calendar.YEAR), yesterday.get(Calendar.MONTH), yesterday.get(Calendar.DATE) - 1, 0, 0, 0);

        //判断用户签到时间是否是在今天凌晨之前
        Integer signed = selectOneByMemberID(memberId).getSigned();
        if (checkdateCalendar.before(today)) {
            //IOS功能开关
                /*if ("0".equals(Enviroment.IOS_STATE) && head == null) {
                    ResultMap resultMap = new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.HAVE_BEEN_SIGN);
                    List<Object> list = new ArrayList<>();
                    list.add(0);
                    resultMap.setResultData(list);
                    logger.info("IOS显示已签到,关闭签到" + memberId + existSigned.getMemberName());
                    return resultMap;
                }*/
            giveCoins(member.getMemberID());
            ResultMap resultMap = new ResultMap(Enviroment.N_IN);
            List<Object> list = new ArrayList<>();
            logger.info("未签到,可以签到用户" + memberId + existSigned.getMemberName());
            if (checkdateCalendar.before(yesterday) || signed >= 7) {
                //将签到天数归为1,重新开始
                stringMap.put("signdays", 0);
            } else {
                //连续签到
                stringMap.put("signdays", signed);
            }
            stringMap.put("signstatus", Enviroment.N_IN_CODE);
            list.add(stringMap);
            resultMap.setResultData(list);
            return resultMap;
        } else {
            ResultMap resultMap = new ResultMap(Enviroment.HAVE_BEEN_SIGN);
            List<Object> list = new ArrayList<>();
            stringMap.put("signdays", signed);
            stringMap.put("signstatus", Enviroment.HAVE_BEEN_SIGN_CODE);
            list.add(stringMap);
            resultMap.setResultData(list);
            logger.info("已签到,重复签到用户" + memberId + member.getName());
            return resultMap;
        }
    }

    @Override
    public ResultMap isOpen(String memberId) {
        if (judgeOpen(memberId)) {
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "不能开启");
    }

    @Override
    @Transactional
    public ResultMap getTurnProbability(String memberId) {
        if (judgeOpen(memberId)) {
            TurnAnglePojo turnRandom = RandomUtil.getTurnRandom();

            //修改金额
            Account account = new Account();
            Account select = accountService.select(Integer.valueOf(memberId));
            account.setId(select.getId());
            Integer currCoins = select.getCoins();
            account.setCoins(turnRandom.getCoins());
            account.setSignDate(TimeUtil.getTime());
            accountService.updateMemberCoinAndSignDAte(account);

            //生成消费记录
            Charge chargeRecord = new Charge();
            chargeRecord.setChargeDate(TimeUtil.getTime());
            chargeRecord.setCoins(currCoins);
            chargeRecord.setCoinsSum(turnRandom.getCoins());//转盘奖励
            chargeRecord.setInviteCoinsSum(turnRandom.getCoins());
            chargeRecord.setChargeMethod("转盘奖励");
            chargeRecord.setInviteMemberId(Integer.valueOf(memberId));
            chargeRecord.setType(Enviroment.TYPE_INCOME);
            chargeDao.insertInviteChargeHistory(chargeRecord);
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, turnRandom);
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_FAILE_MESSAGE);
    }


    private boolean judgeOpen(String memberId) {
        Account account = accountService.select(Integer.valueOf(memberId));
        Calendar checkdateCalendar = Calendar.getInstance();
        checkdateCalendar.setTime(account.getSignDate());
        //获取今天凌晨时间
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE), 0, 0, 0);
        //获取昨天凌晨时间
        Calendar yesterday = Calendar.getInstance();
        yesterday.set(yesterday.get(Calendar.YEAR), yesterday.get(Calendar.MONTH), yesterday.get(Calendar.DATE) - 1, 0, 0, 0);
        if (checkdateCalendar.before(today)) {
            return true;
        }
        return false;
    }
}
