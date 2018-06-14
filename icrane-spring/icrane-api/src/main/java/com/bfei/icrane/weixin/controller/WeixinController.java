package com.bfei.icrane.weixin.controller;

import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.models.MemberInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class WeixinController {
    @Autowired
    private LoginService loginService;
    @RequestMapping("/h5login")
    @ResponseBody
    public void web(HttpServletRequest request, HttpServletResponse response, String code, String state, String
            phoneModel) throws Exception {
        try {
            if(state == null){
                return;
            }
            int endIndex = state.indexOf("-");
            String agentId = "";
            String memberId = "";
            String index = "";
            String chnnerl = state;
            if (endIndex > -1) {
                if (state.contains("agent")) {
                    agentId = state.substring(0, endIndex).replace("agent", "");
                } else {
                    memberId = state.substring(0, endIndex);
                }
                chnnerl = state.substring(endIndex + 1, state.length());
            }
            endIndex = chnnerl.indexOf("_");
            if (endIndex > -1) {
                index = chnnerl.substring(endIndex + 1, chnnerl.length());
                chnnerl = chnnerl.substring(0, endIndex);
            }
            if (StringUtils.isEmpty(phoneModel)) {
                phoneModel = "未知";
            }

            MemberInfo member = (MemberInfo) loginService.weChatLogin(request, code, memberId, "wxWeb", "IMEI", phoneModel, chnnerl, agentId).getResultData();
            String s = "http://h5.lanao.fun/lanaokj/wxLogin.html?memberId=" + member.getMember().getId() + "&token=" + member.getToken();
            if (StringUtils.isNotEmpty(index)) {
                s += "&index=" + index;
            }
            response.sendRedirect(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

