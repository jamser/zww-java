package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.pojos.CommentPojo;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by moying on 2018/7/17.
 */
@ServerEndpoint("/webSocket/commentLists/{memberId}/{token}/{dollId}/{name}")
public class CommnetWebsocketContoller {

    private static final Logger log = LoggerFactory.getLogger(CommnetWebsocketContoller.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<CommnetWebsocketContoller> webSocketSet = new CopyOnWriteArraySet<CommnetWebsocketContoller>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "memberId") Integer memberId,
                       @PathParam(value = "dollId") String dollId, @PathParam(value = "token") String token, @PathParam(value = "name") String name) {
        this.session = session;
        webSocketSet.add(this);
        addOnlineCount();
        try {
            sendMessage("评论连接成功");
            CommentPojo commentPojo = new CommentPojo();
            commentPojo.setDollId(Integer.valueOf(dollId));
            commentPojo.setComment("进入房间");
            commentPojo.setUserName(name);
            Gson gson = new Gson();
            sendInfo(gson.toJson(commentPojo));
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
//        log.info("评论有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        try {
//            log.error("评论发生错误");
        } catch (Exception e) {
            log.error("评论关闭连接后处理过程中出现异常", e);
        }
    }


    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     */
    public static void sendInfo(String message) {
        log.info(message);
        for (CommnetWebsocketContoller item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        CommnetWebsocketContoller.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        CommnetWebsocketContoller.onlineCount--;
    }
}
