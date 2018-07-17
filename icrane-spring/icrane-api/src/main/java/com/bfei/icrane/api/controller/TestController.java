package com.bfei.icrane.api.controller;

import com.bfei.icrane.core.models.vo.CatchVO;
import com.google.gson.Gson;
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

    // 绑定手机

    /**
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test")
    public String Test() throws Exception {
//        CatchVO catchVO = new CatchVO();
//        catchVO.setUserName("huan");
//        catchVO.setDollName("毛绒狗，汪汪汪-1005");
//        catchVO.setMemberId(4);
//        Gson gson = new Gson();
//        String s = gson.toJson(catchVO);
//        PushWebsocketContoller.sendInfo(s);
        return "file_upload_test";
    }
}
