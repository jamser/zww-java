package com.bfei.icrane.core.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Agent {
    private Integer id;

    private String username;

    private String password;

    private String salt;

    private String phone;

    private String nickName;

    private Integer level;

    private Integer status;

    private Integer agentId;

    private Integer agentOneId;

    private Integer agentTwoId;

    private Date createTime;

    private Date updateTime;

    private BigDecimal fee;

    private Long balance;

    private Long balanceDisabled;

    private Long withdraw;

    private Boolean isOem;


}