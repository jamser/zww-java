package com.bfei.icrane.core.models;

import java.math.BigDecimal;
import java.util.Date;

public class AgentCharge {
    private Long id;

    private Long orderId;

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