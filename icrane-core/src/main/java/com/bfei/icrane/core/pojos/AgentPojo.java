package com.bfei.icrane.core.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by moying on 2018/6/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentPojo {
    private Integer id;

    private String username;

    private String phone;

    private String nickName;

    private Integer level;

    private Integer agentId;

    private Integer agentOneId;

    private Integer agentTwoId;

    private BigDecimal fee;

    private Long balance;

    private Long balanceDisabled;

    private Long withdraw;

}
