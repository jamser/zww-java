package com.bfei.icrane.api.controller;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.service.AgentChargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by moying on 2018/6/5.
 */
@Controller
@RequestMapping(value = "/agent")
@CrossOrigin
public class AgentWithdrawController {
    private static final Logger logger = LoggerFactory.getLogger(AgentWithdrawController.class);

    @Autowired
    private AgentChargeService agentChargeService;


    @PostMapping(value = "/incomeLists")
    public ResultMap getIncomeLists(@RequestParam String token, @RequestParam Integer agentId) {

        return null;

    }

}
