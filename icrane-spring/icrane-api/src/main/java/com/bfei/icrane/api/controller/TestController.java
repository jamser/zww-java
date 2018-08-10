package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.ChargeService;
import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.dao.AccountDao;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.ChargeOrder;
import com.bfei.icrane.core.service.ChargeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/28 Description: 用户信息编辑管理控制层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/member/info")
@CrossOrigin
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private DollOrderService dollOrderService;
    @Autowired
    private ChargeOrderService chargeOrderService;
    @Autowired
    private ChargeService chargeService;


    // 绑定手机

    /**
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test")
    public String Test() throws Exception {
        ChargeOrder order = chargeOrderService.orderSuccess("dadb78e8121243f0b25333050a3a1fa9", 199.00);
        Charge charge = new Charge();
        charge.setMemberId(8);
        charge.setCoins(order.getCoinsBefore());
        charge.setCoinsSum(order.getCoinsCharge() + order.getCoinsOffer());
        charge.setPrepaidAmt(order.getPrice());
        charge.setSuperTicket(order.getSuperTicketBefore());
        charge.setSuperTicketSum(order.getSuperTicketCharge() + order.getSuperTicketOffer());
        charge.setType("income");
        charge.setChargeDate(TimeUtil.getTime());
        charge.setChargeMethod("微信充值-" + order.getChargeName());
        Integer result = chargeService.insertChargeHistory(charge);
        return "file_upload_test";
    }

    public static void main(String[] args) {


    }

//    public void insetCatchSuccess() {
//        Doll machine = dollDao.selectByPrimaryKey(215);
//        CatchHistory catchHistory = new CatchHistory();
//        catchHistory.setCatchDate(TimeUtil.getTime());
//        catchHistory.setCatchStatus("抓取成功");
//        catchHistory.setDollId(215);
//        catchHistory.setMemberId(4);
//        String gameNum = StringUtils.getCatchHistoryNum().replace("-", "").substring(0,20);
//        catchHistory.setGameNum(gameNum);
//        catchHistory.setMachineType(machine.getMachineType());
//        catchHistory.setDollCode(machine.getDollID());
//        catchHistory.setDollName(machine.getName());
//        catchHistory.setDollUrl(machine.getTbimgRealPath());
//        catchHistoryDao.insertCatchHistory(catchHistory);
//    }


}
