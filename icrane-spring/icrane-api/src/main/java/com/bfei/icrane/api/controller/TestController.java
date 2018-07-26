package com.bfei.icrane.api.controller;

import com.bfei.icrane.core.dao.AccountDao;
import com.bfei.icrane.core.models.Account;
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
   private AccountDao accountDao;

    // 绑定手机

    /**
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test")
    public String Test() throws Exception {
        Account account = accountDao.selectById(4);

//        insetCatchSuccess();
//        dollRoomService.endPlayByCatchCount(4, 215);
//        SystemPref systemPref = systemPrefDao.selectByPrimaryKey("STOCK_NOTIFY");
//        Doll dollOld = dollDao.selectByPrimaryKey(215);
//        AliyunServiceImpl.getInstance().sendSMSForCode(systemPref.getValue(), "蓝澳科技", "SMS_140550044", dollOld.getName());
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
