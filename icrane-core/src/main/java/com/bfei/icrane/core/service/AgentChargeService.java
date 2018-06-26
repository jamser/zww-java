package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.models.AgentIncome;
import com.bfei.icrane.core.models.vo.AgentIncomeVO;

import java.util.Date;
import java.util.List;

/**
 * Created by moying on 2018/5/25.
 */
public interface AgentChargeService {
    int deleteByPrimaryKey(Long id);

    int insert(AgentCharge record);

    int insertSelective(AgentCharge record);

    AgentCharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentCharge record);

    int updateByPrimaryKey(AgentCharge record);

    long selectByAgentSuperId(Integer agentId, Integer status, Date date);

    long selectByAgentOneId(Integer agentId, Integer status, Date date);

    long selectByAgentTwoId(Integer agentId, Integer status, Date date);

    long selectByAgentThreeId(Integer agentId, Integer status, Date date);

    List<AgentIncomeVO> selectIncomeByAgent(Agent agent);


    List<AgentCharge> selectByStatus(Integer status);

    int updateStatus(Long id);
}
