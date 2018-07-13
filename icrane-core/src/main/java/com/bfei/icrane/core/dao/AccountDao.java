package com.bfei.icrane.core.dao;

import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.core.models.Account;

import java.util.List;

/**
 * Created by SUN on 2018/1/10.
 */
public interface AccountDao {

    Account selectById(Integer id);

    void insert(Account account);

    //void updateById(Account account);
    //更新 成长值
    void updateMemberGrowthValue(Account account);

    //更新 币
    void updateMemberCoin(Account account);

    //更新 币和签到时间
    void updateMemberCoinAndSignDAte(Account account);

    //更新 钻
    void updateMemberSuperTicket(Account account);

    //更新 周卡
    void updateMemberSeeksCardState(Account account);

    //更新 月卡
    void updateMemberMonthCardState(Account account);

    Integer selectId(int id);

    void updateBitStatesById(Account account);

    List<Account> selectPayingUser();

}