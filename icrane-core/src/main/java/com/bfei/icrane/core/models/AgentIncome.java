package com.bfei.icrane.core.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by moying on 2018/6/5.
 */
@Data
public class AgentIncome {

    private Long id;

    private String memberName;

    private String nickName;

    private BigDecimal price;

    private Long agentIncome;

    private Date createTime;
}
