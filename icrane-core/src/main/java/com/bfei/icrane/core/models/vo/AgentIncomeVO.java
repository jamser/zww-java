package com.bfei.icrane.core.models.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by moying on 2018/6/26.
 */
@Data
public class AgentIncomeVO {
    private Long id;

    private String memberName;

    private String nickName;

    private Long price;

    private Long agentIncome;

    private Date createTime;
}
