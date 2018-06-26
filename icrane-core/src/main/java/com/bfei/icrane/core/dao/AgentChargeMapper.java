package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.models.AgentIncome;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AgentChargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AgentCharge record);

    int insertSelective(AgentCharge record);

    AgentCharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AgentCharge record);

    int updateByPrimaryKey(AgentCharge record);

    long selectByAgentSuperId(@Param("agentId")Integer agentId, @Param("status")Integer status, @Param("date")Date date);

    long selectByAgentOneId(@Param("agentId")Integer agentId, @Param("status")Integer status, @Param("date")Date date);

    long selectByAgentTwoId(@Param("agentId")Integer agentId, @Param("status")Integer status, @Param("date")Date date);

    long selectByAgentThreeId(@Param("agentId")Integer agentId, @Param("status")Integer status, @Param("date")Date date);

    List<AgentCharge> selectByStatus(Integer status);

    int updateStatus(Long id);

    List<AgentIncome> selectIncomeByAgentSuperId(Integer agentId);

    List<AgentIncome> selectIncomeByAgentOneId(Integer id);

    List<AgentIncome> selectIncomeByAgentTwoId(Integer id);

    List<AgentIncome> selectIncomeByAgentThreeId(Integer id);

}