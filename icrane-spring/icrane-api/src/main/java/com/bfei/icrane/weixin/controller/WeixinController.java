package com.bfei.icrane.weixin.controller;

import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.models.MemberInfo;
import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.service.OemService;
import com.bfei.icrane.weixin.vo.*;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
public class WeixinController {
    private static final Logger logger = LoggerFactory.getLogger(WeixinController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private OemService oemService;

    @Autowired
    private MemberService memberService;

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

    @RequestMapping("/WeChatLogin")
    @ResponseBody
    public void weChatLogin(HttpServletRequest request, HttpServletResponse response, String code, String state, String
            phoneModel) throws Exception {
        try {
            logger.info("[weChatLogin]code={},state={},phoneModel={}", code, state, phoneModel);
            Object resultData = loginService.weChatLoginFrom(request, code, state, "wxWeb", phoneModel).getResultData();
            if (null == resultData) {
                return;
            }
            MemberInfo member = (MemberInfo) resultData;
            Oem oem = oemService.selectByCode(member.getMember().getRegisterChannel());
            if (null != oem) {
                host = oem.getUrl();
            }
            String url = host + "/" + "lanaokj" + "/wxLogin.html?memberId=" + member.getMember().getId() + "&token=" + member.getToken();
            response.sendRedirect(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    @RequestMapping(value = "weixincoreservlet", method = RequestMethod.GET)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=utf-8");
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        PrintWriter out = response.getWriter();
        if (signature != null &&
                timestamp != null &&
                nonce != null &&
                echostr != null) {
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            } else {
                out.print("微信URL请求验证出错.");
            }
        } else {
            out.print("不是合法的微信URL请求验证.");
        }
        out.close();
    }


    @ResponseBody
    @RequestMapping(value = "weixincoreservlet", method = RequestMethod.POST, produces = "text/html; charset=UTF-8")
    public Object weixin(HttpServletRequest request) {
        Map<String, String> reqMap;
        String xmlStr = null;
        try {
            xmlStr = StringUtils.getRequestData(request);
            reqMap = XmlUtil.xmlToMap(xmlStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("【解析请求出错】" + xmlStr);
            return "fail";
        }
        logger.error("获取微信方茴的信息：" + reqMap);
        // 发送方帐号（一个OpenID）
        String fromUserName = reqMap.get("FromUserName");
        if (StringUtils.isEmpty(fromUserName)) {
            logger.error("fromUserName==null");
            return "fail";
        }
        // 公众帐号 (开发者微信号)
        String toUserName = reqMap.get("ToUserName");

        // 回复文本消息
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(fromUserName);
        textMessage.setFromUserName(toUserName);
        // 两种获取整形时间的方法。
        // 获取到的结果表示当时时间距离1970年1月1日0时0分0秒0毫秒的毫秒数。公众平台api中消息创建时间CreateTime，它表示1970年1月1日0时0分0秒至消息创建时所间隔的秒数，注意是间隔的秒数，不是毫秒数！
        // long longTime1 = System.currentTimeMillis();
        // long longTime2 = new java.util.Date().getTime();
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setFuncFlag(0);
        // 默认返回的文本消息内容
        String respContent = "";

        // 消息类型
        MsgTypeEnum msgType;
        try {
            msgType = MsgTypeEnum.valueOf(reqMap.get("MsgType"));
        } catch (IllegalArgumentException e) {
            logger.error("【不存在的消息类型】" + e.getMessage());
            return "fail";
        }

        switch (msgType) {
            case event:
                return event(reqMap, request);
            case image:
                TextMessage testMessage = new TextMessage();
                testMessage.setToUserName(fromUserName);
                testMessage.setFromUserName(toUserName);
                testMessage.setCreateTime(new Date().getTime());
                testMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
                testMessage.setFuncFlag(0);
                testMessage.setContent("图片已经接收了哦，感谢您的参与！");

                return MessageUtil.textMessageToXml(testMessage);
            case link:
                return "success";
            case location:
                return "success";
            case shortvideo:
                return "success";
            case text:
                return "success";
            case video:
                return "success";
            case voice:
                return "success";
            default:
                break;
        }
        textMessage.setContent(respContent);
        return MessageUtil.textMessageToXml(textMessage);
    }

    /**
     * 事件处理
     *
     * @param reqMap
     * @return
     * @作者 bruce
     * @创建时间 2018年9月22日
     */
    public String event(Map<String, String> reqMap, HttpServletRequest request) {
        EventEnum eventEnum;
        try {
            eventEnum = EventEnum.valueOf(reqMap.get("Event"));
        } catch (IllegalArgumentException e) {
            logger.error("【不存在的事件类型】" + e.getMessage());
            return "fail";
        }

        // 发送方帐号（一个OpenID）
        String fromUserName = reqMap.get("FromUserName");
        // 公众帐号 (开发者微信号)
        String toUserName = reqMap.get("ToUserName");

        String eventKey = reqMap.get("EventKey");

        try {
            //事件类型
            String key = null;
            switch (eventEnum) {
                case SCAN://用户已经关注了
                    key = eventKey;//事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
                    if (StringUtils.isNotEmpty(key)) {
                        insertMember(key, fromUserName, request);//扫描带参数二维码事件处理
                    }
                    break;
                case subscribe://新关注
                    //事件KEY值，qrscene_为前缀，后面为二维码的参数值
                    if (StringUtils.isNotEmpty(eventKey)) {
                        key = eventKey.substring(8);
                        if (StringUtils.isNotEmpty(key)) {
                            insertMember(key, fromUserName, request);//扫描带参数二维码事件处理
                        }
                    } else {//普通关注事件
                    }
                    List<Article> articleList = new ArrayList<>();
                    Article article = new Article();
                    article.setPicUrl("http://oss.lanao.fun/logo/%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20180710091839.jpg");
                    article.setDescription("请点击这里查看详细的操作流程");
                    article.setTitle("网搜抓娃娃操作流程");
                    article.setUrl("http://mmbiz.qpic.cn/mmbiz_jpg/pm2dXw6QpVbnK8kEicnwxTdj2iaOPoY5PtohoYsuu4skeG2rAKE6AIgXr4n8yDWmIN5y17r2Q3hCZckaq7yYASnw/0?wx_fmt=jpeg");
                    NewsMessage newsMessage = new NewsMessage();
                    newsMessage.setToUserName(fromUserName);
                    newsMessage.setFromUserName(toUserName);
                    newsMessage.setCreateTime(new Date().getTime());
                    newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
                    newsMessage.setFuncFlag(0);
                    articleList.add(article);
                    // 设置图文消息个数
                    newsMessage.setArticleCount(articleList.size());
                    // 设置图文消息包含的图文集合
                    newsMessage.setArticles(articleList);
                    // 将图文消息对象转换成xml字符串
                    return MessageUtil.newsMessageToXml(newsMessage);
                case unsubscribe:
                    // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
                    break;

                case CLICK:
                    // 事件KEY值，与创建自定义菜单时指定的KEY值对应
                    break;
                case TEMPLATESENDJOBFINISH:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("【系统错误】", e);
            return "fail";
        }

        return "success";
    }

    @Transactional
    public void insertMember(String state, String wopenid, HttpServletRequest request) throws Exception {
        logger.info("state={},wopenid={}", state, wopenid);
        int endIndex = state.indexOf("-");
        String agentId = "";

        String channel = state;
        if (endIndex > -1) {
            if (state.contains("agent")) {
                agentId = state.substring(0, endIndex).replace("agent", "");
            }
            channel = state.substring(endIndex + 1, state.length());
        }
        endIndex = channel.indexOf("_");
        if (endIndex > -1) {
            channel = channel.substring(0, endIndex);
        }
        logger.info("[insertMember]方法state={},wopenid={},agentId={},channel={}", state, wopenid, agentId, channel);
        Oem oem = oemService.selectByCode(channel);
        if (null == oem) {
            oem = oemService.selectByCode("lanaokj");
        }
        logger.info("insertMember==>{}", oem);
        JSONObject json = WXUtil.getUserInfo(wopenid, oem);
        if (!StringUtils.isEmpty(json) && json.containsKey("unionid")) {
            String unionId = json.getString("unionid");
            //检查add表如果没有就存入
            if (StringUtils.isEmpty(memberService.selectGzhopenIdByUnionId(unionId))) {
                memberService.insertmember_add(wopenid, unionId);
            }
            //unionId登录兼容问题
            String openId = memberService.selectOpenIdByUnionId(unionId);
            if (StringUtils.isEmpty(openId)) {
                openId = wopenid;
            }
            Member member = memberService.selectByOpenId(openId);
            if (member == null) {
                logger.info("微信事件注册新用户");
                //新用户先注册
                member = loginService.weChatRegistered(openId, channel, null, "wxWeb", unionId, agentId, json);
                if (member == null) {
                    IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.REGISTRATION_FAILED);
                }
            }
            logger.info("用户信息: member={}", member);
        } else {
            logger.error("获取用户微信信息出错={}", json);
        }

    }

    @ResponseBody
    @RequestMapping(value = "createMenu", method = RequestMethod.GET)
    protected Object createMenu(HttpServletRequest request) throws ServletException, IOException {
        return WXUtil.createMenu(oemService.selectByCode(request.getParameter("code")));
    }
}

