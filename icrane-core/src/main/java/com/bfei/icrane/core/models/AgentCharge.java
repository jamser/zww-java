package com.bfei.icrane.core.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AgentCharge {
    private Long id;

    private Long orderId;

    private Integer agentSuperId;

    private Integer agentOneId;

    private Integer agentTwoId;

    private Integer agentThreeId;

    private BigDecimal agentSuperFee;

    private BigDecimal agentOneFee;

    private BigDecimal agentTwoFee;

    private BigDecimal agentThreeFee;

    private Long agentSuperIncome;

    private Long agentOneIncome;

    private Long agentTwoIncome;

    private Long agentThreeIncome;

    private Date updateTime;

    private Date createTime;

    private Integer status;

}