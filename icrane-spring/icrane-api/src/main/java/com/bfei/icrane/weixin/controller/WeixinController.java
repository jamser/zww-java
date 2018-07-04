package com.bfei.icrane.weixin.controller;

import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.models.MemberInfo;

import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.service.OemService;
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

    @Autowired
    private OemService oemService;

    private String host = "http://h5.lanao.fun";


    @RequestMapping("/h5login")
    @ResponseBody
    public void web(HttpServletRequest request, HttpServletResponse response, String code, String state, String
            phoneModel) throws Exception {
        try {
            if (null == state) {
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

            Object resultData = loginService.weChatLogin(request, code, memberId, "wxWeb", "IMEI", phoneModel, chnnerl, agentId).getResultData();
            if (null == resultData) {
                return;
            }
            MemberInfo member = (MemberInfo) resultData;
            Oem oem = oemService.selectByCode(member.getMember().getRegisterChannel());
            if (null != oem) {
                host = oem.getUrl();
            }
            String url = host + "/" + "lanaokj" + "/wxLogin.html?memberId=" + member.getMember().getId() + "&token=" + member.getToken();
            if (StringUtils.isNotEmpty(index)) {
                url += "&index=" + index;
            }
            response.sendRedirect(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}

