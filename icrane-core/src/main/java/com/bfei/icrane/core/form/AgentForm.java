package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * Created by moying on 2018/6/2.
 */
@Data
public class AgentForm {

    @NotEmpty(message = "token不能为空")
    private String token;

    @NotNull(message = "代理ID不能为空")
    private Integer agentId;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    @NotEmpty
    @Pattern(regexp = "^[0-9a-zA-Z]{6,16}$", message = "密码只能是英文字符或者数字")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^1[34578]\\d{9}", message = "手机号格式不规范")
    private String phone;

    @NotEmpty(message = "姓名不能为空")
    private String nickName;

}
