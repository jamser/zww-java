package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeOrder implements Serializable {

    private static final long serialVersionUID = -7573063272518136649L;

    private Long id;                //订单序号
    private String orderNo;            //订单编号
    private Integer chargeruleid;    //充值规则
    private String chargeName;        //充值规则名称
    private Double price;            //订单金额
    private Integer memberId;        //用户id
    private String memberName;        //用户名称
    private Integer chargeType;        //充值类型  1 为微信
    private Integer chargeState;        //订单状态
    private Integer coinsBefore;        //充值前娃娃币
    private Integer coinsAfter;        //充值后娃娃币
    private Integer coinsCharge;        //充值娃娃币
    private Integer coinsOffer;        //充值赠送娃娃币
    private Integer superTicketBefore;        //充值前强爪券
    private Integer superTicketAfter;        //充值后强爪券
    private Integer superTicketCharge;        //充值强爪券
    private Integer superTicketOffer;        //充值赠送强爪券
    private Date createDate;        //创建时间
    private Date updateDate;        //修改时间

    //代理ID
    private Long agentSuperId;
    private Long agentOneId;
    private Long agentTwoId;
    private Long agentThreeId;
    //代理费率
    private BigDecimal agentSuperFee;
    private BigDecimal agentOneFee;
    private BigDecimal agentTwoFee;
    private BigDecimal agentThreeFee;

}
