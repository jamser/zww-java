package com.bfei.icrane.api.service.impl;

import com.aliyuncs.exceptions.ClientException;
import com.bfei.icrane.api.controller.PushWebsocketContoller;
import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.common.wx.utils.TenpayUtil;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.models.vo.CatchVO;
import com.bfei.icrane.core.pojos.RankListPojo;
import com.bfei.icrane.core.pojos.RankMemberPojo;
import com.bfei.icrane.core.pojos.Rankpojo;
import com.bfei.icrane.core.service.VipService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.Map.Entry;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/27 Description: 娃娃发货或寄存订单业务接口实现.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("DollOrderService")
@Transactional
public class DollOrderServiceImpl implements DollOrderService {
    private static final Logger logger = LoggerFactory.getLogger(DollOrderServiceImpl.class);
    @Autowired
    private DollOrderDao dollOrderDao;
    @Autowired
    private DollOrderItemDao dollOrderItemDao;
    @Autowired
    private DollDao dollDao;
    @Autowired
    DollOrderGoodsDao dollOrderGoodsDao;
    @Autowired
    private SystemPrefDao systemPrefDao;
    @Autowired
    private MemberAddrDao memberAddrDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private VipService vipService;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TDollInfoMapper tDollInfoMapper;
    @Autowired
    private MemberChargeHistoryDao memberChargeHistoryDao;
    RedisUtil redisUtil = new RedisUtil();

    @Override
    public List<DollOrder> selectListByOrderIds(Long[] orderIds) {
        return dollOrderDao.selectListByOrderIds(orderIds);
    }

    @Override
    public DollOrder selectByPrimaryKey(Long id) {
        return dollOrderDao.selectByPrimaryKey(id);
    }

    @Override
    public List<DollOrder> selectListByPrimaryKey(Long[] orderIds) {
        return dollOrderDao.selectListByPrimaryKey(orderIds);
    }

    @Override
    public List<DollOrderItem> selectItemsByMemberId(Integer memberId) {
        /*List<DollOrderItem> dollOrderItems = dollOrderItemDao.selectByMemberId(memberId);
        for (DollOrderItem dollOrderItem : dollOrderItems) {
            if ("申请发货".equals(dollOrderItem.getDollOrder().getStatus())){
                dollOrderItem.getDollOrder().setStatus("待发货");
            }
        }*/
        List<DollOrderItem> dollOrderItems = dollOrderItemDao.selectByMemberId(memberId);
        Vip vip = vipService.selectVipByMemberId(memberId);
        for (Iterator iter = dollOrderItems.iterator(); iter.hasNext(); ) {
            DollOrderItem dollOrderItem = (DollOrderItem) iter.next();
            DollOrder dollOrder = dollOrderItem.getDollOrder();
            if ("寄存中".equals(dollOrder.getStatus()) && dollOrder.getOrderDate().getTime() > new Date("2018/4/9").getTime()) {
                Calendar ca = Calendar.getInstance();
                ca.setTime(dollOrder.getOrderDate());
                ca.add(Calendar.DATE, vip.getCheckTime());
                Date time = ca.getTime();
                Date date = new Date();
                if (time.getTime() < date.getTime()) {
                    iter.remove();
                }
            }
        }
        return dollOrderItems;
    }

    @Override
    public List<DollOrderItem> selectItemsByMemberIdOrderStatus(Integer memberId, String orderStatus) {
        List<DollOrderItem> dollOrderItems = dollOrderItemDao.selectByMemberIdOrderStatus(memberId, orderStatus);
        for (DollOrderItem dollOrderItem : dollOrderItems) {
            String[] split = dollOrderItem.getDollName().split("-");
            if (dollOrderItem.getDollOrder().getLover().equals("131.4")) {
                dollOrderItem.setDollName(split[0] + "(小丘比特)");
            } else if (dollOrderItem.getDollOrder().getLover().equals("199.0")) {
                dollOrderItem.setDollName(split[0] + "(大丘比特)");
            } else {
                dollOrderItem.setDollName(split[0]);
            }

        }
        return dollOrderItems;
    }

    public Integer insertOrder(Integer memberId, Integer dollId, Integer dollNum) {
        Doll doll = dollDao.selectByPrimaryKey(dollId);
        /*if ("3".equals(String.valueOf(doll.getMachineType()))) {
            return 1;
        }*/
        logger.info("insertOrder 参数memberId:{},dollId:{},dollNum:{}", memberId, dollId, dollNum);
        //查询用户默认地址
        MemberAddr memberAddr = memberAddrDao.selectDefaultAddr(memberId);
        //查询系统寄存箱默认
        SystemPref systemPref = systemPrefDao.selectByPrimaryKey("DOLL_STOCK_DAYS");
        SystemPref deliverCoins = systemPrefDao.selectByPrimaryKey("DELIVERY_COINS");
        Integer deliverCoin = Integer.valueOf(deliverCoins.getValue());
        Integer validDate = Integer.valueOf(systemPref.getValue());
        DollOrder dollOrder = new DollOrder();
        DollOrderItem dollOrderItem = new DollOrderItem();
        String orderNum = StringUtils.getOrderNumber();
        if (memberAddr != null) {
            dollOrder.setMemberAddress(memberAddr);
        }
        if (deliverCoin != null) {
            dollOrder.setDeliverCoins(deliverCoin);
        }

        //判断测试人员
        Account account = accountDao.selectById(memberId);

        dollOrder.setDollRedeemCoins(doll.getRedeemCoins());
        dollOrder.setOrderNumber(orderNum);
        dollOrder.setOrderDate(TimeUtil.getTime());
        dollOrder.setOrderBy(memberId);
        dollOrder.setStatus("寄存中");
        dollOrder.setStockValidDate(TimeUtil.plusDay(validDate));
        dollOrder.setLover(account.getLover());
        dollOrderDao.insertOrder(dollOrder);

        dollOrderItem.setDollOrder(dollOrder);
        dollOrderItem.setDoll(doll);
        dollOrderItem.setQuantity(dollNum);
        dollOrderItem.setCreatedDate(TimeUtil.getTime());
        dollOrderItemDao.insert(dollOrderItem);

        if (doll.getPrice() == 0) {
            logger.info("七夕房间抓中userId={},dollId={}", memberId, dollId);
            Account newAccount = new Account();
            newAccount.setId(account.getId());
            newAccount.setLover("0");
            accountDao.updateAccountLover(newAccount);
        }


        if (account.getTester().equals(0) && (doll.getMachineType().equals(0) || doll.getMachineType().equals(3))) {
            TDollInfo tDollInfo = tDollInfoMapper.selectByollCode(doll.getDollID());
            if (StringUtils.isEmpty(tDollInfo)) {
                logger.error("房间ID={}娃娃识别码不对应", doll.getId());
            } else {
//        //更新房间娃娃数量
                TDollInfo dollInfo = new TDollInfo();
                dollInfo.setDollcode(doll.getDollID());
                tDollInfoMapper.updateByDollCode(dollInfo);
                SystemPref systemPref1 = systemPrefDao.selectByPrimaryKey(Enviroment.STOCK_LOWEST_NUM);
                if (tDollInfo.getDolltotal().equals(Integer.valueOf(systemPref1.getValue()))) {
                    logger.info("库存不足={}", tDollInfo.getDolltotal());
                    sendSms(doll, "SMS_140725948");
                }
                if (tDollInfo.getDolltotal() <= 1) {
                    sendSms(doll, "SMS_140550044");
                }
                logger.info("房间={} 减库存", doll.getName());
            }
        }

        //发送抓中通知
        Member member = memberDao.selectById(memberId);
        CatchVO catchVO = new CatchVO();
        catchVO.setUserName(member.getName());
        catchVO.setDollName(doll.getName());
        catchVO.setMemberId(memberId);
        Gson gson = new Gson();
        String s = gson.toJson(catchVO);
        PushWebsocketContoller.sendInfo(s);
        doll.setQuantity(null);
        doll.setMachineStatus(null);
        return dollDao.updateByPrimaryKeySelective(doll);
    }

    public void sendSms(Doll doll, String template) {
        SystemPref systemPref = systemPrefDao.selectByPrimaryKey(Enviroment.STOCK_NOTIFY);
        try {
            AliyunServiceImpl.getInstance().sendSMSForCode(systemPref.getValue(), "蓝澳科技", template, doll.getName());
            Doll dollNew = new Doll();
            dollNew.setId(doll.getId());
            dollNew.setMachineStatus("未上线");
            dollDao.updateClean(dollNew);
            redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(doll.getId()));
            Doll machine = dollDao.selectByPrimaryKey(doll.getId());
            String machineStatus = machine.getMachineStatus();
            if ("维修中".equals(machineStatus) || "维护中".equals(machineStatus) ||
                    "未上线".equals(machineStatus)) {
                redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(doll.getId()), "维修中");
            } else {
                redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(doll.getId()), "空闲中");
            }
            logger.info("dollId={}房间娃娃数量为0，将房间设为未上线", doll.getId());
        } catch (ClientException e) {
            e.printStackTrace();
            logger.error("dollId={}将房间设为未上线失败，原因={}", doll.getId(), e.getMessage());
        }
    }

    @Override
    public int updateByPrimaryKeySelective(DollOrder record) {
        return dollOrderDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public DollOrder selectByOrderIds(Long[] orderIds) {
        return dollOrderDao.selectByOrderIds(orderIds);
    }

    @Override
    public List<DollOrder> selectByOrderNotIn(DollOrder record) {
        return dollOrderDao.selectByOrderNotIn(record);
    }

    @Override
    public List<DollOrderItem> selectByOrderItem(DollOrder record) {
        return dollOrderItemDao.selectByOrderItem(record);
    }

    /**
     * 申请发货
     *
     * @param memberId
     * @param orderIds
     * @param addrId
     * @return
     */
    @Override
    public ResultMap sendOrder(Integer memberId, Long[] orderIds, Integer addrId, String note) {
        //根据抓取订单id查询可发货的寄存娃娃
        List<DollOrder> sendList = dollOrderDao.selectListByOrderIds(orderIds);
        if (sendList == null || sendList.size() == 0) {
            logger.info("申请发货失败:" + Enviroment.SELECT_SENDORDER_FAILED);
            return new ResultMap(Enviroment.FAILE_CODE, Enviroment.SELECT_SENDORDER_FAILED);
        }
        //订单对应娃娃明细
        List<DollOrderItem> items = new ArrayList<>();
        for (DollOrder order : sendList) {
            List<DollOrderItem> item = dollOrderItemDao.getOrderItemByOrderId(order.getId());
            items.addAll(item);
        }
        if (items == null || items.size() == 0) {
            logger.info("申请发货失败:" + Enviroment.SELECT_SENDORDER_ITEMS_FAILED);
            return new ResultMap(Enviroment.FAILE_CODE, Enviroment.SELECT_SENDORDER_ITEMS_FAILED);
        }
        ArrayList<DollOrderItem> realItems = new ArrayList<>();
        Iterator it = items.iterator();
        String dollitemids = "";
        while (it.hasNext()) {
            DollOrderItem s = (DollOrderItem) it.next();
            // 拿这个元素到新集合去找，看有没有
            if (!realItems.contains(s)) {
                realItems.add(s);
                dollitemids += s.getId() + ",";
            }
        }
        Map<String, Integer> map = new HashMap<>();
        //合并 娃娃
        for (DollOrderItem item : realItems) {
            if (map.get(item.getDollCode()) == null || map.get(item.getDollCode()) == 0) {
                map.put(item.getDollCode(), item.getQuantity());
            } else {
                Integer num = map.get(item.getDollCode());
                map.put(item.getDollCode(), num + item.getQuantity());//娃娃数量增加
            }
        }
        String dollsInfo = "";
        for (Entry<String, Integer> entry : map.entrySet()) {
            dollsInfo += entry.getKey() + "*" + entry.getValue() + ";";
        }
        // 获取最大时间的订单id
        DollOrder dollOrderids = dollOrderDao.selectByOrderIds(orderIds);
        //发货订单申请
        DollOrderGoods dollOrderGoods = new DollOrderGoods();
        String orderNum = StringUtils.getOrderNumber();
        dollOrderGoods.setOrderNumber(orderNum);//申请发货订单生成
        dollOrderGoods.setOrderDate(TimeUtil.getTime());
        dollOrderGoods.setMemberId(memberId);
        dollOrderGoods.setStatus("申请发货");
        dollOrderGoods.setStockValidDate(dollOrderids.getStockValidDate());
        dollOrderGoods.setDollitemids(dollitemids);
        dollOrderGoods.setDollsInfo(dollsInfo);
        MemberAddr memberAddr = memberAddrDao.selectByPrimaryKey(addrId);//收货地址
        if (memberAddr == null || !memberId.equals(memberAddr.getMemberId())) {
            memberAddr = memberAddrDao.selectDefaultAddr(memberId);
        }
        dollOrderGoods.setReceiverName(memberAddr.getReceiverName());
        dollOrderGoods.setReceiverPhone(memberAddr.getReceiverPhone());
        dollOrderGoods.setProvince(memberAddr.getProvince());
        dollOrderGoods.setCity(memberAddr.getCity());
        dollOrderGoods.setCounty(memberAddr.getCounty());
        dollOrderGoods.setStreet(memberAddr.getStreet());
        dollOrderGoods.setCreatedDate(TimeUtil.getTime());
        dollOrderGoods.setNote(note);
        //SystemPref systemPref = systemPrefDao.selectByPrimaryKey("DELIVERY_FREE_QT");
//        Vip vip = vipService.selectVipByMemberId(memberId);
//        Integer freeQt = Integer.valueOf(vip.getExemptionPostageNumber());
//        if (orderIds.length >= freeQt
//                //新用户包邮
//                //|| dollOrderGoodsDao.selectByMemberId(memberId).size() < 1
//                ) {
        dollOrderGoods.setDeliverCoins(0);
//        } else {
//            SystemPref deliverCoins = systemPrefDao.selectByPrimaryKey("DELIVERY_COINS");
//            Integer deliverCoin = deliverCoins == null ? 0 : Integer.valueOf(deliverCoins.getValue());
//            dollOrderGoods.setDeliverCoins(deliverCoin);
//            Charge charge = new Charge();
//            Member member = memberDao.selectById(memberId);
//
//            //判断金币是否足够
//            if(deliverCoin>member.getCoins()){
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "您当前金币不满足发货条件哦！！");
//            }
//            charge.setMemberId(memberId);
//            charge.setCoins(member.getCoins());
//            //charge.setCoinsSum(doll.getRedeemCoins() + member.getCoins());
//            charge.setCoinsSum(-deliverCoin);//练习房兑换奖励
//            charge.setChargeDate(TimeUtil.getTime());
//            charge.setType("expense");
//            charge.setChargeMethod("邮费扣除" + deliverCoin);
//            charge.setChargeDate(TimeUtil.getTime());
//            chargeDao.updateMemberCount(charge);
//            chargeDao.insertChargeHistory(charge);
//        }
        Integer result = dollOrderGoodsDao.insertSelective(dollOrderGoods);
        if (result != null && result == 1) {
            Integer integer = dollOrderGoodsDao.selectOrderGoodsIdByDollitemids(dollitemids);
            if (integer != null && integer > 0) {
                logger.info("申请发货成功");
                dollOrderDao.sendDoll(addrId, orderIds);
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                logger.info("申请发货失败:" + Enviroment.CREATE_ORDER_FAILED);
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.CREATE_ORDER_FAILED);
            }
        } else {
            logger.info("申请发货失败:" + Enviroment.CREATE_ORDER_FAILED);
            return new ResultMap(Enviroment.FAILE_CODE, Enviroment.CREATE_ORDER_FAILED);
        }
    }

    /**
     * 发货 处理  已放弃使用
     */
    @Transactional
    public int updateOrderId(DollOrder record, List<DollOrder> dollOrderNotIns, List<DollOrderItem> item,
                             Integer addrId, Integer[] orderIds) {
        if (item == null || item.size() == 0) {
            return 0;
        }
        DollOrderItem dItem = new DollOrderItem();
        for (DollOrderItem dollOrder : item) {
            dItem.setDollOrder(record);
            dItem.setId(dollOrder.getId());
            dollOrderItemDao.updateOrderId(dItem);
        }
        Map<Integer, DollOrderItem> map = new HashMap<Integer, DollOrderItem>();
        // 合并详情
        for (DollOrderItem dollOrderItem : item) {
            // 如果map里不存在 就插入
            if (map.containsKey(dollOrderItem.getDoll().getId()) == false) {
                map.put(dollOrderItem.getDoll().getId(), dollOrderItem);
            } else {
                // 如果map里存在 就把该条数据删除
                DollOrderItem existItem = map.get(dollOrderItem.getDoll().getId());
                existItem.setQuantity(dollOrderItem.getQuantity() + existItem.getQuantity());
                existItem.setCreatedDate(TimeUtil.getTime());
                dollOrderItemDao.updateByPrimaryKeySelective(existItem);
                dollOrderItemDao.deleteByPrimaryKey(dollOrderItem.getId());
            }
        }

        for (DollOrder dollOrderNotIn : dollOrderNotIns) {
            dollOrderDao.deleteByPrimaryKey(dollOrderNotIn.getId());
        }
        DollOrder dOrder = new DollOrder();
        MemberAddr memberAddr = memberAddrDao.selectByPrimaryKey(addrId);//收货地址
        if (memberAddr == null) {
            memberAddr = memberAddrDao.selectDefaultAddr(record.getOrderBy());
        }
        SystemPref systemPref = systemPrefDao.selectByPrimaryKey("DELIVERY_FREE_QT");
        Integer freeQt = Integer.valueOf(systemPref.getValue());
        if (orderIds.length >= freeQt) {
            dOrder.setDeliverCoins(0);
        }
        memberAddr.setId(addrId);
        dOrder.setDeliverDate(TimeUtil.getTime());
        dOrder.setId(record.getId());
        dOrder.setStatus("申请发货");
        dOrder.setModifiedDate(TimeUtil.getTime());
//		dOrder.setModifiedBy(dOrder.getOrderBy());
        dOrder.setMemberAddress(memberAddr);
        logger.info("dOrder:{}", dOrder);
        return dollOrderDao.updateByPrimaryKeySelective(dOrder);
    }

    @Override
    public List<DollOrder> selectExpireOrder() {
        return dollOrderDao.selectExpireOrder();
    }

    @Override
    public List<DollOrder> selectOutTimeDolls() {
        return dollOrderDao.selectOutTimeDolls();
    }

    @Override
    public ResultMap beforeSendDoll(Integer memberId, Long[] orderIds) {
        Map<String, Object> map = new HashMap<>();
        //新用户包邮
        /*if (dollOrderGoodsDao.selectByMemberId(memberId).size() < 1) {
            map.put("deliverCoins", 0);
            map.put("details", "新用户包邮");
            return new ResultMap("操作成功", map);
        }*/
        SystemPref deliverCoins = systemPrefDao.selectByPrimaryKey("DELIVERY_COINS");
        Integer deliverCoin = deliverCoins == null ? 0 : Integer.valueOf(deliverCoins.getValue());
        SystemPref systemPref = systemPrefDao.selectByPrimaryKey("DELIVERY_FREE_QT");
        Vip vip = vipService.selectVipByMemberId(memberId);
        //Integer freeQt = Integer.valueOf(systemPref.getValue());
        Integer freeQt = vip.getExemptionPostageNumber();
        if (orderIds.length >= freeQt) {
            map.put("deliverCoins", 0);
            map.put("details", vip.getName() + freeQt + "个以上包邮");
        } else {
            map.put("deliverCoins", deliverCoin);
            map.put("details", "两个起包邮（注意：发货一个需扣" + deliverCoin + "金币");
        }
        return new ResultMap("操作成功", map);
    }

    @Override
    @Cacheable(value = "catchSuccess_", key = "#type+''+#memberId")
    public ResultMap getCatchSuccessRanks(Integer type, Integer memberId) {
        RankListPojo rankListPojo = new RankListPojo();
        Rankpojo userPojo = null;
        List<Rankpojo> rankVOS = new ArrayList<>();
        switch (type) {
            case 1:
                rankVOS = dollOrderItemDao.selectByRankNow(null);
                break;
            case 2:
                rankVOS = dollOrderItemDao.selectByRankWeek(null, TenpayUtil.getMondayDayStr(new Date()), TenpayUtil.getNowDate(new Date()));
                break;
            case 3:
                rankVOS = dollOrderItemDao.selectByRankAll(null);
                break;
            default:
                rankVOS = dollOrderItemDao.selectByRankAll(null);
                break;
        }
        for (int i = 0; i < rankVOS.size(); i++) {
            if (rankVOS.get(i).getMemberId().equals(memberId)) {
                userPojo = new Rankpojo();
                userPojo.setId(i + 1);
                userPojo.setIconRealPath(rankVOS.get(i).getIconRealPath());
                userPojo.setMemberId(rankVOS.get(i).getMemberId());
                userPojo.setNumber(rankVOS.get(i).getNumber());
                userPojo.setSex(rankVOS.get(i).getSex());
                userPojo.setUserName(rankVOS.get(i).getUserName());
            }
        }
        //个人排名
        if (StringUtils.isEmpty(userPojo))

        {
            Member member = memberDao.selectById(memberId);
            userPojo = new Rankpojo();
            List<Rankpojo> rankVOS1 = new ArrayList<>();
            switch (type) {
                case 1:
                    rankVOS1 = dollOrderItemDao.selectByRankNow(memberId);
                    break;
                case 2:
                    rankVOS1 = dollOrderItemDao.selectByRankWeek(memberId, TenpayUtil.getMondayDayStr(new Date()), TenpayUtil.getNowDate(new Date()));
                    break;
                case 3:
                    rankVOS1 = dollOrderItemDao.selectByRankAll(memberId);
                    break;
            }
            userPojo.setIconRealPath(member.getIconRealPath());
            userPojo.setMemberId(member.getId());
            userPojo.setNumber(rankVOS1.get(0).getNumber());
            userPojo.setSex(member.getGender());
            userPojo.setUserName(member.getName());
        }
        rankListPojo.setRankpojo(userPojo);
        rankListPojo.setRankpojos(rankVOS);

        return new

                ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, rankListPojo);

    }

    @Override
    @Cacheable(value = "getCatchSuccessRanksByMember_", key = "#memberId")
    public ResultMap getCatchSuccessRanksByMember(Integer memberId) {
        RankMemberPojo rankMemberPojo = new RankMemberPojo();
        Member member = memberDao.selectById(memberId);
        rankMemberPojo.setIconRealPath(member.getIconRealPath());
        rankMemberPojo.setMemberId(member.getId());
        rankMemberPojo.setSex(member.getGender());
        rankMemberPojo.setUserName(member.getName());

//        rankMemberPojo.setRankWeek(dollOrderItemDao.selectByRankWeek(memberId, TenpayUtil.getMondayDayStr(new Date()), TenpayUtil.getNowDate(new Date())).get(0).getNumber());
//        rankMemberPojo.setRankAll(dollOrderItemDao.selectByRankAll(memberId).get(0).getNumber());
        List<Rankpojo> rankNow = dollOrderItemDao.selectByRankNow(null);
        List<Rankpojo> rankWeek = dollOrderItemDao.selectByRankWeek(null, TenpayUtil.getMondayDayStr(new Date()), TenpayUtil.getNowDate(new Date()));
        List<Rankpojo> rankAll = dollOrderItemDao.selectByRankAll(null);
        for (int i = 0; i < rankNow.size(); i++) {
            if (rankNow.get(i).getMemberId().equals(memberId)) {
                rankMemberPojo.setRankToady(i + 1);
            }
        }
        for (int i = 0; i < rankWeek.size(); i++) {
            if (rankWeek.get(i).getMemberId().equals(memberId)) {
                rankMemberPojo.setRankWeek(i + 1);
            }
        }
        for (int i = 0; i < rankAll.size(); i++) {
            if (rankAll.get(i).getMemberId().equals(memberId)) {
                rankMemberPojo.setRankAll(i + 1);
            }
        }
        return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, rankMemberPojo);
    }

    /**
     * 兑换
     *
     * @param memberId
     * @param orderIds
     */
    @Override
    public ResultMap dollExchange(Integer memberId, Long[] orderIds) {
        //根据抓取订单id查询可发货的寄存娃娃
        List<DollOrder> exchangeList = dollOrderDao.selectListByOrderIds(orderIds);
        if (exchangeList == null || exchangeList.size() == 0) {
            logger.info("兑换失败:" + Enviroment.SELECT_EXCHANGE_FAILED);
            return new ResultMap(Enviroment.FAILE_CODE, Enviroment.SELECT_EXCHANGE_FAILED);
        }
        Account account = accountDao.selectById(memberId);
        for (DollOrder dollOrder : exchangeList) {
           DollOrderItem dollOrderItem = dollOrderItemDao.selectByOrderId(dollOrder.getId());
            int coinBefore = account.getCoins();
            int coin = dollOrder.getDollRedeemCoins();
            int coinAfter = coinBefore + coin;
            //兑换变为金币添加记录
            MemberChargeHistory chargeRecord = new MemberChargeHistory();
            chargeRecord.setChargeDate(new Date());
            chargeRecord.setChargeMethod("房间("+ dollOrderItem.getDollName() +")已兑换成币");
            chargeRecord.setCoins(coin);
            chargeRecord.setPrepaidAmt(0.00);
            chargeRecord.setMemberId(memberId);
            chargeRecord.setType("income");
            chargeRecord.setChargeDate(new Date());
            chargeRecord.setDollId(dollOrderItem.getDollId());
            chargeRecord.setCoinsBefore(coinBefore);
            chargeRecord.setCoinsAfter(coinAfter);
            memberChargeHistoryDao.insertSelective(chargeRecord);
            //账户加币
            account.setCoins(coin);
            accountDao.updateMemberCoin(account);
        }


        int i = dollOrderDao.dollExchange(orderIds);
        if (i > 0) {

        //娃娃状态改为已兑换
        int i =  dollOrderDao.dollExchange(orderIds);
        if(i >0 ){
            logger.info("兑换成功");
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
        } else {
            logger.info("兑换失败:" + Enviroment.UPDATE_ORDER_FAILED);
            return new ResultMap(Enviroment.FAILE_CODE, Enviroment.UPDATE_ORDER_FAILED);
        }
    }
}
