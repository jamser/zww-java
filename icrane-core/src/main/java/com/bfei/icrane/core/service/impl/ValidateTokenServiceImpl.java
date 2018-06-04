package com.bfei.icrane.core.service.impl;


import com.bfei.icrane.core.dao.AgentTokenMapper;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentToken;
import com.bfei.icrane.core.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfei.icrane.common.util.RedisUtil;
import com.bfei.icrane.core.dao.MemberDao;
import com.bfei.icrane.core.dao.ValidateTokenDao;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.models.MemberToken;
import com.bfei.icrane.core.service.ValidateTokenService;

/**
 * Author: lgq Version: 1.1 Date: 2017/09/20 Description: 验证token. Copyright
 * (c) 2017 伴飞网络. All rights reserved.
 */
@Service("ValidataTokenService")
public class ValidateTokenServiceImpl implements ValidateTokenService {
    @Autowired
    ValidateTokenDao validateTokenDao;
    @Autowired
    MemberDao memberDao;
    RedisUtil redisUtil = new RedisUtil();

    @Autowired
    private AgentTokenMapper agentTokenMapper;

    @Autowired
    private AgentService agentService;

    @Override
    public MemberToken selectByMemberId(int memberId) {
        // TODO Auto-generated method stub
        return validateTokenDao.selectByMemberId(memberId);
    }

    @Override
    public int updateToken(MemberToken mtoken) {
        // TODO Auto-generated method stub
        redisUtil.setString(mtoken.getToken(), String.valueOf(mtoken.getMemberId()), 3600 * 24);
        return validateTokenDao.updateToken(mtoken);
    }

    public boolean validataToken(String token, Integer memberId) {
        if (redisUtil.existsKey(token)) {
            String vMemberId = redisUtil.getString(token);
            if (vMemberId.equals(String.valueOf(memberId))) {
                redisUtil.setString(token, String.valueOf(memberId), 3600 * 24);
                return true;
            } else {
                Member currmember = memberDao.selectById(Integer.parseInt(vMemberId));
                if (currmember != null && currmember.getMemberID().equals(String.valueOf(memberId))) {
                    redisUtil.setString(token, String.valueOf(memberId), 3600 * 24);
                    return true;
                }
                redisUtil.delKey(token);
                return false;
            }
        }
        MemberToken member = validateTokenDao.selectByToken(token);
        if (member != null && String.valueOf(member.getMemberId()).equals(String.valueOf(memberId))) {
            redisUtil.setString(token, String.valueOf(memberId), 3600 * 24);
            return true;
        }
        validateTokenDao.deleteByMemberId(memberId);
        return false;
    }

    @Override
    public boolean validataAgentToken(String token, Integer agentId) {
        if (redisUtil.existsKey(token)) {
            String vAgentId = redisUtil.getString(token);
            if (vAgentId.equals(String.valueOf(agentId))) {
                redisUtil.setString(token, String.valueOf(agentId), 3600 * 24);
                return true;
            } else {
                Agent agent = agentService.selectByPrimaryKey(Integer.parseInt(vAgentId));
                if (agent != null && agent.getAgentId().equals(String.valueOf(agentId))) {
                    redisUtil.setString(token, String.valueOf(agentId), 3600 * 24);
                    return true;
                }
                redisUtil.delKey(token);
                return false;
            }
        }
        AgentToken agentToken = agentTokenMapper.selectByToken(token);
        if (agentToken != null && String.valueOf(agentToken.getAgentId()).equals(String.valueOf(agentId))) {
            redisUtil.setString(token, String.valueOf(agentId), 3600 * 24);
            return true;
        }
        agentTokenMapper.deleteByAgentId(agentId);
        return false;
    }

    public boolean validataToken(String token) {
        if (redisUtil.existsKey(token)) {
            String memberId = redisUtil.getString(token);
            redisUtil.setString(token, memberId, 3600 * 24);
            return true;
        }
        MemberToken member = validateTokenDao.selectByToken(token);
        if (member != null) {
            redisUtil.setString(token, String.valueOf(member.getMemberId()), 3600 * 24);
            return true;
        } else {
            redisUtil.delKey(token);
            return false;
        }
    }

    public boolean validataAgentToken(String token) {
        if (redisUtil.existsKey(token)) {
            String memberId = redisUtil.getString(token);
            redisUtil.setString(token, memberId, 3600 * 24);
            return true;
        }
        AgentToken agentToken = agentTokenMapper.selectByToken(token);
        if (agentToken != null) {
            redisUtil.setString(token, String.valueOf(agentToken.getAgentId()), 3600 * 24);
            return true;
        } else {
            redisUtil.delKey(token);
            return false;
        }
    }
}


