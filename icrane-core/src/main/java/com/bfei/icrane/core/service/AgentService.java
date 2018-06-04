package com.bfei.icrane.core.service;

import com.bfei.icrane.core.form.AgentForm;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentToken;
import com.bfei.icrane.core.models.BankInfo;

import java.util.List;

/**
 * Created by moying on 2018/5/25.
 */
public interface AgentService {
    int deleteByPrimaryKey(Integer id);

    int insert(Agent record);

    int insertSelective(Agent record);

    Agent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Agent record);

    int updateByPrimaryKey(Agent record);

    int updateAgentBalance(Agent record);


    int insertBankInfo(BankInfo bankInfo);

    List<BankInfo> getBankInfoList(int agentId);

    Agent selectByUserName(String username);

    AgentToken getAgentLogin(Agent agent);

    int insertAgent(Agent record,AgentForm agentForm);
}
