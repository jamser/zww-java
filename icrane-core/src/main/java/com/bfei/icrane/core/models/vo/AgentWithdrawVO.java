package com.bfei.icrane.core.models.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by moying on 2018/6/26.
 */
@Data
public class AgentWithdrawVO {

    private Long amount;

    private Long actualAmount;

    private String cardNo;

    private Date createDate;

    private String remark;

    private Integer cardBankType;

    private Byte status;
}
