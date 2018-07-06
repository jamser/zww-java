package com.bfei.icrane.api.service;

import org.apache.http.client.ClientProtocolException;

public interface LocalMachineService {
	//token验证
	 String  validateToken(String token,String userId) throws ClientProtocolException;
	 
	 boolean standMachine(String userId,Integer dollId);
	 
	 void onOpen(String userId, Integer dollId);
	 
	 String onMessage(boolean newMachineType,String message, Integer userId,Integer dollId,String device);
	 
	 void consumeGame(String token,Integer userId,Integer dollId) throws ClientProtocolException;
	 
	 void historyGame(String token,Integer userId,Integer dollId,Integer state) throws ClientProtocolException;
	 
	 void onClose(String token,Integer userId,Integer dollId) throws ClientProtocolException;
	 //是否补发下抓
	 boolean leaveClaw(Integer userId,Integer dollId);
	 //是否补扣费
	 boolean leaveConsume(Integer userId,Integer dollId);
	 //是否补游戏记录
	 boolean leaveHistory(Integer userId,Integer dollId);
	 //下抓计数
	 void addClaw(Integer userId,Integer dollId);
	 //接受机器指令 并过滤 重复指令
	 boolean onReceived(String info ,Integer userId ,Integer dollId);

	  //机器抓中状态
	  void onDollCatch(Integer dollId,String catchFlag);
}
