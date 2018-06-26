package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Created by moying on 2018/6/3.
 */
@Data
public class AgentLoginForm {

    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}", message = "用户名格式不规范")
    private String username;


    @NotEmpty(message = "密码不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能6～16位英文字符或者数字")
    private String password;
}
