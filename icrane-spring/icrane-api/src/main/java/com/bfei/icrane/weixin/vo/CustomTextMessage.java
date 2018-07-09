package com.bfei.icrane.weixin.vo;

import lombok.Data;

@Data
public class CustomTextMessage extends CustomBaseMessage {
	
	private CustomTextContentMessage text;

}
