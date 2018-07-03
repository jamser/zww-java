package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxPay implements Serializable {

    private static final long serialVersionUID = 1L;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String prepayId;
    private String paySign;
    private String outTradeNo;
    private String mwebUrl;

}
