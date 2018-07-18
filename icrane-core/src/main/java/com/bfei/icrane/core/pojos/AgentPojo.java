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

    private String nickName;

    private String iconRealPath;

    private Integer level;

    private Long balance;

    private Long balanceDisabled;

    private Long withdraw;

}
