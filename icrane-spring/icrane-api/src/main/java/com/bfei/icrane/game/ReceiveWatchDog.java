package com.bfei.icrane.game;

import java.io.InputStream;
import java.net.Socket;

import com.bfei.icrane.api.controller.WebSocketController;
import com.bfei.icrane.api.service.impl.LocalMachineServiceImpl;
import com.bfei.icrane.api.service.impl.MachineServiceImpl;
import com.bfei.icrane.common.util.RedisKeyGenerator;
import com.bfei.icrane.common.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveWatchDog implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(ReceiveWatchDog.class);
	private Integer dollId;
	private Integer userId;
	private volatile Boolean running = true;
	private RedisUtil redisUtil = new RedisUtil();
	
	public ReceiveWatchDog(Integer dollId,Integer userId){
		this.dollId = dollId;
		this.userId = userId;
	}
    public void run() {  
    	this.running = MachineServiceImpl.socketRunning.get(this.dollId);
        while(this.running){  
            try {

            	Socket  socket= MachineServiceImpl.machineSocketMap.get(this.dollId);
            	if(socket==null || socket.isClosed()) {return;}
                InputStream in = socket.getInputStream();  
                if(in.available()>0){  
 					/*try {
 							BufferedReader br = new BufferedReader(new InputStreamReader(in));
 							String info = null;
 							while((info=br.readLine())!=null){
 								//System.out.println("我是客户端，服务器说："+info);
 								WebSocketController.sendMessage(info,dollId);
 							}
 					} catch (IOException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 						return;
 					}*/
                	byte[] bytes = new byte[1024];
                	while(true){
    					//读取数据（阻塞）
    					int read = in.read(bytes);
    					if(read != -1){
    						String info =new String(bytes, 0, read);
							logger.info("++++++++++++"+info+"新机器接受指令" + this.dollId);
    						//if(info.indexOf("gotToy")>0) {
    							//redisUtil.setString(RedisKeyGenerator.getUserGameCatch(userId), "1", 60*2);
    			           // }
    						if(LocalMachineServiceImpl.getInstance().onReceived(info,this.userId,dollId)) {
    							//收到ready产生游戏编号
    							WebSocketController webSocket = WebSocketController.webSocketControllerMap.get(dollId);
    							webSocket.sendMessage(info, dollId, webSocket.popMsgFlag);
    						};
    					}else{
    						break;
    					}
    				}
                    /*ObjectInputStream ois = new ObjectInputStream(in);  
                    Object obj = ois.readObject();  
                    //System.out.println("接收：\t"+obj);
                    ObjectAction oa = actionMapping.get(obj.getClass());  
                    oa = oa==null?new DefaultObjectAction():oa;  
                    oa.doAction(obj, MachineServiceImpl.this);  */
                } else {  
                   Thread.sleep(1000);
                }  
            } catch (Exception e) {  
                //e.printStackTrace();  
                this.running = false;
            }   
            
        }  
    }
	public Boolean getRunning() {
		return running;
	}
	public void setRunning(Boolean running) {
		this.running = running;
	}  
    
    
}  
