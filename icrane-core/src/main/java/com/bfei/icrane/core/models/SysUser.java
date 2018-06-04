package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

@Data
public class SysUser {
    private Integer id;

    private String avatar;

    private String account;

    private String password;

    private String salt;

    private String name;

    private Date birthday;

    private Integer sex;

    private String email;

    private String phone;

    private String roleid;

    private Integer deptid;

    private Integer status;

    private Date createtime;

    private Integer version;
}