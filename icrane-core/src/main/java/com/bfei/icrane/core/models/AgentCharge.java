package com.bfei.icrane.core.models;

import java.math.BigDecimal;
import java.util.Date;

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

    private BigDecimal agentSuperIncome;

    private BigDecimal agentOneIncome;

    private BigDecimal agentTwoIncome;

    private BigDecimal agentThreeIncome;

    private Date updateTime;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getAgentSuperId() {
        return agentSuperId;
    }

    public void setAgentSuperId(Integer agentSuperId) {
        this.agentSuperId = agentSuperId;
    }

    public Integer getAgentOneId() {
        return agentOneId;
    }

    public void setAgentOneId(Integer agentOneId) {
        this.agentOneId = agentOneId;
    }

    public Integer getAgentTwoId() {
        return agentTwoId;
    }

    public void setAgentTwoId(Integer agentTwoId) {
        this.agentTwoId = agentTwoId;
    }

    public Integer getAgentThreeId() {
        return agentThreeId;
    }

    public void setAgentThreeId(Integer agentThreeId) {
        this.agentThreeId = agentThreeId;
    }

    public BigDecimal getAgentSuperFee() {
        return agentSuperFee;
    }

    public void setAgentSuperFee(BigDecimal agentSuperFee) {
        this.agentSuperFee = agentSuperFee;
    }

    public BigDecimal getAgentOneFee() {
        return agentOneFee;
    }

    public void setAgentOneFee(BigDecimal agentOneFee) {
        this.agentOneFee = agentOneFee;
    }

    public BigDecimal getAgentTwoFee() {
        return agentTwoFee;
    }

    public void setAgentTwoFee(BigDecimal agentTwoFee) {
        this.agentTwoFee = agentTwoFee;
    }

    public BigDecimal getAgentThreeFee() {
        return agentThreeFee;
    }

    public void setAgentThreeFee(BigDecimal agentThreeFee) {
        this.agentThreeFee = agentThreeFee;
    }

    public BigDecimal getAgentSuperIncome() {
        return agentSuperIncome;
    }

    public void setAgentSuperIncome(BigDecimal agentSuperIncome) {
        this.agentSuperIncome = agentSuperIncome;
    }

    public BigDecimal getAgentOneIncome() {
        return agentOneIncome;
    }

    public void setAgentOneIncome(BigDecimal agentOneIncome) {
        this.agentOneIncome = agentOneIncome;
    }

    public BigDecimal getAgentTwoIncome() {
        return agentTwoIncome;
    }

    public void setAgentTwoIncome(BigDecimal agentTwoIncome) {
        this.agentTwoIncome = agentTwoIncome;
    }

    public BigDecimal getAgentThreeIncome() {
        return agentThreeIncome;
    }

    public void setAgentThreeIncome(BigDecimal agentThreeIncome) {
        this.agentThreeIncome = agentThreeIncome;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}