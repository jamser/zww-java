package com.bfei.icrane.core.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

/**
 * Created by moying on 2018/6/3.
 */
@Data
public class AgentLoginForm {

    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Size(min = 6, max = 12)
    private String password;

}
