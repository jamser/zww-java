package com.bfei.icrane.api.service.impl;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import com.bfei.icrane.common.util.PropFileManager;
import com.bfei.icrane.game.KeepAliveWatchDog;
import com.bfei.icrane.game.ReceiveWatchDog;

public class MachineServiceImpl  {

	//private static MachineServiceImpl _instance;
    private static final PropFileManager propFileMgr = new PropFileManager("interface.properties");
	private static String serverIp = propFileMgr.getProperty("machineIp");
	private static Integer port =Integer.parseInt(propFileMgr.getProperty("machinePort"));
	
	public static ExecutorService machineCachedPool =new ThreadPoolExecutor(0, 6000, 0L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
	//private static String serverIp = "172.19.172.240";//内网 
	//private static Integer port = 2345;
	
	public static Map<Integer, Socket> machineSocketMap;
	public static Map<Integer, Boolean> socketRunning;
	 static {
		 machineSocketMap = new ConcurrentHashMap<Integer, Socket>();
		 socketRunning = new ConcurrentHashMap<Integer, Boolean>();
	   }
	/** 
     * 处理服务端发回的对象，可实现该接口。 
     */  
    public static interface ObjectAction{  
        void doAction(Object obj,MachineServiceImpl client);  
    }  
    public static final class DefaultObjectAction implements ObjectAction{  
        public void doAction(Object obj,MachineServiceImpl client) {  
            //System.out.println("处理："+obj.toString());
        }
    }  
    private String message;
    //public static Socket socket;  
   // public static boolean running=false;  
    private static long lastSendTime;  
    private ConcurrentHashMap<Class, ObjectAction> actionMapping = new ConcurrentHashMap<Class,ObjectAction>();  

	/*public static MachineServiceImpl getInstance() {
		if(_instance == null) {
			return (new MachineServiceImpl());
		}else {
			return _instance;
		}
	}*/
	 
	 
	 public static void stop(Integer dollId){  
	        if(machineSocketMap.containsKey(dollId)) {
				machineSocketMap.remove(dollId);
	        }
	   } 
	 
	 /** 
	     * 添加接收对象的处理对象。 
	     * @param cls 待处理的对象，其所属的类。 
	     * @param action 处理过程对象。 
	     */  
	    public void addActionMap(Class<Object> cls,ObjectAction action){  
	        actionMapping.put(cls, action);  
	    }  
	  
	    public static void sendObject(String message,Integer dollId) throws IOException {  
	       // ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); 
	    	 // oos.write(obj);  
		      //  oos.flush();
	    	if(machineSocketMap.containsKey(dollId)){
	    		Socket socket = machineSocketMap.get(dollId);
	    		OutputStream os = socket.getOutputStream();
	    		PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流
	    		pw.write(message);
	    		pw.flush();
	    	}
	    }

	public static void main(String[] args) throws IOException {
		System.out.println("serverIp:" + serverIp + " " + "port:" + port);
		Socket socket = new Socket(serverIp,port);
		System.out.println(socket);
	}


	public static void sendMsg(String msg,Integer dollId,Integer userId) {
		try {
			if(!machineSocketMap.containsKey(dollId)) {
				Socket socket = new Socket(serverIp,port);
				machineSocketMap.put(dollId, socket);
				socketRunning.put(dollId, true);
			} else if(machineSocketMap.get(dollId).isClosed()) {
				machineSocketMap.remove(dollId);
				Socket	socket = new Socket(serverIp,port);
				machineSocketMap.put(dollId, socket);
				socketRunning.put(dollId, true);
			}
		        lastSendTime=System.currentTimeMillis();  
		       // boolean running=true;  
		        KeepAliveWatchDog keep = new KeepAliveWatchDog(msg,dollId); 
		        ReceiveWatchDog receive=new ReceiveWatchDog(dollId,userId); 
		        //Thread keep = new Thread(new KeepAliveWatchDog(msg,dollId));  
		        //Thread receive=new Thread(new ReceiveWatchDog(dollId));  
		       // keep.start();
		       // receive.start();
		        machineCachedPool.submit(keep);
		        machineCachedPool.submit(receive);
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if(machineSocketMap.containsKey(dollId)) {
					machineSocketMap.get(dollId).close();
					machineSocketMap.remove(dollId);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}  
		
	}
	
	
}
