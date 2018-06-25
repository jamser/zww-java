package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.models.vo.MemberCatchStatistics;
import com.bfei.icrane.core.models.vo.MemberComplaintAll;
import com.bfei.icrane.core.service.MemberCatchSuccessService;
import com.bfei.icrane.core.service.MemberComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 申诉列表 on 2017-12-03.
 */

@Service("MemberComplaintService")
public class MemberComplaintServiceImpl implements MemberComplaintService {

    @Autowired
    private MemberComplaintDao memberComplaintDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private DollOrderDao dollOrderDao;

    @Autowired
    private MemberAddrDao memberAddrDao;

    @Autowired
    private DollDao dollDao;

    @Autowired
    private DollOrderItemDao dollOrderItemDao;

    @Autowired
    private SystemPrefDao systemPrefDao;

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private AccountDao accountDao;

    @Override
    public PageBean<MemberComplaintAll> getMemberComplaintList(int page, int pageSize, String memberid) {
        PageBean<MemberComplaintAll> pageBean = new PageBean<MemberComplaintAll>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = memberComplaintDao.totalCount(memberid);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<MemberComplaintAll> list = memberComplaintDao.getMemberComplaintList(begin, pageSize, memberid);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }

    @Override
    public PageBean<MemberComplaintAll> getDoneMemberComplaintList(int page, int pageSize, String memberid) {
        PageBean<MemberComplaintAll> pageBean = new PageBean<MemberComplaintAll>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = memberComplaintDao.totalDoneCount(memberid);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<MemberComplaintAll> list = memberComplaintDao.getDoneMemberComplaintList(begin, pageSize, memberid);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }


    @Override
    public Integer updateComplaintResult(Integer id, Integer state, String checkReason) {
        //修改时间
        Date dates = new Date();
        //查询申诉列表
        MemberComplaintAll memberComplaintAll = memberComplaintDao.selectByPrimaryKey(id);

        if (state == -1) {
            return memberComplaintDao.updateComplaintResult(id, state, checkReason, dates);
        } else if (state == 1) {

            if (memberComplaintAll.getCheckState() == 0) {
                Account account = accountDao.selectById(memberComplaintAll.getMemberId());
                Doll doll = dollDao.selectByPrimaryKey(memberComplaintAll.getDollId());
                if(doll != null){
//                   if(doll.getMachineType() == 2){
//                       //生成消费记录 返钻石
//                       Charge chargeRecord = new Charge();
//                       chargeRecord.setChargeDate(TimeUtil.getTime());
//                       chargeRecord.setChargeMethod("申诉返钻石(" + doll.getName() + ")");
//                       chargeRecord.setCoins(account.getCoins());
//                       chargeRecord.setCoinsSum(0);
//                       chargeRecord.setSuperTicket(account.getSuperTicket());
//                       chargeRecord.setSuperTicketSum(doll.getPrice());
//                       chargeRecord.setDollId(doll.getId());
//                       chargeRecord.setMemberId(account.getId());
//                       chargeRecord.setType("sincome");
//                       chargeDao.updateMemberCount(chargeRecord);
//                       chargeDao.insertChargeHistory(chargeRecord);
//                   }else {
                       //生成消费记录 返娃娃币
                       Charge chargeRecord = new Charge();
                       chargeRecord.setChargeDate(TimeUtil.getTime());
                       chargeRecord.setChargeMethod("申诉返币(" + doll.getName() + ")");
                       chargeRecord.setCoins(account.getCoins());
                       chargeRecord.setCoinsSum(doll.getPrice());
                       chargeRecord.setDollId(doll.getId());
                       chargeRecord.setMemberId(account.getId());
                       chargeRecord.setType("income");
                       chargeDao.updateMemberCount(chargeRecord);
                       chargeDao.insertChargeHistory(chargeRecord);
                   }
//                }

            }
            return memberComplaintDao.updateComplaintResult(id, state, checkReason, dates);

        } else if (state == 2) {

            if (memberComplaintAll.getCheckState() == 0) {
                MemberAddr memberAddr = memberAddrDao.selectDefaultAddr(memberComplaintAll.getMemberId());
                SystemPref systemPref = systemPrefDao.selectByPrimaryKey("DOLL_STOCK_DAYS");
                SystemPref deliverCoins = systemPrefDao.selectByPrimaryKey("DELIVERY_COINS");
                Integer deliverCoin = Integer.valueOf(deliverCoins.getValue());
                Integer validDate = Integer.valueOf(systemPref.getValue());
                DollOrder dollOrder = new DollOrder();
                Doll doll = dollDao.selectByPrimaryKey(memberComplaintAll.getDollId());
                DollOrderItem dollOrderItem = new DollOrderItem();
                String orderNum = StringUtils.getOrderNumber();
                while (dollOrderDao.selectByOrderNum(orderNum) != null) {
                    orderNum = StringUtils.getOrderNumber();
                }
                if (memberAddr != null) {
                    dollOrder.setMemberAddress(memberAddr);
                }
                if (deliverCoin != null) {
                    dollOrder.setDeliverCoins(deliverCoin);
                }
                dollOrder.setOrderNumber(orderNum);
                dollOrder.setOrderDate(TimeUtil.getTime());
                dollOrder.setOrderBy(memberComplaintAll.getMemberId());
                dollOrder.setStatus("寄存中");
//                if ("1".equals(String.valueOf(doll.getMachineType()))) {//练习房 直接兑换
//                    dollOrder.setStatus("已兑换");
//                    dollOrder.setDeliverCoins(doll.getRedeemCoins());
//                    Charge charge = new Charge();
//                    Member member = memberDao.selectById(memberComplaintAll.getMemberId());
//                    charge.setMemberId(memberComplaintAll.getMemberId());
//                    charge.setCoins(member.getAccount().getCoins());
//                    charge.setCoinsSum(doll.getRedeemCoins());//练习房兑换奖励
//                    charge.setChargeDate(TimeUtil.getTime());
//                    charge.setType("income");
//                    charge.setDollId(doll.getId());
//                    charge.setChargeMethod("由<练习房申诉," + doll.getDollID() + ">兑换获取");
//                    charge.setChargeDate(TimeUtil.getTime());
//                    chargeDao.updateMemberCount(charge);
//                    chargeDao.insertChargeHistory(charge);
//                }
                dollOrder.setStockValidDate(TimeUtil.plusDay(validDate));
                dollOrderDao.insertOrder(dollOrder);

                dollOrderItem.setDollOrder(dollOrder);
                dollOrderItem.setDoll(doll);
                dollOrderItem.setQuantity(1);
                dollOrderItem.setCreatedDate(TimeUtil.getTime());
                dollOrderItemDao.insert(dollOrderItem);
                dollDao.updateByPrimaryKeySelective(doll);
            }
            return memberComplaintDao.updateComplaintResult(id, state, checkReason, dates);
        } else {
            return memberComplaintDao.updateComplaintResult(id, state, checkReason, dates);
        }
    }

    @Override
    public Integer insert(MemberComplaint complaint) {
        return memberComplaintDao.insert(complaint);
    }

    @Override
    public int selectMemberComplaintByHistoryId(Integer historyId) {
        return memberComplaintDao.selectMemberComplaintByHistoryId(historyId);
    }
}
