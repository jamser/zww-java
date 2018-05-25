package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.AgentMapper;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moying on 2018/5/25.
 */

@Service("AgentService")
public class AgentServiceImpl implements AgentService {


    @Autowired
    private AgentMapper agentMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return agentMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Agent record) {
        return agentMapper.insert(record);
    }

    @Override
    public int insertSelective(Agent record) {
        return agentMapper.insertSelective(record);
    }

    @Override
    public Agent selectByPrimaryKey(Long id) {
        return agentMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Agent record) {
        return agentMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Agent record) {
        return agentMapper.updateByPrimaryKey(record);
    }
}
