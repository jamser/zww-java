package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.Account;

import java.util.List;
import java.util.Map;

/**
 * Created by SUN on 2018/3/2.
 * 用户账户相关服务类接口
 */
public interface AccountService {

    Account selectById(Integer id);

    Account select(Integer id);

    Account createAccount(Integer id);

    void insert(Account account);

    /**
     * 修改成长值
     *
     * @param account
     */
    void updateMemberGrowthValue(Account account);

    /**
     * 修改hi币
     *
     * @param account
     */
    void updateMemberCoin(Account account);

    /**
     * 修改钻石
     *
     * @param account
     */
    void updateMemberSuperTicket(Account account);

    /**
     * 更新周卡
     *
     * @param account
     */
    void updateMemberSeeksCardState(Account account);

    /**
     * 更新月卡
     *
     * @param account
     */
    void updateMemberMonthCardState(Account account);

    Integer selectId(int id);

    void updateBitStatesById(Account account);

    Map vip(Integer memberId);

    List<Account> selectPayingUser();

    boolean whetherTheDowngrade(Integer memberId);

    void updateMemberCoinAndSignDAte(Account account);

    void updateMemberGrowthValueMonth(Account account);

    void updateAccountLover(Account account);
}