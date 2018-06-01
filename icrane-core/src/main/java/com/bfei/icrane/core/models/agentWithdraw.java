package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;
@Data
public class agentWithdraw {
    private Integer id;

    private Integer agentid;

    private Long amount;

    private Long fee;

    private Long actualAmount;

    private String cardNo;

    private String name;

    private String phone;

    private String idCardNo;

    private String tradeNo;

    private Byte status;

    private Date confirmDate;

    private Date createDate;

    private Date updateDate;

    private String remark;

}