package com.bfei.icrane.core.models;

import com.bfei.icrane.common.util.BitStatesUtils;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by SUN on 2018/1/10.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = -8422287866656057489L;

    private Integer id;                         //用户id
    private Integer coins;                      //hi币
    private Integer superTicket;                //强爪券
    private Date signDate;
    private long bitState;                      //账户状态值
    private Date weeksCardState = new Date(0);  //最近周卡领取时间
    private Date monthCardState = new Date(0);  //最近月卡领取时间
    private BigDecimal growthValue;             //会员成长值
    private BigDecimal growthValueMonth;      //会员每月成长值
    private Integer growthValueMonthLevel;      //会员每月充值级别
    private Vip vip;                            //会员
    private int tester;

    public boolean isTester() {
        if (this.tester == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void addState(long state) {
        this.bitState = BitStatesUtils.addState(this.bitState, state);
    }

    public void removeState(long state) {
        this.bitState = BitStatesUtils.removeState(this.bitState, state);
    }

    //获取是否首充
    public boolean getCoinFirstCharge() {
        return BitStatesUtils.hasState(this.bitState, BitStatesUtils.COIN_FIRST_CHARGE);
    }

    //当天是否领取周卡
    public boolean getTodyWeeksCard() {
        //获取当前时间
        Calendar checkdateCalendar = Calendar.getInstance();
        //获取用户周卡领取时间
        //设置为用户周卡领取时间
        checkdateCalendar.setTime(weeksCardState);
        //获取今天凌晨时间
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE) - 1, 0, 0, 0);
        //判断用户周卡领取时间是否是在今天凌晨之前
        if (checkdateCalendar.before(today)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getWeeksCardEnd() {
        //获取是否周卡有效
        return BitStatesUtils.hasState(this.bitState, BitStatesUtils.WEEKS_CARD);
    }

    //当天是否领取月卡
    public boolean getTodyMonthCard() {
        //获取当前时间
        Calendar checkdateCalendar = Calendar.getInstance();
        //获取用户月卡领取时间
        //设置为用户月卡领取时间
        checkdateCalendar.setTime(monthCardState);
        //获取今天凌晨时间
        Calendar today = Calendar.getInstance();
        today.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE) - 1, 0, 0, 0);
        //判断用户月卡领取时间是否是在今天凌晨之前
        if (checkdateCalendar.before(today)) {
            return false;
        } else {
            return true;
        }
    }

    //获取月卡是否到期
    public boolean getMonthCardend() {
        return BitStatesUtils.hasState(this.bitState, BitStatesUtils.MONTH_CARD);
    }
}
