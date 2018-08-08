package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRules implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Double chargePrice;//礼包价格
    private Integer coinsCharge;//娃娃币
    private Integer coinsOffer;//赠送娃娃币
    private Integer superTicketCharge;//强爪券
    private Integer superTicketOffer;//赠送强爪券
    private Double discount;//折扣
    private String description;//说明文字
    private Date createdDate;//创建日期
    private Integer createdBy;//创建人id
    private Date modifiedDate;//修改日期
    private Integer modifiedBy;//修改人id
    private Integer chargeType;//充值类型 , (0 普通包,1 时长包)
    private String chargeName;//规则名称: , (首充, 新手礼包, 月卡, 周卡)
    private Integer orderby;//排序序号:
    private Integer cionsFirst;//首充娃娃币
    private Integer chargeTimesLimit;//限购次数
    private Integer chargeDateLimit;//期限
    private Integer rulesStatus;//规则状态
    private String icon;//图标

}
