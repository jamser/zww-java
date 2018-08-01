package com.bfei.icrane.api.controller;

import com.aliyun.mns.model.Message;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.iot.model.v20170620.PubResponse;
import com.bfei.icrane.api.service.LocalMachineService;
import com.bfei.icrane.api.service.impl.LocalMachineServiceImpl;
import com.bfei.icrane.api.service.impl.MachineServiceImpl;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.core.service.AliyunService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import com.bfei.icrane.game.RoomSession;
import com.bfei.icrane.game.task.HeartbeatDetectTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mwan
 *         Version: 1.0
 *         Date: 2017/9/22
 *         Description: WebSocket控制类.
 *         Copyright (c) 2018 365zww网络. All rights reserved.
 */
@ServerEndpoint("/webSocket/{memberId}/{dollId}/{key}/{device}/{queue}/{token}")
public class WebSocketController {
    public static final boolean printer = false;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    //用来存放每个娃娃机对应的MyWebSocket对象，一台娃娃机只允许一个连接
    private static Map<Integer, Session> webSocketMap;
    public static Map<Integer, WebSocketController> webSocketControllerMap;
    private Integer dollId;
    private Integer userId;
    //用于保存HttpSession与WebSocketSession的映射关系  全局房间session
    public static final Map<Integer, RoomSession> roomSessionMap;

    private AliyunService aliyunService = AliyunServiceImpl.getInstance();

    private LocalMachineService localMachineService = LocalMachineServiceImpl.getInstance();

    //public static ScheduledExecutorService cachedPool = Executors.newScheduledThreadPool(80);
    //ThreadPoolExecutor.DiscardPolicy() 抛弃当前的任务
    // public static ExecutorService cachedPool = new ThreadPoolExecutor(3, 3000,10L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.DiscardPolicy());
    public static ExecutorService cachedPool = new ThreadPoolExecutor(5, 3000, 10L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    //  public static ExecutorService popPool = Executors.newFixedThreadPool(50);

    //private Date heartbeatDetectTime;//心跳检测时间
    //private boolean heartbeatFlag;//心跳检测标记
    private HeartbeatDetectTask heartTask;
    //private Thread popMsgThread;// 接收消息的线程

    public volatile boolean popMsgFlag = false;
    private Date checkMsgDate = null;

    private boolean newMachineType = false;//

    static {
        roomSessionMap = new HashMap<Integer, RoomSession>();
        webSocketMap = new ConcurrentHashMap<Integer, Session>();
        webSocketControllerMap = new ConcurrentHashMap<Integer, WebSocketController>();
    }

    //private RedisUtil redisUtil = new RedisUtil();

    public WebSocketController() {
        onlineCount.getAndIncrement();
    }

    /*
     * 使用@Onopen注解的表示当客户端链接成功后的回掉。参数Session是可选参数
     * 这个Session是WebSocket规范中的会话，表示一次会话。并非HttpSession
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "memberId") String memberId,
                       @PathParam(value = "dollId") String dollId, @PathParam(value = "key") String key,
                       @PathParam(value = "device") String device, @PathParam(value = "queue") String queue,
                       @PathParam(value = "token") String token) throws Exception {

        try {
            logger.info("进来了onOpen。。。。。。。。。。。。。。。。");
            this.dollId = Integer.parseInt(dollId);
            this.userId = Integer.parseInt(memberId);
            //如果已经有人在玩则关闭session
            //String roomHostKey = RedisKeyGenerator.getRoomHostKey(this.dollId);
            if (localMachineService.standMachine(memberId, this.dollId)) {
                session.close();
                return;
            }
            if (webSocketMap.containsKey(this.dollId)) {
                //RoomSession roomSession = roomSessionMap.get(dollId);
                //logger.info("娃娃机" + dollId + "已有人连接");
                session.close();
            } else {
                //  logger.info("将玩家" + memberId + "在房间" + dollId + "的session添加到全局map中");
                //  logger.info("afterConnection接口参数memberId=" + memberId + "," + "dollId=" + dollId + "," + "key=" + key + "," + "device=" + device + "," + "queue=" + queue + "," + "token=" + token);
                //验证token有效性
                //logger.info("调用玩家" + memberId + "的token验证接口");
                String httpResponse = localMachineService.validateToken(token, memberId);
                if (httpResponse.equals("false")) {
                    logger.info("Token验证失败！");
                    try {
                        session.close();
                    } catch (IOException e1) {
                        logger.error("Token验证关闭连接时出现异常", e1);
                    }
                }
                newMachineType = false;
                if (device.indexOf("a") > 0) {
                    newMachineType = true;
                }
                //有效token
                localMachineService.onOpen(memberId, this.dollId);
                // redisUtil.setString(roomHostKey, memberId, 60 * 5);
                // redisUtil.setString(RedisKeyGenerator.getGameResult(this.dollId), "0", 60 * 5);//游戏结果
                webSocketMap.put(this.dollId, session);
                webSocketControllerMap.put(this.dollId, this);
                RoomSession roomSession = new RoomSession();
                // roomSession.setHeartbeatDetectTime(new Date());
                roomSession.setToken(token);
                String nickname = "玩家" + memberId;
                String housename = "娃娃机" + dollId;
                String message = String.format("[%s,%s]", nickname, "开始控制" + housename);
                roomSession.setDollId(this.dollId);
                roomSession.setMemberId(Integer.parseInt(memberId));
                roomSession.setNickname(nickname);
                roomSession.setHousename(housename);
                roomSession.setProductKey(key);
                roomSession.setDevice(device);
                String iotControlTopic = "/" + key + "/" + device + "/control";
                roomSession.setIotControlTopic(iotControlTopic);
                roomSession.setIotStatusQueue(queue);
                //roomSession.setHeartbeatFlag(true);//开始心跳连接检测
                String endGameUrl = LocalMachineServiceImpl.propFileMgr.getProperty("webapi.endGame");
                String exitDollRoomUrl = LocalMachineServiceImpl.propFileMgr.getProperty("webapi.exitDollRoom");
                roomSession.setEndGameUrl(endGameUrl);
                roomSession.setExitDollRoomUrl(exitDollRoomUrl);
                roomSession.setConsumeUrl(LocalMachineServiceImpl.propFileMgr.getProperty("webapi.consumeGame"));
                heartTask = new HeartbeatDetectTask(dollId, memberId, token, endGameUrl, exitDollRoomUrl);
                heartTask.setHeartbeatDetectTime(TimeUtil.getTime());
                roomSessionMap.put(this.dollId, roomSession);
                //开启接受消息的线程
                //popMsgThread = new Thread(new Runnable() {
                //     public void run() {
                //    	ReceivingMessage(roomSession.getIotStatusQueue());
                //     }
                // });
                popMsgFlag = true;
                //popMsgThread.start();
                //开启物联网检测
                cachedPool.submit(heartTask);
                //  cachedPool.scheduleAtFixedRate(heartTask,0,1,TimeUnit.SECONDS);
                if (!newMachineType) {
                    Runnable rb = new Runnable() {
                        @Override
                        public void run() {
                            ReceivingMessage(roomSession.getIotStatusQueue());
                        }
                    };
                    cachedPool.execute(rb);
                }
            }
        } catch (Exception e) {
            logger.error("建立Websocket连接时出错!", e);
        }
    }

    public void ReceivingMessage(String queueName) {
        try {
            logger.info("进来了ReceivingMessage。。。。。。。。。。。。。。。。");
            RoomSession roomSession = roomSessionMap.get(this.dollId);
            Message message = null;
            String messageRawBody = "";
            //logger.info("队列名称:"+queueName+":popMsgFlag:"+popMsgFlag);
            while (popMsgFlag) {
                if (printer) {
                    logger.info("开始从阿里云物联网套件接受到消息popMsgFlag:" + popMsgFlag);
                }
                message = (Message) aliyunService.getMessageMnsQueue(queueName);
                Date enqueueTime = message.getEnqueueTime();
                messageRawBody = message.getMessageBodyAsRawString();
                //logger.info(queueName+":从阿里云物联网套件接受到消息：" + messageRawBody);
                //logger.info("收到的消息时间戳为：" + TimeUtil.formate(enqueueTime));
                //logger.info("转发消息时间基准为：" + TimeUtil.formate(checkMsgDate));
                //logger.info("时间差值为：" + (enqueueTime.getTime() - checkMsgDate.getTime()));
                //if (checkMsgDate == null || enqueueTime.after(checkMsgDate)) {

                //if (checkMsgDate == null || (enqueueTime.getTime() - checkMsgDate.getTime()>=-1000000)) {
                if (messageRawBody != null && !"".equals(messageRawBody)) {
                    //收到ready产生游戏编号
                    //过滤游戏重复指令
                        /*if(messageRawBody.indexOf("gotToy")>0) {
                            localMachineService.onDollCatch(roomSession.getMemberId(),"1");
                        }*/
                    if (localMachineService.onReceived(messageRawBody, userId, dollId)) {
                        sendMessage(messageRawBody, dollId, popMsgFlag);
                    }
                    ;
                }
                // } else {
                //     logger.info("消息时间过早，不转发至手机端。");
                //}
            }
            //return;
        } catch (Exception e) {
            logger.error("从阿里云物联网套件接收消息过程中出错!", e);
        }
    }

    //向所有客户端发送消息
    public synchronized void sendMessage(String info, Integer dollId, boolean popSend) {
        try {
//            logger.info("进来了sendMessage前。。。。。。。。。。。。。。。。" + info);
//				for (Map.Entry<Integer, WebSocketController> entry : webSocketMap.entrySet()) {
//					entry.getValue().session.getBasicRemote().sendText(info);
//					logger.info("向手机端" + entry.getKey() + "转发消息:"+info);
//				}
            if (webSocketMap.get(dollId) != null && webSocketMap.get(dollId).isOpen() && webSocketMap.get(dollId).getBasicRemote() != null && popSend && info != null) {
 /*           	String[] infos = info.split("}");
                final String sText = infos[0] + "}";*/
                logger.info("进来了sendMessage后。。。。。。。。。。。。。。。。" + info);
                webSocketMap.get(dollId).getBasicRemote().sendText(info);
                //logger.info("向在娃娃机" + dollId + "手机端" + roomSessionMap.get(dollId).getMemberId() + "转发消息:" + info);
            } else {
                logger.info("会话已断开，不向在娃娃机" + dollId + "的玩家" + roomSessionMap.get(dollId).getMemberId() + "转发消息:" + info);
            }
        } catch (IOException e) {
            logger.error("向手机端转发消息失败", e);
            //this.popMsgFlag = false;
        }
    }

    /*
     * 使用@OnMessage注解的表示当客户端发送消息后的回掉，第一个参数表示用户发送的数据。参数Session是可选参数，
     * 与OnOpen方法中的session是一致的
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {
//        logger.info("进来了onMessage。。。。。。。。。。。。。。。。" + message);
        // 调用阿里物联网套件接口
        RoomSession roomSession = roomSessionMap.get(this.dollId);
        //Message msg=new Gson().fromJson(message.getPayload().toString(),Message.class);
        //msg.setDate(new Date());
        //sendMessageToUser(msg.getTo(), new TextMessage(new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(msg)));
        // 调用阿里物联网套件接口
        if (printer) {
            logger.info("从手机端接收到消息：" + message);
        }
        String IotMessage = "";
        //heartbeatDetectTime = TimeUtil.getTime();
        checkMsgDate = new Date();//投币后设置时间戳基准
        //heartTask.setHeartbeatDetectTime(new Date());
        //投币命令重置心跳
        heartTask.setHeartbeatDetectTime(TimeUtil.getTime());
        try {
            IotMessage = localMachineService.onMessage(newMachineType, message, roomSession.getMemberId(), dollId, roomSession.getDevice());
//            logger.info("IotMessage：" + IotMessage);
            if ("维修中".equals(IotMessage)) {
                webSocketMap.get(dollId).close();//清除状态
                webSocketMap.remove(dollId);
                webSocketControllerMap.remove(dollId);
                this.popMsgFlag = false;
                return;
            }
            if ("coin".equals(message) && !newMachineType) {
                //投币命令重置心跳uun
                heartTask.setHeartbeatDetectTime(TimeUtil.getTime());
                pubMessageIot(roomSession.getProductKey(), roomSession.getIotControlTopic(), IotMessage);
                IotMessage = "{\"control\":\"right\"}";
                pubMessageIot(roomSession.getProductKey(), roomSession.getIotControlTopic(), IotMessage);
                IotMessage = "{\"control\":\"stop\"}";
                pubMessageIot(roomSession.getProductKey(), roomSession.getIotControlTopic(), IotMessage);
                IotMessage = "";
            }
            if ("".equals(IotMessage)) {
                return;
            }
            pubMessageIot(roomSession.getProductKey(), roomSession.getIotControlTopic(), IotMessage);
        } catch (ServerException e) {
            logger.error("从手机端接收消息过程中出错!", e);
        }
    }

    private void pubMessageIot(String productKey, String iotControlTopic, String message) throws ServerException, ClientException {
        logger.info("==========测试===================productKey：{}，iotControlTopic：{}", productKey, iotControlTopic);
        PubResponse response = (PubResponse) aliyunService.pubMessageIot(productKey, iotControlTopic, message);
        if (response.getSuccess()) {
            //logger.info("向阿里云物联网套件发送消息成功：" + message);
        } else {
            logger.info("向阿里云物联网套件发送消息失败：" + message);
        }

    }

    /*
     * 用户断开链接后的回调，注意这个方法必须是客户端调用了断开链接方法后才会回调
     */
    @OnClose
    public void onClose() {
        logger.info("进来了onClose。。。。。。。。。。。。。。。。");
        RoomSession roomSession = roomSessionMap.get(this.dollId);
        String tokenStr = roomSession.getToken();
        Integer userId = roomSession.getMemberId();
        try {
            onlineCount.decrementAndGet();
            //终止接收消息的线程
            //popMsgFlag = false;
            //roomSession.close();
            //roomSession.setPopMsgFlag(false);
            //logger.info("已停止玩家"+roomSession.getMemberId()+"的接受消息线程");
          /* if (redisUtil.existsKey(RedisKeyGenerator.getMemberClaw(roomSession.getMemberId()))) {
                Integer num = 0 ;
				num = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getMemberClaw(roomSession.getMemberId())));
				if (num==0) {
					roomSession.consumeGame();
				}
			}*/
            //调用结束游戏接口
            //logger.info("在娃娃机" + roomSession.getDollId() + "的用户" + roomSession.getMemberId() + "已断开连接，开始调用结束游戏接口！");
            // roomSession.endGameMsg();
            //logger.info("在娃娃机" + roomSession.getDollId() + "的用户" + roomSession.getMemberId() + "已断开连接，已调用结束游戏接口！");
            //终止心跳检测的线程
            heartTask.setHeartbeatFlag(false);
            // logger.info("已停止玩家" + roomSession.getMemberId() + "的心跳检测线程");
            if (roomSessionMap.containsKey(dollId) && !newMachineType) {
                // logger.info("将玩家" + roomSession.getMemberId() + "在房间" + dollId + "的session从全局map中移除");

                //补下抓
                if (localMachineService.leaveClaw(this.userId, this.dollId)) {
                    String IotMessage = "{\"control\":\"claw\"}";
                    PubResponse response = (PubResponse) aliyunService.pubMessageIot(roomSession.getProductKey(), roomSession.getIotControlTopic(), IotMessage);
                    if (response.getSuccess()) {
                        // logger.info("向阿里云物联网套件发送消息成功：claw");
                        //扣费
                    } else {
                        // logger.info("向阿里云物联网套件发送消息失败：claw");
                    }
                    localMachineService.addClaw(userId, dollId);
                    //logger.info("将玩家" + roomSession.getMemberId() + "在房间" + roomSession.getDollId() + "的session从全局map中移除");
                    String message = String.format("[%s,%s]", roomSession.getNickname(), "结束控制" + roomSession.getHousename());
                    logger.info(message);
                }
                logger.info("roomSessionMap.containsKey(dollId) && !newMachineType");
                localMachineService.consumeGame(tokenStr, userId, dollId);
                localMachineService.historyGame(tokenStr, userId, dollId, 1);
                //结束游戏及时发送下抓
                localMachineService.onClose(tokenStr, userId, dollId);
                popMsgFlag = false;
                roomSessionMap.remove(dollId);
            }

            if (newMachineType && MachineServiceImpl.machineSocketMap.containsKey(this.dollId)) {
                //补下抓
                if (localMachineService.leaveClaw(this.userId, this.dollId)) {
                    MachineServiceImpl.sendMsg(roomSession.getDevice() + "|control|weakClaw", this.dollId, roomSession.getMemberId());
                    localMachineService.addClaw(this.userId, dollId);
                }

                //补游戏记录
                localMachineService.historyGame(tokenStr, userId, dollId, 1);
                localMachineService.onClose(tokenStr, userId, dollId);
                popMsgFlag = false;
                //MachineServiceImpl.machineSocketMap.get(this.dollId).close();
                MachineServiceImpl.machineSocketMap.remove(this.dollId);
            }

            if (webSocketMap.containsKey(dollId)) {
                webSocketMap.remove(dollId);
                webSocketControllerMap.remove(dollId);
            }
        } catch (Exception e) {
            logger.error("关闭连接后处理过程中出现异常", e);
            if (webSocketMap.containsKey(dollId)) {
                try {
                    webSocketMap.get(dollId).close();
                    webSocketMap.remove(dollId);
                    webSocketControllerMap.remove(dollId);
                } catch (IOException e1) {
                    logger.error("关闭连接后处理过程中出现异常", e);
                }

            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {

        try {
            //redisUtil.delKey(RedisKeyGenerator.getRoomHostKey(this.dollId));
            RoomSession roomSession = roomSessionMap.get(this.dollId);
            if (roomSession != null) {
                logger.info("onError...........userId={},dollId={}",userId,dollId);
                String tokenStr = roomSession.getToken();
                Integer userId = roomSession.getMemberId();
                int state = 0;
                // 判断是否补扣费
                if (localMachineService.leaveConsume(this.userId, this.dollId)) {
                    state = 1;
                    //补扣费
                    localMachineService.consumeGame(tokenStr, userId, dollId);
                }


                //补游戏记录
                logger.info("补游戏记录userId={},dollId={},state={}", this.userId,this.dollId,state);
                localMachineService.historyGame(tokenStr, userId, dollId, state);
                localMachineService.exitDollRoom(tokenStr,userId);
            }
            logger.error("session处理过程中出现异常userId={},dollId={}",userId,dollId);
        } catch (Exception e) {
            //heartbeatFlag = false;
            logger.error("关闭连接后处理过程中出现异常", e);
        }
    }
}
