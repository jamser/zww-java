package com.bfei.icrane.common.util;

import com.bfei.icrane.core.models.Agent;

import java.math.BigDecimal;

/**
 * Created by moying on 2018/5/30.
 */
public class AgentUtils {

    private static final Integer CODEIMGURL_EXPIRE = 2592000;

    private RedisUtil redisUtil = new RedisUtil();


    //    代理是否为空 费率不为0
    public static boolean isNotNull(Agent member) {
        if (StringUtils.isEmpty(member) || member.getFee().compareTo(new BigDecimal(0)) < 0) {
            return false;
        }
        return true;
    }

    public String getCodeImagUrl(Integer agentId) {
        if (redisUtil.existsKey(Enviroment.AGENT_SHARE, "agent_" + agentId)) {
            return redisUtil.getHashSet(Enviroment.AGENT_SHARE, "agent_" + agentId);
        }
        return null;
    }

    public void setCodeImagUrl(Integer agentId, String value) {
        if (!redisUtil.existsKey(Enviroment.AGENT_SHARE)) {
            redisUtil.addHashSet(Enviroment.AGENT_SHARE, "agent_" + agentId, value, CODEIMGURL_EXPIRE);
        }
        redisUtil.addHashSet(Enviroment.AGENT_SHARE, "agent_" + agentId, value);
    }
}
