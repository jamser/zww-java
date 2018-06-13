package com.bfei.icrane.game.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import com.bfei.icrane.api.service.SystemPrefService;
import com.bfei.icrane.core.models.SystemPref;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bfei.icrane.common.util.HttpClientUtil;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.common.util.TimeUtil;
import com.bfei.icrane.game.RoomSession;
import org.springframework.beans.factory.annotation.Autowired;


public class HeartbeatDetectTask implements Runnable {

    private static final int WAIT_TIMEOUT = 40;//等待60秒还没有从客户端收到消息则断开连接，停止接受消息线程

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatDetectTask.class);

    //private RoomSession roomSession = new RoomSession();

    private RedisUtil redisUtil = new RedisUtil();
    private Integer dollId;
    private Integer memberId;
    private String token = null;
    private String endGameUrl = null;
    private String exitDollRoomUrl = null;
    //private RoomSession roomSession = new RoomSession();
    private boolean heartbeatFlag = true;

    private Date heartbeatDetectTime = new Date();



    public HeartbeatDetectTask(String dollId, String memberId, String token, String endGameUrl, String exitDollRoomUrl) {
        //this.roomSession = roomSession;
        //this.heartbeatFlag = roomSession.isHeartbeatFlag();
        //this.heartbeatDetectTime = roomSession.getHeartbeatDetectTime();
        this.dollId = Integer.parseInt(dollId);
        this.memberId = Integer.parseInt(memberId);
        this.token = token;
        this.endGameUrl = endGameUrl;
        this.exitDollRoomUrl = exitDollRoomUrl;
    }

    @Override
    public void run() {

        try {
            while (this.heartbeatFlag) {
                //如果超过60秒没有收到客户端的消息，则关闭连接，并强制调用结束游戏和退房接口
//                logger.info("房间【" + dollId + "】用户id【" + memberId + "】心跳检测时间" + (WAIT_TIMEOUT - (TimeUtil.getTime().getTime() - heartbeatDetectTime.getTime()) / 1000) + "秒");
                if ((TimeUtil.getTime().getTime() - heartbeatDetectTime.getTime()) > 1000 * WAIT_TIMEOUT) {
                    logger.info("在娃娃机" + dollId + "的用户" + memberId + "已超时" + WAIT_TIMEOUT + "秒，强制关闭连接！");
                    //如果房间楼主还是本用户则调用结束游戏接口
                    if (redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId)) &&
                            redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)).equals(String.valueOf(memberId))) {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("memberId", String.valueOf(memberId)));
                        params.add(new BasicNameValuePair("dollId", String.valueOf(dollId)));
                        params.add(new BasicNameValuePair("token", token));
                        logger.info("在娃娃机" + dollId + "的用户" + memberId + "已超时" + WAIT_TIMEOUT + "秒，强制开始调用结束游戏接口！");
                        HttpClientUtil.getInstance().executeByPOST(endGameUrl, params);
                        logger.info("在娃娃机" + dollId + "的用户" + memberId + "已超时" + WAIT_TIMEOUT + "秒，已强制调用结束游戏接口！");
                        /*List<NameValuePair> params=new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("memberId",String.valueOf(memberId)));
		    			params.add(new BasicNameValuePair("dollId",String.valueOf(dollId)));
		    			params.add(new BasicNameValuePair("token",token));
		    			//String endGameUrl = propFileMgr.getProperty("webapi.endGame");
		    			logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，强制开始调用结束游戏接口！");
		    			HttpClientUtil.getInstance().executeByPOST(endGameUrl, params);
		    			logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，已强制调用结束游戏接口！");
		    			*/
                    }
                    //	logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，强制开始调用结束游戏接口！");
                    //	this.roomSession.endGameMsg();
                    //	logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，已强制调用结束游戏接口！");

                    //如果玩家超时后还在本房间里调用退房接口
                    //if (redisUtil.sIsMember(RedisKeyGenerator.getRoomKey(dollId), String.valueOf(memberId))) {
                        /*List<NameValuePair> params=new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("memberId",String.valueOf(memberId)));
		    			params.add(new BasicNameValuePair("token",token));
		    			//String exitDollRoomUrl = propFileMgr.getProperty("webapi.exitDollRoom");
		    			logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，强制开始调用退房接口！");
		    			HttpClientUtil.getInstance().executeByPOST(exitDollRoomUrl, params);
		    			logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，已强制调用退房接口！");
		    			*/
                    //	logger.info("在娃娃机"+dollId+"的用户"+memberId+"已超时"+WAIT_TIMEOUT+"秒，强制开始调用退房接口！");
                    //	this.roomSession.exitDollMsg();
                    logger.info("在娃娃机" + dollId + "的用户" + memberId + "已超时" + WAIT_TIMEOUT + "秒，已强制调用退房接口！");
                    //}
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            logger.error("心跳检测过程中出错!", e);
        }
    }

    public boolean isHeartbeatFlag() {
        return heartbeatFlag;
    }

    public void setHeartbeatFlag(boolean heartbeatFlag) {
        this.heartbeatFlag = heartbeatFlag;
    }

    public Date getHeartbeatDetectTime() {
        return heartbeatDetectTime;
    }

    public void setHeartbeatDetectTime(Date heartbeatDetectTime) {
        this.heartbeatDetectTime = heartbeatDetectTime;
    }


}
