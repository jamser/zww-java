package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * Created by moying on 2018/6/5.
 */
@Data
public class AgentChangeForgetConfirmForm {

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    String password;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    String confirmPassword;

    Integer agentId;
}
