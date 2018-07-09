package com.bfei.icrane.weixin.controller;

import com.bfei.icrane.api.service.AgentService;
import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.OemMapper;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.models.MemberInfo;

import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.service.OemService;
import com.bfei.icrane.core.service.RiskManagementService;
import com.bfei.icrane.weixin.vo.*;
import com.google.gson.JsonObject;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@Controller
public class WeixinController {
    private static final Logger logger = LoggerFactory.getLogger(WeixinController.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private OemService oemService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private OemMapper oemMapper;
    @Autowired
    private RiskManagementService riskManagementService;

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



    @RequestMapping(value = "weixincoreservlet",method= RequestMethod.GET)
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
        if(signature != null &&
                timestamp != null &&
                nonce != null &&
                echostr != null){
            // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            }else{
                out.print("微信URL请求验证出错.");
            }
        }else{
            out.print("不是合法的微信URL请求验证.");
        }
        out.close();
    }



    @ResponseBody
    @RequestMapping(value = "weixincoreservlet",method=RequestMethod.POST,produces="text/html; charset=UTF-8")
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
        String respContent = "点击查看 <a href=\"\">首页访问</a>";

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
                return event(reqMap,request);
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
     * @作者 bruce
     * @创建时间 2018年9月22日
     * @param reqMap
     * @return
     */
    public String event(Map<String,String> reqMap,HttpServletRequest request){
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
                        insertMember(key, fromUserName,request);//扫描带参数二维码事件处理
                    }
                    List<Article> articleList = new ArrayList<Article>();
                    Article article = new Article();
                    article.setPicUrl("http://mmbiz.qpic.cn/mmbiz_jpg/pm2dXw6QpVbnK8kEicnwxTdj2iaOPoY5PtuLTQgCLghhJNBibzicMEA39Y7DUwR0tRlJD51cYPzSoBmTHciaOGw8KQw/0?wx_fmt=jpeg");
                    article.setDescription("bruce");
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
                case subscribe://新关注
                    //事件KEY值，qrscene_为前缀，后面为二维码的参数值
                    if (StringUtils.isNotEmpty(eventKey)) {
                        key = eventKey.substring(8);
                        if (StringUtils.isNotEmpty(key)) {
                            insertMember(key, fromUserName,request);//扫描带参数二维码事件处理
                        }
                    } else {//普通关注事件
                        return "success";
                    }
                    Image image = new Image();
                    image.setMediaId("PNI789FlvjpUMDO4QGnspbEpY8RVu3xs3P4ng6Np_2A");
                    ImageMessage imageMessage = new ImageMessage();
                    imageMessage.setImage(image);
                    imageMessage.setCreateTime(new Date().getTime());
                    imageMessage.setFromUserName(toUserName);
                    imageMessage.setToUserName(fromUserName);
                    imageMessage.setFuncFlag(0);
                    imageMessage.setMsgType(MessageUtil.REQ_MESSAGE_TYPE_IMAGE);
                    return  MessageUtil.imageMessageToXml(imageMessage);
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
    public void insertMember(String state, String wopenid,HttpServletRequest request)throws  Exception{
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
        Oem oem = oemMapper.selectByCode(channel);
        if (null == oem) {
            oem = oemMapper.selectByCode("lanaokj");
        }
        Agent agent = agentService.selectByPrimaryKey(oem.getId());
        JSONObject json = WXUtil.getUserInfo(wopenid,oem);
        if(!StringUtils.isEmpty(json)){
            String accessToken = json.getString("access_token");
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
            Member  member = memberService.selectByOpenId(openId);
            if (member == null) {
                //新用户先注册
                member = loginService.wxRegistered(openId, channel, null, accessToken, "wxWeb", unionId, agentId);
                if (member == null) {
                    IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.REGISTRATION_FAILED);
                }
            }
            if (null != agent && agent.getPhone().equals(member.getMobile()) && !member.getOpenId().equals(wopenid)) {
                member.setWeixinId(wopenid);
                memberService.updateByOpenId(member);
            }
            int register = riskManagementService.register(member.getId(), "IMEI", HttpClientUtil.getIpAdrress(request));
            if (register != 1) {
                return;
            }
            loginService.wxLogin(member, "wxWeb", channel, null);
        }

    }

    @ResponseBody
    @RequestMapping(value = "ppppp",method= RequestMethod.GET)
    protected Object batchget_material(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
             JSONObject jsonObject =  WXUtil.batchget_material(oemMapper.selectByCode(request.getParameter("lanaokj")));
             return jsonObject.toString();
    }
}

