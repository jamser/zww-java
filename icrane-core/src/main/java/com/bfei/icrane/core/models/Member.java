package com.bfei.icrane.core.models;

import com.bfei.icrane.common.util.SpringUtil;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.impl.AccountServiceImpl;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/16
 * Description: APP用户持久化类.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member implements Serializable {

    private AccountService accountService;

    private static final long serialVersionUID = 1L;
    private int id;
    private String memberID;
    private String name;
    private String mobile;
    private String password;
    private String weixinId;
    private String openId;
    private String unionId;
    private String gender;
    //private Integer coins;//金币数
    private Integer catchNumber;
    private Timestamp registerDate;
    private Timestamp modifiedDate;
    private int modifiedBy;
    private Timestamp lastLoginDate;
    private Timestamp lastLogoffDate;
    private boolean onlineFlg;
    private String iconContextPath;
    private String iconFileName;
    private String iconRealPath;
    private Date birthday;
    private Integer watchingDollId; // 玩家进入娃娃机房间的id
    private Long playingDollFlg; //玩家是否在操作娃娃机的标记
    private String easemobUuid;
    private boolean activeFlg;
    private boolean inviteFlg;
    private PrefSet prefset;
    private boolean inviteFlgWeb;//是否已兑换邀请奖励
    private String registerFrom;//注册设备
    private String lastLoginFrom;//登录设备
    private String rReward;
    private String lReward;
    private Integer firstLogin;//首充登录 表示 0  为第一次登录
    private Integer firstCharge;//首充充值
    private String registerChannel;
    private String loginChannel;
    private String phoneModel;
    private Account account = new Account();//账户信息

    //兼容老版本的机器
    public Integer getCoins() {
        return getAccount().getCoins();
    }

    public void setCoins(Integer coins) {
        this.getAccount().setCoins(coins);
    }

    public Account getAccount() {
        if (SpringUtil.isReady()) {
            this.accountService = (AccountService) SpringUtil.getBean("AccountService");
            //判断数据库是否已经迁移过来了
            if (id != 0 && accountService.selectId(id) == null) {
                account = accountService.createAccount(id);
            }else{
                account = accountService.select(id);
            }
        }
        return this.account;
    }

    public String getBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (birthday != null) {
            String dt = sdf.format(birthday);
            return dt;
        } else {
            return null;
        }
    }
}
