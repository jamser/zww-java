package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;
@Data
public class Oem {

    private Integer id;

    private String name;

    private String code;

    private String appid;

    private String appsecret;

    private String partner;

    private String partnerKey;

    private String company;

    private String natappUrl;

    private String url;

    private String smsCode;

    private String smsName;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}