package com.bfei.icrane.api.service.impl;

import com.bfei.icrane.api.service.AgentWithdrawService;
import com.bfei.icrane.core.dao.AgentWithdrawMapper;
import com.bfei.icrane.core.models.agentWithdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moying on 2018/6/6.
 */
@Service("AgentWithdrawService")
public class AgentWithdrawServiceImpl implements AgentWithdrawService {

    @Autowired
    private AgentWithdrawMapper agentWithdrawMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return agentWithdrawMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(agentWithdraw record) {
        return agentWithdrawMapper.insert(record);
    }

    @Override
    public int insertSelective(agentWithdraw record) {
        return agentWithdrawMapper.insertSelective(record);
    }

    @Override
    public agentWithdraw selectByPrimaryKey(Integer id) {
        return agentWithdrawMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(agentWithdraw record) {
        return agentWithdrawMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(agentWithdraw record) {
        return agentWithdrawMapper.updateByPrimaryKey(record);
    }

    @Override
    public Long selectByWithdraw(Integer id) {
        return agentWithdrawMapper.selectByWithdraw(id);
    }
}
