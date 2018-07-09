package com.bfei.icrane.weixin.vo;

/**
 * 微信事件类型
 * @作者   bruce
 * @日期 2018年9月22日
 */
public enum EventEnum {

	/**
	 * 订阅,(订阅 + 扫描带参数二维码事件)
	 */
	subscribe,
	/**
	 * 取消订阅
	 */
	unsubscribe,
	/**
	 * 扫描带参数二维码事件
	 */
	SCAN,
	/**
	 * 上报地理位置事件
	 */
	LOCATION,
	/**
	 * 自定义菜单事件:点击菜单拉取消息时的事件推送
	 */
	CLICK,
	/**
	 * 自定义菜单事件:点击菜单跳转链接时的事件推送
	 */
	VIEW,
	
	/**
	 * 模板消息
	 */
	TEMPLATESENDJOBFINISH,
	
}
