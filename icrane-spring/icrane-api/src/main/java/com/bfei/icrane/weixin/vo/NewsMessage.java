package com.bfei.icrane.weixin.vo;

import lombok.Data;

import java.util.List;


/** 
 * 文本消息 
 *  
 * @author bruce
 * @date 2018-09-10
 */
@Data
public class NewsMessage extends BaseMessage {

    // 图文消息个数，限制为10条以内
    private int ArticleCount;

    // 多条图文消息信息，默认第一个item为大图  
    private List<Article> Articles;

}
