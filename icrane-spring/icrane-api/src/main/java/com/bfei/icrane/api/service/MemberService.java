package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.*;

import java.util.List;
import java.util.Map;

/**
 * @author mwan Version: 1.0 Date: 2017/4/23 Description: 用户Service接口类.
 *         Copyright (c) 2017 mwan. All rights reserved.
 */
public interface MemberService {

    /**
     * 登录验证
     */
    Member login(String mobile, String password);

    /**
     * 更新登录状态
     */
    Integer updateLogin(Member member, MemberToken mtoken, String lastLoginFrom);

    /**
     * 登出
     */
    Integer logoff(Member member, MemberToken mtoken);

    void logoff(Integer memberId);

    /**
     * 验证短信验证码,绑定手机
     */
    ResultMap linkMobile(Integer memberId, String mobile, String smsCode);

    /**
     * 注册
     */
    Integer insertRegister(Member member, PrefSet ps);

    /**
     * 根据手机号查询
     */
    Member selectByMobile(String mobile);

    /**
     * 根据邀请码查询
     */
    Member selectByMemberID(String memberID);

    /**
     * 插入短信验证码
     */
    Integer insertSmsCode(MemberSmscode member);

    /**
     * 验证短信code是否重复
     */
    MemberSmscode validateSmsCodeByCode(String smsCode);

    /**
     * 根据手机号验证短信code是否重复
     */
    MemberSmscode validateSmsCodeByMobile(String mobile);

    /**
     * 删除
     */
    Integer deleteSmscode(String mobile);

    Member selectById(Integer Id);

    /**
     * 修改密码
     */
    int updatePwd(Member mb);

    /*
     * 为用户生成授权token，清除现有的token
     */
    String generateToken(int memberId);

    /**
     * 根据openId查询用户
     */
    Member selectByOpenId(String openId);

    /**
     * 更新token
     */
    int updateToken(MemberToken mt);

    /**
     * 更新登录状态
     */
    Integer updateMember(Member mb, MemberToken mt);

    /**
     * 新增微信用户
     */
    int insertMemberBywx(Member mb);

    /**
     * 插入token
     */
    int insertToken(MemberToken mt);

    /**
     * 更新会员属性，属性非必填
     */
    Integer updateByPrimaryKeySelective(Member member);

    /**
     * 根据memberId查询用户金币数
     */
    Member getUserInfo(Integer memberId);

    /**
     * 查询新用户赠送金币
     */
    SystemPref getSystemPref(String code);

    /**
     * 查询memberID根据mobile
     */
    Member getCodeBymobile(String mobile);

    /**
     * 更新用户unionId信息
     *
     * @param userId  用户userId
     * @param openId  用户openId
     * @param unionId 用户unionId
     */
    void insertUnionId(int userId, String openId, String unionId);

    void destructionToken(Integer memberId);

    void destructionTokenByRedis(Integer memberId);

    /**
     * 获取随机头像昵称
     *
     * @return
     */
    InitializeHeads selectInitializeHeads();

    /**
     * @param mobile
     * @param token
     * @return
     */
    ResultMap sendLinkMobileSMS(String mobile, String token);

    String selectOpenIdByUnionId(String unionId);

    String selectGzhopenId(int memberId);

    String selectGzhopenIdByUnionId(String unionId);

    Boolean isWorker(Integer memberId);

    Boolean isVIP(Integer memberId);

    String[] getSuperUsers();

    List<Integer> selectOutTokenUserId();

    Integer selectRank(int id);

    void insertmember_add(String wopenid, String unionId);

    String verifyChannel(String channel);

    Double rechargeAmount(Integer memberId);

    int updateMemberPhoneModel(Integer memberId, String phoneModel);

    String selectLevelById(int hostId);

    List<Member> getMemberListByAgentId(Integer id, Integer lever);
}