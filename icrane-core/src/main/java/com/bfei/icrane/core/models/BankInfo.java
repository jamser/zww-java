package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

@Data
public class BankInfo {
    public interface SaveValidate { }

    private Integer id;

    private Integer agentId;

    private String cardBank;

    private String cardSubBank;

    private String cardProvince;

    private String cardCity;

    private String cardArea;

    private String cardBankNo;

    private String cardNo;

    private String idCardNo;

    private String name;

    private String phone;

    private String idCardPicturePos;

    private String idCardPictureRev;

    private String idCardPicture;

    private String bankPicturePos;

    private Date createTime;

    private Date updateTime;

    private Integer cardBankType;

}