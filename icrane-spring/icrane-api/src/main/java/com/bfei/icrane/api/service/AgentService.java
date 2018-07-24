package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.form.AgentForm;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentToken;
import com.bfei.icrane.core.models.BankInfo;
import com.bfei.icrane.core.models.Oem;

import java.util.List;

/**
 * Created by moying on 2018/5/25.
 */
public interface AgentService {
    int deleteByPrimaryKey(Integer id);

    int insert(Agent record);

    int insertSelective(Agent record);

    Agent selectByPrimaryKey(Integer id);

    Agent selectByPhone(String phone);

    int updateByPrimaryKeySelective(Agent record);

    int updateByPrimaryKey(Agent record);

    int updateAgentBalance(Agent record);


    int insertBankInfo(BankInfo bankInfo);

    int updateBankInfo(BankInfo bankInfo);

    BankInfo selectByCardNo(String cardNo);

    List<BankInfo> getBankInfoList(int agentId);

    Agent selectByUserName(String username);

    AgentToken getAgentLogin(Agent agent);

    int insertAgent(Agent record, AgentForm agentForm);

    ResultMap getInviteCount(Integer agentId);

    ResultMap sendPhoneCode(String phone, String message, Agent agent);

    ResultMap getInviteLists(Integer agentId);

    BankInfo selectByBankId(Integer id);

    List<Agent> selectByPhoneLists(String phone);

    void updateAgentProfile(Integer agentId, String imgUrl);

    ResultMap selectByAgent(Integer agentId);

    List<Agent> selectByAll();
}
