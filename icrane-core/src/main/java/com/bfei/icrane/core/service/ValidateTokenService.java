package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.MemberToken;

/**
 * Author: lgq Version: 1.1 Date: 2017/09/20 Description: 验证token. Copyright
 * (c) 2017 伴飞网络. All rights reserved.
 */
public interface ValidateTokenService {

    /**
     * 轻验证token
     */
    boolean validataToken(String token);

    boolean validataToken(String token, Integer memberId);

    boolean validataAgentToken(String token, Integer agentId);

    MemberToken selectByMemberId(int memberId);

    int updateToken(MemberToken mtoken);

    boolean validataAgentToken(String token);

}
