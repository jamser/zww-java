package com.bfei.icrane.weixin.vo;

import lombok.Data;

/**
 * 消息基类（公众帐号 -> 普通用户） 
 *  
 * @author bruce
 * @date 2018-09-10
 */
@Data
public class CustomBaseMessage {

	//用户openid
    private String touser;

    // 消息类型（text/music/news）  
    private String msgtype;
    
}
