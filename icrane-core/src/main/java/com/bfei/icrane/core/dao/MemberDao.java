package com.bfei.icrane.core.dao;

import java.util.List;

import com.bfei.icrane.core.models.*;
import org.apache.ibatis.annotations.Param;

/**
 * @author mwan Version: 1.0 Date: 2017/9/16 Description: 用户Dao接口类. Copyright
 *         (c) 2017 伴飞网络. All rights reserved.
 */
public interface MemberDao {

    /**
     * 登录
     */
    Member login(String mobile, String password);

    Integer updateLogin(Member member);

    Integer insertToken(MemberToken mtoken);

    /**
     * 登出
     */
    Integer updateLogoff(Member member);

    void updateLogoffBymemberId(Integer memberId);

    /**
     * 删除token
     */
    Integer deleteToken(MemberToken mtoken);

    /**
     * 验证短信code
     */
    MemberSmscode validateSmsCode(String mobile, String smscode);

    /**
     * 注册
     */
    Integer insertRegister(Member member);

    /**
     * 根据手机号查询
     */
    Member selectByMobile(String mobile);

    /**
     * 根据邀请码查询
     */
    Member selectByMemberID(String memberID);

    /**
     * 删除Smscode
     */
    Integer deleteSmscode(String mobile);

    /**
     * 插入短信验证码
     */
    Integer insertSmsCode(MemberSmscode member);

    /**
     * 验证短信code是否重复
     */
    MemberSmscode validateSmsCodeByCode(String smscode);

    /**
     * 根据手机号验证短信code是否重复
     */
    MemberSmscode validateSmsCodeByMobile(String mobile);

    /**
     * 插入设置
     */
    Integer insertPref(PrefSet ps);

    Member selectById(Integer Id);

    Member selectByAgent(Integer Id);

    /**
     * 更改密码
     */
    int updatePwd(Member mb);

    int updateFirstCharge(Member mb);

    /**
     * 根据openId查询
     */
    Member selectByOpenId(String weixinId);

    /**
     * 更新token
     */
    int updateToken(MemberToken mt);

    /**
     * 微信登录更新登录状态
     */
    int updateMember(Member mb);

    /**
     * 新增微信用户
     */
    int insertMemberBywx(Member mb);

    /**
     * 更新用户属性
     */
    int updateByPrimaryKeySelective(Member member);

    /**
     * 根据memberId查询用户金币数
     */
    Member getUserInfoById(Integer memberId);

    int updateInviteStatus(Integer memberId);

    /**
     * 查询memberID根据mobile
     */
    Member getCodeByMobile(String mobile);

    /**
     * 分页查询用户列表
     */
    List<Member> getUserList(@Param("begin") int begin, @Param("pageSize") int pageSize, @Param("name") String name, @Param("memberID") String memberID, @Param("lastLoginFrom") String lastLoginFrom, @Param("registerDate") String registerDate, @Param("endDate") String endDate, @Param("registerChannel") String registerChannel);

    List<Member> getUserList1(@Param("begin") int begin, @Param("pageSize") int pageSize, @Param("name") String name, @Param("memberID") String memberID, @Param("lastLoginFrom") String lastLoginFrom, @Param("registerDate") String registerDate, @Param("endDate") String endDate);

    /**
     * 总记录数
     */
    int totalCount(@Param("name") String name, @Param("memberid") String memberid, @Param("lastLoginFrom") String lastLoginFrom, @Param("registerDate") String registerDate, @Param("endDate") String endDate, @Param("registerChannel") String registerChannel);

    int totalCount1(@Param("name") String name, @Param("memberid") String memberid, @Param("lastLoginFrom") String lastLoginFrom, @Param("registerDate") String registerDate, @Param("endDate") String endDate);

    Member getMemberById(Integer id);

    Member getFirstById(Integer id);

    Member getAllInfoById(Integer id);

    /**
     * 微信登录更新登录状态
     *
     * @param mb 登录信息
     * @return 数据库受影响行数
     */
    //int updateMemberToken(Member mb);

    //获取全部用户信息
    Member getAllById(Integer id);

    //查询渠道列表
    List<Member> getMemberAllGroupBy();

    void insertUnionId(@Param("userId") int userId, @Param("openId") String openId, @Param("unionId") String unionId);

    MemberToken selectToken(MemberToken mt);

    List<String> selectTokenById(int memberId);

    /**
     * 根据userid删除token
     *
     * @param memberId
     */
    void deleteTokenByIdToken(String memberId);

    List<InitializeHeads> selectInitializeHeads();

    Integer getCoinsById(Integer memberId);

    String selectOpenIdByUnionId(String unionId);

    boolean check(String cdkey, Integer memberId);

    String selectGzhopenId(int memberId);

    Boolean isWorker(Integer memberId);

    String[] getSuperUsers();

    List<Integer> selectOutTokenUserId();

    Integer selectPayNumberByMemberId(Integer memberId);

    int insertChannel(@Param("memberId") int memberId, @Param("channel") String channel, @Param("inviter") Integer inviter, @Param("rank") Integer rank);

    void updateChannel(@Param("memberId") int memberId, @Param("channel") String channel, @Param("inviter") Integer inviter, @Param("rank") Integer rank);

    Integer selectRank(int id);

    String selectGzhopenIdByUnionId(String unionId);

    void insertmember_add(@Param("openid") String openid, @Param("unionId") String unionId);

    List<String> selectChannels();

    Double rechargeAmount(Integer memberId);

    int updateMemberPhoneModel(@Param("memberId") Integer memberId, @Param("phoneModel") String phoneModel);

    String selectLevelById(int hostId);

    List<Member> getMemberListByAgentId(@Param("id") Integer id, @Param("level") Integer level);

    int updateByOpenId(Member member);
}