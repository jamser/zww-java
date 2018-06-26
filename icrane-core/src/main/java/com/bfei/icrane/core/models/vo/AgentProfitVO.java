package com.bfei.icrane.core.models.vo;

import com.bfei.icrane.core.models.AgentIncome;
import lombok.Data;

import java.util.List;

/**
 * Created by moying on 2018/6/26.
 */
@Data
public class AgentProfitVO {

    private Long todayProfit;

    private Long totalProfit;

    private Long withdrawProfit;

    List<AgentIncomeVO> agentIncomeList;
}
