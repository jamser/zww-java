package com.bfei.icrane.weixin.vo;

import lombok.Data;

/**
 * 文本消息 
 *  
 * @author bruce
 * @date 2018-05-19
 */
@Data
public class TextMessage extends BaseMessage {

    // 回复的消息内容  
    private String Content;  


}
