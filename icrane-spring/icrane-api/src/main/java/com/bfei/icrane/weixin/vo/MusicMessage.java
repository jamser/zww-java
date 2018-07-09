package com.bfei.icrane.weixin.vo;

import lombok.Data;

/**
 * 音乐消息 
 *  
 * @author bruce
 * @date 2018-09-10
 */
@Data
public class MusicMessage extends BaseMessage {

    // 音乐  
    private Music Music;  

}  