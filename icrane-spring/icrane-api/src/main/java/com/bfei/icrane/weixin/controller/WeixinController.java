package com.bfei.icrane.weixin.controller;

import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.common.util.WXUtil;
import com.bfei.icrane.core.models.MemberInfo;
import com.bfei.icrane.core.models.XmlMessageEntity;
import com.bfei.icrane.weixin.service.IWeixinService;
import com.sun.org.apache.xpath.internal.SourceTree;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


@Controller
public class WeixinController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private IWeixinService weixinService;


    @RequestMapping(value = "/weixin", method = RequestMethod.GET)
    @ResponseBody
    public String checkSignature(String signature, String timestamp, String nonce, String echostr) {
        return weixinService.checkSignature(signature, timestamp, nonce, echostr);
    }

    @RequestMapping(value = "/miniApps", method = RequestMethod.GET)
    @ResponseBody
    public String checkSignatureMiniApps(String signature, String timestamp, String nonce, String echostr) {
        return weixinService.checkSignature(signature, timestamp, nonce, echostr);
    }

    @RequestMapping(value = "/weixin", method = RequestMethod.POST)
    @ResponseBody
    public XmlMessageEntity handlerMessage(@RequestBody XmlMessageEntity entity) throws Exception {
        try {
            //System.out.println(entity);
            return weixinService.getBackEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping(value = "/miniApps", method = RequestMethod.POST)
    @ResponseBody
    public Object handlerMessageMiniApps(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            String input = null;
            StringBuffer requestBody = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                requestBody.append(input);
            }
            JSONObject jsonObject = JSONObject.fromObject(requestBody.toString());
            return weixinService.getBackEntityMiniApps(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/h5login")
    @ResponseBody
    public void web(HttpServletRequest request, HttpServletResponse response, String code, String state, String
            phoneModel) throws Exception {
        try {
            //request.setCharacterEncoding("UTF-8");
            int endIndex = state.indexOf("-");
            String memberId = "";
            String index = "";
            String chnnerl = state;
            if (endIndex > -1) {
                memberId = state.substring(0, endIndex);
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
            MemberInfo member = (MemberInfo) loginService.weChatLogin(request, code, memberId, "wxWeb", "IMEI", phoneModel, chnnerl).getResultData();
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

