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

    @NotEmpty(message = "银行联行号不能为空")
    private String cardBankNo;

    @NotEmpty(message = "卡号不能为空")
    @Pattern(regexp = "^[0-9]{16,19}$", message = "密码只能是英文字符或者数字")
    private String cardNo;

    @NotEmpty(message = "身份证号不能为空")
    private String idCardNo;

    @NotEmpty(message = "真实姓名不能为空")
    private String name;

    @NotEmpty(message = "银行预留手机号不能为空")
    @Pattern(regexp = "^1[34578]\\d{9}", message = "手机号格式不规范")
    private String phone;

    @NotEmpty(message = "身份证正面不能为空")
    private String idCardPicturePos;

    @NotEmpty(message = "身份证反面不能为空")
    private String idCardPictureRev;

    @NotEmpty(message = "手持身份证不能为空")
    private String idCardPicture;

    @NotEmpty(message = "银行卡正面不能为空")
    private String bankPicturePos;
}
