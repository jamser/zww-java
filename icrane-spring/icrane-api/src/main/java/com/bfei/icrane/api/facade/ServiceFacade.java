package com.bfei.icrane.api.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.api.service.DollRoomService;
import com.bfei.icrane.common.util.PropFileManager;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.models.CatchHistory;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.Member;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/10/09
 * Description: 统一服务facade层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("ServiceFacade")
public class ServiceFacade {

    @Autowired
    private DollRoomService dollRoomService;
    @Autowired
    private DollOrderService dollOrderService;

    private PropFileManager propFileMgr = new PropFileManager("interface.properties");

    @Deprecated
    @Transactional
    public boolean endRound(Integer dollId, Integer memberId, Integer catchFlag,String state) {
        // TODO Auto-generated method stub

        if (!dollRoomService.endRound(dollId, memberId, catchFlag, state)) {
            return false;
        }
        ;
        //如抓取成功则生成订单
        if (catchFlag > 0) {
            if (dollOrderService.insertOrder(memberId, dollId, 1) <= 0) {
                return false;
            }
            ;
        }
        return true;
    }

    @Transactional
    public boolean endRound(Integer dollId, Integer memberId, Integer catchFlag, String gameNum, String state) {
        // TODO Auto-generated method stub
        if (!dollRoomService.endRound(dollId, memberId, catchFlag, gameNum, state)) {
            return false;
        }
        //如抓取成功则生成订单
//        if (catchFlag > 0) {
//            if (dollOrderService.insertOrder(memberId, dollId, 1) <= 0) {
//                return false;
//            }
//        }
        return true;
    }


    public boolean endPlay(Integer dollId, Integer memberId) {
        // TODO Auto-generated method stub

        if (!dollRoomService.endPlay(dollId, memberId)) {
            return false;
        }
        return true;
    }

    //根据娃娃机id分配socket接口地址，实现负载均衡
    public String getSocketUrl(int dollId) {

        if ((dollId % 2) == 0) {
            //偶数id则调用地址1
            return propFileMgr.getProperty("webapi.webSocket1");
        } else {
            //奇数id则调用地址2
            return propFileMgr.getProperty("webapi.webSocket2");
        }
    }

}
