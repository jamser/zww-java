package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.form.AgentForm;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.AgentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moying on 2018/5/25.
 */

@Service("AgentService")
public class AgentServiceImpl implements AgentService {

    private final String AGENT = "agent_";


    @Autowired
    private AgentMapper agentMapper;

    @Autowired
    private BankInfoMapper bankInfoMapper;

    @Autowired
    private AgentTokenMapper agentTokenMapper;

    @Autowired
    private SystemPrefDao systemPrefDao;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MemberDao memberDao;

    private RedisUtil redisUtil = new RedisUtil();


    @Override
    public int deleteByPrimaryKey(Integer id) {
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
    public Agent selectByPrimaryKey(Integer id) {
        return agentMapper.selectByPrimaryKey(id);
    }

    @Override
    public Agent selectByPhone(String phone) {
        return agentMapper.selectByPhone(phone);
    }

    @Override
    public int updateByPrimaryKeySelective(Agent record) {
        return agentMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Agent record) {
        return agentMapper.updateByPrimaryKey(record);
    }

    @Override
    public int updateAgentBalance(Agent record) {
        return agentMapper.updateAgentBalance(record);
    }

    @Override

    public int insertBankInfo(BankInfo bankInfo) {
        return bankInfoMapper.insertSelective(bankInfo);
    }

    @Override
    public List<BankInfo> getBankInfoList(int agentId) {
        return bankInfoMapper.selectByAgentId(agentId);
    }

    @Override
    public Agent selectByUserName(String username) {
        return agentMapper.selectByUserName(username);
    }

    @Override
    @Transactional
    public AgentToken getAgentLogin(Agent agent) {
        if (redisUtil.existsKey(AGENT + agent.getId())) {
            String token = redisUtil.getString(AGENT + agent.getId());
            redisUtil.delKey(token);
        }
        String token = StringUtils.getAgentToken();
        agentTokenMapper.deleteByAgentId(agent.getId());
        AgentToken agentToken = new AgentToken();
        agentToken.setAgentId(agent.getId());
        agentToken.setToken(token);
        agentTokenMapper.insert(agentToken);
        redisUtil.setString(token, String.valueOf(agentToken.getAgentId()), 3600 * 24);
        redisUtil.setString("agent_" + String.valueOf(agentToken.getAgentId()), token, 3600 * 24);
        return agentToken;

    }

    @Override
    @Transactional
    public int insertAgent(Agent agent, AgentForm agentForm) {
        Agent newAgent = new Agent();
        BeanUtils.copyProperties(agentForm, newAgent);
        AgentEnum agentEnum = AgentEnum.getAgentByCode(agent.getLevel() + 1);
        newAgent.setLevel(agentEnum.getCode());
        newAgent.setFee(new BigDecimal(systemPrefDao.selectByPrimaryKey(agentEnum.getInfo()).getValue()));
        String salt = ToolUtil.getRandomString(5);
        newAgent.setPassword(MD5Utils.md5(agentForm.getPassword().trim(), salt));
        newAgent.setSalt(salt);
        switch (agent.getLevel()) {
            case 0:
                newAgent.setAgentId(agent.getId());
                break;
            case 1:
                newAgent.setAgentId(agent.getAgentId());
                newAgent.setAgentOneId(agent.getId());
                break;
            case 2:
                newAgent.setAgentId(agent.getAgentId());
                newAgent.setAgentOneId(agent.getAgentOneId());
                newAgent.setAgentTwoId(agent.getId());
                break;
            default:
                break;
        }
        SysUser sysUser = new SysUser();
        sysUser.setAccount(newAgent.getUsername());
        sysUser.setPassword(newAgent.getPassword());
        sysUser.setSalt(newAgent.getSalt());
        sysUser.setPhone(newAgent.getPhone());
        sysUser.setName(newAgent.getNickName());
        sysUser.setRoleid("18");
        sysUser.setDeptid(1);
        sysUser.setStatus(1);
        sysUser.setCreatetime(new Date());
        sysUserMapper.insertSelective(sysUser);
        return agentMapper.insertSelective(newAgent);
    }

    @Override
    public ResultMap getInviteCount(Integer agentId) {
        Map<String, Object> hashMap = new HashMap<>();
        int inviteActivity = 0;
        Agent agent = agentMapper.selectByPrimaryKey(agentId);
        List<Member> memberList = memberDao.getMemberListByAgentId(agent.getId(), agent.getLevel());
        for (int i = 0; i < memberList.size(); i++) {
            if (memberList.get(i).isActiveFlg()) {
                inviteActivity++;
            }
        }
        hashMap.put("inviteNumber", memberList.size());
        hashMap.put("activeNumber", inviteActivity);
        hashMap.put("notActiveNumber", memberList.size() - inviteActivity);
        return new ResultMap("获取列表成功", hashMap);
    }
}
