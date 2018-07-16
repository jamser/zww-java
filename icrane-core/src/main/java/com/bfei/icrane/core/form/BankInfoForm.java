package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by moying on 2018/6/5.
 */
@Data
public class BankInfoForm {

    private Integer id;

    @NotNull
    private Integer agentId;

    @NotEmpty(message = "银行名字不能为空")
    private String cardBank;

    @NotEmpty(message = "支行不能为空")
    private String cardSubBank;

    @NotEmpty(message = "发行省份不能为空")
    private String cardProvince;

    @NotEmpty(message = "发行城市不能为空")
    private String cardCity;

    @NotEmpty(message = "发行区域不能为空")
    private String cardArea;

    //    @NotEmpty(message = "银行联行号不能为空")
    private String cardBankNo;

    @NotEmpty(message = "卡号不能为空")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "卡号位数不够")
    private String cardNo;

    @NotEmpty(message = "身份证号不能为空")
    private String idCardNo;

    @NotEmpty(message = "真实姓名不能为空")
    private String name;

    @NotEmpty(message = "银行预留手机号不能为空")
    @Pattern(regexp = "^1[34578]\\d{9}", message = "手机号格式不规范")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String smsCode;

    @NotNull
    private Integer cardBankType;
}
