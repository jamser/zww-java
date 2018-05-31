package com.bfei.icrane.common.util;

import com.bfei.icrane.core.models.Agent;

import java.math.BigDecimal;

/**
 * Created by moying on 2018/5/30.
 */
public class AgentUtils {


    //    代理是否为空 费率不为0
    public static boolean isNotNull(Agent member) {
        if (StringUtils.isEmpty(member) || member.getFee().compareTo(new BigDecimal(0)) < 0) {
            return false;
        }
        return true;
    }
}
