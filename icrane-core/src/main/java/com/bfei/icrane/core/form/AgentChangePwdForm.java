package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * Created by moying on 2018/6/5.
 */
@Data
public class AgentChangePwdForm {

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能是英文字符或者数字")
    String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能是英文字符或者数字")
    String confirmPassword;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1[34578]\\d{9}", message = "手机号格式不规范")
    String phone;

    @NotEmpty(message = "验证码")
    @Pattern(regexp = "^[0-9]\\d{6}", message = "验证码格式不规范")
    String smsCode;
}
