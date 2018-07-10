package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by moying on 2018/7/9.
 */
@Data
public class AgentChangePasswordForm {

    @NotEmpty(message = "token不能为空")
    private String token;

    @NotNull(message = "代理ID不能为空")
    private Integer agentId;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    String oldPassword;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    String newPassword;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    String confirmPassword;
}
