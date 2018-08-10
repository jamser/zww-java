package com.bfei.icrane.core.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.models.vo.ChannelChargeOrder;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.AgentChargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfei.icrane.core.service.ChargeOrderService;
import org.springframework.transaction.annotation.Transactional;

@Service("chargeOrderService")
@Transactional
public class ChargeOrderServiceImpl implements ChargeOrderService {
    private static final Logger logger = LoggerFactory.getLogger(ChargeOrderService.class);

    @Autowired
    private ChargeDao chargeDao;
    @Autowired
    ChargeOrderDao chargeOrderDao;
    @Autowired
    ChargeRulesDao chargeRulesDao;
    @Autowired
    MemberDao memberDao;
    @Autowired
    MemberChargeComboDao memberChargeComboDao;
    @Autowired
    MemberChargeActivitiDao memberChargeActivitiDao;
    @Autowired
    SystemPrefDao systemPrefDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AgentChargeService agentChargeService;
    @Autowired
    private AgentMapper agentService;

    @Override
    public int createChareOrder(ChargeOrder order) {
        // TODO Auto-generated method stub
        order.setChargeState(0);
        order.setCreateDate(new Date());
        order.setUpdateDate(new Date());
        return chargeOrderDao.insertSelective(order);
    }

    /**
     * 创建订单
     */
    public Integer createChareOrder(int chargeruleid, Double dprice, int memberId, String orderNo) {
        Member member = memberDao.getFirstById(memberId);
        ChargeRules rule = chargeRulesDao.selectByPrimaryKey(chargeruleid);
        Integer firstCharge = member.getFirstCharge() == null ? 0 : member.getFirstCharge();
        if (rule.getChargeType() == 0) {
            //旧首充
            if (firstCharge + 1 > rule.getChargeTimesLimit()) {
                //超过首充限制次数
                return -1;
            }
        }
        if (rule.getChargeType() == 4) {
            //新首充
            if (member.getAccount().getCoinFirstCharge()) {
                //超过首充限制次数
                return -1;
            }
        }
        if (rule.getChargeType() == 1) {
            //时长包 看充值状态
            MemberChargeCombo memberChargeCombo = memberChargeComboDao.selectMemberEffect(memberId, 1);
            if (memberChargeCombo != null) {//已经充过时长包
                return -2;
            }
        }
        ChargeOrder order = new ChargeOrder();
        order.setChargeruleid(chargeruleid);
        order.setPrice(dprice);
        order.setMemberId(memberId);
        order.setOrderNo(orderNo);
        order.setChargeType(rule.getChargeType());
        order.setMemberName(member.getName());//用户名
        order.setChargeName(rule.getChargeName());//充值规则名称
        Account account = member.getAccount();//账户
        order.setCoinsBefore(account.getCoins());
        order.setSuperTicketBefore(account.getSuperTicket());
        if (rule.getChargeType() == 0 || rule.getChargeType() == 4 || rule.getChargeType() == 5) {//普通包或者新首充
            order.setCoinsCharge(rule.getCoinsCharge());
            order.setCoinsOffer(rule.getCoinsOffer());
            Integer coinsAfter = account.getCoins() + rule.getCoinsCharge() + rule.getCoinsOffer();
            order.setCoinsAfter(coinsAfter);
            order.setSuperTicketCharge(rule.getSuperTicketCharge());
            order.setSuperTicketOffer(rule.getSuperTicketOffer());
            Integer SuperTicketAfter = account.getSuperTicket() + rule.getSuperTicketCharge() + rule.getSuperTicketOffer();
            order.setSuperTicketAfter(SuperTicketAfter);
        } else if (rule.getChargeType() == 1 || rule.getChargeType() == 2 || rule.getChargeType() == 3) {//时长包
            order.setCoinsCharge(rule.getCionsFirst());//首充时长 第一次领取
            order.setCoinsOffer(0);
            Integer coinsAfter = account.getCoins() + rule.getCionsFirst();
            order.setCoinsAfter(coinsAfter);
        }
        return createChareOrder(order);
    }

    @Override
    public ChargeOrder orderSuccess(String orderNo, Double fee) {
        try {
            ChargeOrder chargeOrder = chargeOrderDao.selectByOrderNo(orderNo);

            if (chargeOrder == null || chargeOrder.getChargeState() == 1) {
                logger.error("【查询订单】订单不存在 或者 订单已经支付");
                return null;
            }
            //金额是否一致
            if (!MathUtil.equals(fee, chargeOrder.getPrice())) {
                logger.error("【微信支付】异步通知 ，订单金额不一致 ,orderId{}，微信通知金额={}，系统金额={}", orderNo,
                        fee, chargeOrder.getPrice());
                return null;
            }

            //修改订单状态
            chargeOrderDao.orderSuccess(orderNo, new Date());

            //代理收益
            createAgentCharge(chargeOrder);

            //普通礼包修改首充次数
            ChargeOrder order = chargeOrder;
            Integer memberId = order.getMemberId();
            Account baseaccount = accountService.select(memberId);
            Account account = new Account();
            account.setId(baseaccount.getId());
            account.setBitState(baseaccount.getBitState());
            Integer chargeType = order.getChargeType();
            if (chargeType == 0) {//普通礼包
                Member member = memberDao.getMemberById(memberId);
                Integer firstCharge = member.getFirstCharge();
                if (firstCharge == null) {
                    firstCharge = 0;
                }
                member.setFirstCharge(firstCharge + 1);
                memberDao.updateFirstCharge(member);
            }
            if (chargeType == 5) {//普通礼包
                Member member = memberDao.getMemberById(memberId);
                Integer firstCharge = member.getFirstCharge();
                if (firstCharge == null) {
                    firstCharge = 0;
                }
                account.setLover(String.valueOf(chargeOrder.getPrice()));

                logger.info("用户充值丘比特礼包userId={}", memberId);
                member.setFirstCharge(firstCharge + 1);
                memberDao.updateFirstCharge(member);
                accountService.updateAccountLover(account);

            }
            //新首充礼包
            if (chargeType == 4) {
                account.addState(BitStatesUtils.COIN_FIRST_CHARGE);
                accountService.updateBitStatesById(account);
            }
            //时长礼包  加入套餐
            if (chargeType == 1) {
                ChargeRules rule = chargeRulesDao.selectByPrimaryKey(order.getChargeruleid());
                MemberChargeCombo chargeCombo = new MemberChargeCombo();
                chargeCombo.setMemberId(memberId);
                chargeCombo.setMemberName(order.getMemberName());
                chargeCombo.setChargeType(chargeType);
                chargeCombo.setChargeName(rule.getChargeName());
                chargeCombo.setChargeDateLimit(rule.getChargeDateLimit());
                chargeCombo.setChargeDateStart(new Date());
                chargeCombo.setCoinsGive(rule.getCoinsCharge());
                chargeCombo.setMemberState(0);
                chargeCombo.setGiveTimes(0);
                memberChargeComboDao.insertChargeCombo(chargeCombo);
            }
            //新周卡
            if (chargeType == 2) {
                ChargeRules rule = chargeRulesDao.selectByPrimaryKey(order.getChargeruleid());
                MemberChargeCombo memberChargeCombo = memberChargeComboDao.selectByUserIdAndChargeType(memberId, chargeType);
                if (memberChargeCombo == null) {
                    //第一次购买新周卡
                    MemberChargeCombo chargeCombo = new MemberChargeCombo();
                    chargeCombo.setMemberId(memberId);
                    chargeCombo.setMemberName(order.getMemberName());
                    chargeCombo.setChargeType(chargeType);
                    chargeCombo.setChargeName(rule.getChargeName());
                    chargeCombo.setChargeDateLimit(rule.getChargeDateLimit());
                    chargeCombo.setChargeDateStart(new Date());
                    chargeCombo.setCoinsGive(rule.getCoinsCharge());
                    chargeCombo.setMemberState(0);
                    chargeCombo.setGiveTimes(0);
                    memberChargeComboDao.insertChargeCombo(chargeCombo);
                    //设置账户月卡生效状态
                    account.addState(BitStatesUtils.WEEKS_CARD);
                    //设置月卡领取时间为当日,当天不能领取
                    account.setCoins(0);
                    account.setWeeksCardState(new Date());
                    // accountService.updateById(account);
                    accountService.updateMemberSeeksCardState(account);
                } else if (account.getWeeksCardEnd()) {
                    //原来没过期
                    //System.out.println("原来没过期");
                    memberChargeCombo.setCoinsGive(rule.getCoinsCharge());
                    memberChargeCombo.setChargeDateLimit(memberChargeCombo.getChargeDateLimit() + rule.getChargeDateLimit());
                    memberChargeComboDao.uodate(memberChargeCombo);
                } else {
                    //原来过期
                    //System.out.println("原来过期");
                    memberChargeCombo.setCoinsGive(rule.getCoinsCharge());
                    memberChargeCombo.setChargeDateLimit(rule.getChargeDateLimit());
                    memberChargeCombo.setChargeDateStart(new Date());
                    memberChargeCombo.setMemberState(0);
                    memberChargeCombo.setGiveTimes(0);
                    memberChargeComboDao.uodate(memberChargeCombo);
                    account.setCoins(0);
                    account.addState(BitStatesUtils.WEEKS_CARD);
                    account.setWeeksCardState(new Date());
                    //accountService.updateById(account);
                    accountService.updateMemberSeeksCardState(account);
                }
            }
            //新月卡
            if (chargeType == 3) {
                ChargeRules rule = chargeRulesDao.selectByPrimaryKey(order.getChargeruleid());
                MemberChargeCombo memberChargeCombo = memberChargeComboDao.selectByUserIdAndChargeType(memberId, chargeType);
                if (memberChargeCombo == null) {
                    //第一次购买新周卡或者原来已经过期
                    MemberChargeCombo chargeCombo = new MemberChargeCombo();
                    chargeCombo.setMemberId(memberId);
                    chargeCombo.setMemberName(order.getMemberName());
                    chargeCombo.setChargeType(chargeType);
                    chargeCombo.setChargeName(rule.getChargeName());
                    chargeCombo.setChargeDateLimit(rule.getChargeDateLimit());
                    chargeCombo.setChargeDateStart(new Date());
                    chargeCombo.setCoinsGive(rule.getCoinsCharge());
                    chargeCombo.setMemberState(0);
                    chargeCombo.setGiveTimes(0);
                    memberChargeComboDao.insertChargeCombo(chargeCombo);
                    account.setCoins(0);
                    account.addState(BitStatesUtils.MONTH_CARD);
                    account.setMonthCardState(new Date());
                    // accountService.updateById(account);
                    accountService.updateMemberMonthCardState(account);
                } else if (account.getMonthCardend()) {
                    //原来没过期
                    memberChargeCombo.setChargeDateLimit(memberChargeCombo.getChargeDateLimit() + rule.getChargeDateLimit());
                    memberChargeCombo.setCoinsGive(rule.getCoinsCharge());
                    memberChargeComboDao.uodate(memberChargeCombo);
                } else {
                    //原来过期
                    memberChargeCombo.setCoinsGive(rule.getCoinsCharge());
                    memberChargeCombo.setChargeDateLimit(rule.getChargeDateLimit());
                    memberChargeCombo.setChargeDateStart(new Date());
                    memberChargeCombo.setMemberState(0);
                    memberChargeCombo.setGiveTimes(0);
                    memberChargeComboDao.uodate(memberChargeCombo);
                    account.setCoins(0);
                    account.addState(BitStatesUtils.MONTH_CARD);
                    account.setMonthCardState(new Date());
                    accountService.updateMemberMonthCardState(account);
                }
            }
            return order;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void createAgentCharge(ChargeOrder chargeOrder) {
        Member member = memberDao.selectByAgent(chargeOrder.getMemberId());
        BigDecimal price = BigDecimal.valueOf(chargeOrder.getPrice());
        AgentCharge agentCharge = new AgentCharge();
        agentCharge.setOrderId(chargeOrder.getId());
        Agent agent = new Agent();
        agent.setUpdateTime(new Date());
        BigDecimal income;

        if (!StringUtils.isEmpty(member.getAgentSuper()) ||
                !StringUtils.isEmpty(member.getAgentOne()) ||
                !StringUtils.isEmpty(member.getAgentTwo()) ||
                !StringUtils.isEmpty(member.getAgentThree())) {

            Integer time = Integer.valueOf(systemPrefDao.selectByPrimaryKey(Enviroment.BALANCE_CHANGE_TIME).getValue());
            if (time > 0) {
                agentCharge.setStatus(0);
            } else {
                agentCharge.setStatus(1);
            }

            //计算代理收益价格
            if (AgentUtils.isNotNull(member.getAgentSuper())) {
                agentCharge.setAgentSuperId(member.getAgentSuperId());
                agentCharge.setAgentSuperFee(member.getAgentSuper().getFee());
                if (AgentUtils.isNotNull(member.getAgentOne())) {
                    income = (member.getAgentSuper().getFee().subtract(member.getAgentOne().getFee())).multiply(price.multiply(new BigDecimal(100)));
                } else {
                    income = member.getAgentSuper().getFee().multiply(price.multiply(new BigDecimal(100)));
                }
                agentCharge.setAgentSuperIncome(income.longValue());
                agent.setId(member.getAgentSuper().getId());
                if (time <= 0) {
                    agent.setBalance(income.longValue());
                    agentService.updateAgentBalance(agent);
                }
                logger.info("代理 {} ,增加金额 {},订单号 {}", member.getAgentSuperId(), income.longValue(), chargeOrder.getOrderNo());
            }
            if (AgentUtils.isNotNull(member.getAgentOne())) {
                agentCharge.setAgentOneId(member.getAgentOneId());
                agentCharge.setAgentOneFee(member.getAgentOne().getFee());
                if (AgentUtils.isNotNull(member.getAgentTwo())) {
                    income = (member.getAgentOne().getFee().subtract(member.getAgentTwo().getFee())).multiply(price.multiply(new BigDecimal(100)));
                } else {
                    income = member.getAgentOne().getFee().multiply(price.multiply(new BigDecimal(100)));
                }
                agentCharge.setAgentOneIncome(income.longValue());
                agent.setId(member.getAgentOne().getId());
                if (time <= 0) {
                    agent.setBalance(income.longValue());
                    agentService.updateAgentBalance(agent);
                }

                logger.info("代理 {} ,增加金额 {},订单号 {}", member.getAgentOneId(), income.longValue(), chargeOrder.getOrderNo());
            }
            if (AgentUtils.isNotNull(member.getAgentTwo())) {
                agentCharge.setAgentTwoId(member.getAgentTwoId());
                agentCharge.setAgentTwoFee(member.getAgentTwo().getFee());
                if (AgentUtils.isNotNull(member.getAgentThree())) {
                    income = (member.getAgentTwo().getFee().subtract(member.getAgentThree().getFee())).multiply(price.multiply(new BigDecimal(100)));
                } else {
                    income = member.getAgentTwo().getFee().multiply(price.multiply(new BigDecimal(100)));
                }
                agentCharge.setAgentTwoIncome(income.longValue());
                agent.setId(member.getAgentTwo().getId());
                if (time <= 0) {
                    agent.setBalance(income.longValue());
                    agentService.updateAgentBalance(agent);
                }
                logger.info("代理 {} ,增加金额 {},订单号 {}", member.getAgentTwoId(), income.longValue(), chargeOrder.getOrderNo());
            }
            if (AgentUtils.isNotNull(member.getAgentThree())) {
                agentCharge.setAgentThreeId(member.getAgentThreeId());
                agentCharge.setAgentThreeFee(member.getAgentThree().getFee());
                income = member.getAgentThree().getFee().multiply(price.multiply(new BigDecimal(100)));
                agentCharge.setAgentThreeIncome(income.longValue());
                agent.setId(member.getAgentThree().getId());
                if (time <= 0) {
                    agent.setBalance(income.longValue());
                    agentService.updateAgentBalance(agent);
                }
                logger.info("代理 {} ,增加金额 {},订单号 {}", member.getAgentThreeId(), income.longValue(), chargeOrder.getOrderNo());
            }
            agentCharge.setCreateTime(new Date());
            agentCharge.setUpdateTime(new Date());
            agentChargeService.insertSelective(agentCharge);
            logger.info("生成代理收益数据，订单ID {},订单详情 {}", agentCharge.getOrderId(), agentCharge.toString());
        }
    }

    @Override
    public int orderFailure(String orderNo) {
        // 支付失败
        return chargeOrderDao.orderFailure(orderNo, new Date());
    }

    @Override
    public ChargeRules queryRule(Integer ruleId) {
        return chargeRulesDao.selectByPrimaryKey(ruleId);
    }

    @Override
    public PageBean getChargeOrderList(int page, int pageSize) {
        PageBean<Object> pageBean = new PageBean<Object>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = chargeOrderDao.totalCount();
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Object> pageList = new ArrayList<Object>();
        List<ChargeOrder> list = chargeOrderDao.selectChargeOrderList(begin, pageSize);
        for (ChargeOrder chargeOrder : list) {
            //Integer userId = ;
            Member member = memberDao.getAllInfoById(chargeOrder.getMemberId());
            //chargeOrder.setMemberId(Integer.parseInt(member.getMemberID()));
            Map<String, String> map = new HashMap<String, String>();
            if (member != null) {
                map.put("memberId", member.getMemberID());
            }
            map.put("id", String.valueOf(chargeOrder.getId()));
            map.put("orderNo", chargeOrder.getOrderNo());
            map.put("chargeruleid", String.valueOf(chargeOrder.getChargeruleid()));
            map.put("chargeName", chargeOrder.getChargeName());
            map.put("price", String.valueOf(chargeOrder.getPrice()));
            map.put("memberName", chargeOrder.getMemberName());
            map.put("chargeType", String.valueOf(chargeOrder.getChargeType()));
            map.put("chargeState", String.valueOf(chargeOrder.getChargeState()));
            map.put("coinsBefore", String.valueOf(chargeOrder.getCoinsBefore()));
            map.put("coinsAfter", String.valueOf(chargeOrder.getCoinsAfter()));
            map.put("coinsCharge", String.valueOf(chargeOrder.getCoinsCharge()));
            map.put("coinsOffer", String.valueOf(chargeOrder.getCoinsOffer()));
            map.put("createDate", sdf.format(chargeOrder.getCreateDate()));
            map.put("updateDate", sdf.format(chargeOrder.getUpdateDate()));
            pageList.add(map);
        }
        pageBean.setList(pageList);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }


    @Override
    public PageBean selectChargeOrderBy(String memberName, String id, Integer chargeName, Integer
            chargeState, String startTime, String endTime, int page, int pageSize) {
        PageBean<Object> pageBean = new PageBean<Object>();

        Member members = memberDao.selectByMemberID(id);
        Integer userId = null;
        if (members != null) {
            userId = members.getId();
        }
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = chargeOrderDao.totalCount1(memberName, userId, chargeName, chargeState, startTime, endTime);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Object> pageList = new ArrayList<Object>();
        List<ChargeOrder> list = chargeOrderDao.selectChargeOrderBy(memberName, userId, chargeName, chargeState, startTime, endTime, begin, pageSize);
        for (ChargeOrder chargeOrder : list) {
            Member member = memberDao.getAllInfoById(chargeOrder.getMemberId());
            //chargeOrder.setMemberId(Integer.parseInt(member.getMemberID()));
            Map<String, String> map = new HashMap<String, String>();
            if (member != null) {
                map.put("memberId", member.getMemberID());
            }
            map.put("id", String.valueOf(chargeOrder.getId()));
            map.put("orderNo", chargeOrder.getOrderNo());
            map.put("chargeruleid", String.valueOf(chargeOrder.getChargeruleid()));
            map.put("chargeName", chargeOrder.getChargeName());
            map.put("price", String.valueOf(chargeOrder.getPrice()));
            map.put("memberName", chargeOrder.getMemberName());
            map.put("chargeType", String.valueOf(chargeOrder.getChargeType()));
            map.put("chargeState", String.valueOf(chargeOrder.getChargeState()));
            map.put("coinsBefore", String.valueOf(chargeOrder.getCoinsBefore()));
            map.put("coinsAfter", String.valueOf(chargeOrder.getCoinsAfter()));
            map.put("coinsCharge", String.valueOf(chargeOrder.getCoinsCharge()));
            map.put("coinsOffer", String.valueOf(chargeOrder.getCoinsOffer()));

            map.put("createDate", sdf.format(chargeOrder.getCreateDate()));
            map.put("updateDate", sdf.format(chargeOrder.getUpdateDate()));
            pageList.add(map);
        }
        pageBean.setList(pageList);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }

    /**
     * 查询某个用户的充值记录
     *
     * @param page
     * @param pageSize
     * @param userid
     * @return
     */
    @Override
    public PageBean<ChargeOrder> selectChargeOrderByUserid(int page, int pageSize, Integer userid) {
        PageBean<ChargeOrder> pageBean = new PageBean<ChargeOrder>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = chargeOrderDao.totalCountUserid(userid);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<ChargeOrder> list = chargeOrderDao.selectChargeOrderByUserid(userid, begin, pageSize);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }

    @Override
    public Double selectChargeNumByUserid(Integer memberId) {
        return chargeOrderDao.totalPriceUserid(memberId);
    }

    //渠道充值记录
    @Override
    public PageBean<ChannelChargeOrder> selectChannelChargeOrderBy(String lastLoginFrom, String
            registerChannel, String memberName, String memberId, Integer chargerelueid, Integer chargeState, String
                                                                           startTime, String endTime, int page, int pageSize) {
        PageBean<ChannelChargeOrder> pageBean = new PageBean<ChannelChargeOrder>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = chargeOrderDao.totalCountChannel(lastLoginFrom, registerChannel, memberName, memberId, chargerelueid, chargeState, startTime, endTime);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<ChannelChargeOrder> list = chargeOrderDao.selectChannelChargeOrderBy(lastLoginFrom, registerChannel, memberName, memberId, chargerelueid, chargeState, startTime, endTime, begin, pageSize);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }

    @Override
    public Integer selectChannelChargeNum(String lastLoginFrom, String registerChannel, String memberName, String
            memberId, Integer chargerelueid, Integer chargeState, String startTime, String endTime) {
        return chargeOrderDao.selectChannelChargeNum(lastLoginFrom, registerChannel, memberName, memberId, chargerelueid, chargeState, startTime, endTime);
    }

    @Override
    public Double selectChannelChargePrice(String lastLoginFrom, String registerChannel, String memberName, String memberId, Integer chargerelueid, Integer chargeState, String startTime, String endTime) {
        return chargeOrderDao.selectChannelChargePrice(lastLoginFrom, registerChannel, memberName, memberId, chargerelueid, chargeState, startTime, endTime);
    }

    @Override
    public int selectmemberIdByOrder_no(String out_trade_no) {
        return chargeOrderDao.selectmemberIdByOrder_no(out_trade_no);
    }

    @Override
    public Integer insertChargeHistory(Charge charge) {
        try {
            charge.setChargeDate(TimeUtil.getTime());
            chargeDao.updateMemberCount(charge);
            Integer result;
            if (charge.getSuperTicketSum() == null) {
                charge.setSuperTicketSum(0);
            }
            if (charge.getCoinsSum() == null) {
                charge.setCoinsSum(0);
            }
            if (charge.getSuperTicketSum() <= 0 && charge.getCoinsSum() > 0) {//普通金币礼包记录
                result = chargeDao.insertChargeHistory(charge);
                return result;
            }
            if (charge.getSuperTicketSum() > 0 && charge.getCoinsSum() <= 0) {//普通纯钻石礼包记录
                charge.setCoinsSum(charge.getSuperTicketSum());
                charge.setCoins(charge.getSuperTicket());
                charge.setType("s" + charge.getType());
                result = chargeDao.insertChargeHistory(charge);
                return result;
            }
            if (charge.getSuperTicketSum() > 0 && charge.getCoinsSum() > 0) {//混合礼包记录
                result = chargeDao.insertChargeHistory(charge);
                charge.setCoinsSum(charge.getSuperTicketSum());
                charge.setCoins(charge.getSuperTicket());
                charge.setType("s" + charge.getType());
                result = chargeDao.insertChargeHistory(charge);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChargeOrder selectByOrderNo(String orderNo) {
        return chargeOrderDao.selectByOrderNo(orderNo);
    }
}
