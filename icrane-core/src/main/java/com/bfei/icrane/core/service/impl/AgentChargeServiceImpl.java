package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.AgentChargeMapper;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentCharge;
import com.bfei.icrane.core.models.AgentIncome;
import com.bfei.icrane.core.models.vo.AgentIncomeVO;
import com.bfei.icrane.core.service.AgentChargeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moying on 2018/5/25.
 */
@Service("AgentChargeService")
public class AgentChargeServiceImpl implements AgentChargeService {

    @Autowired
    private AgentChargeMapper agentChargeMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return agentChargeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(AgentCharge record) {
        return agentChargeMapper.insert(record);
    }

    @Override
    public int insertSelective(AgentCharge record) {
        return agentChargeMapper.insertSelective(record);
    }

    @Override
    public AgentCharge selectByPrimaryKey(Long id) {
        return agentChargeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AgentCharge record) {
        return agentChargeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AgentCharge record) {
        return agentChargeMapper.updateByPrimaryKey(record);
    }

    @Override
    public long selectByAgentSuperId(Integer agentId, Integer status, Date date) {
        return agentChargeMapper.selectByAgentSuperId(agentId,status,date);
    }

    @Override
    public long selectByAgentOneId(Integer agentId, Integer status, Date date) {
        return agentChargeMapper.selectByAgentOneId(agentId,status,date);
    }

    @Override
    public long selectByAgentTwoId(Integer agentId, Integer status, Date date) {
        return agentChargeMapper.selectByAgentTwoId(agentId,status,date);
    }

    @Override
    public long selectByAgentThreeId(Integer agentId, Integer status, Date date) {
        return agentChargeMapper.selectByAgentThreeId(agentId,status,date);
    }

    @Override
    public  List<AgentIncomeVO> selectIncomeByAgent(Agent agent) {
        switch (agent.getLevel()) {
            case 0:
                return getLists(agentChargeMapper.selectIncomeByAgentSuperId(agent.getId()));
            case 1:
                return getLists(agentChargeMapper.selectIncomeByAgentOneId(agent.getId()));
            case 2:
                return getLists(agentChargeMapper.selectIncomeByAgentTwoId(agent.getId()));
            case 3:
                return getLists(agentChargeMapper.selectIncomeByAgentThreeId(agent.getId()));
        }
        return null;
    }

    private List<AgentIncomeVO> getLists(List<AgentIncome>agentIncomeList) {
        List<AgentIncomeVO> agentIncomeVOList = new ArrayList<>();
        for (AgentIncome agentIncome:agentIncomeList) {
            AgentIncomeVO agentIncomeVO = new AgentIncomeVO();
            agentIncomeVO.setId(agentIncome.getId());
            agentIncomeVO.setAgentIncome(agentIncome.getAgentIncome());
            agentIncomeVO.setCreateTime(agentIncome.getCreateTime());
            agentIncomeVO.setMemberName(agentIncome.getMemberName());
            agentIncomeVO.setNickName(agentIncome.getNickName());
            agentIncomeVO.setPrice(agentIncome.getPrice().multiply(new BigDecimal(100)).longValue());
            agentIncomeVOList.add(agentIncomeVO);
        }
        return agentIncomeVOList;
    }

    @Override
    public List<AgentCharge> selectByStatus(Integer status) {
        return agentChargeMapper.selectByStatus(status);
    }

    @Override
    public int updateStatus(Long id) {
        return agentChargeMapper.updateStatus(id);
    }

}
