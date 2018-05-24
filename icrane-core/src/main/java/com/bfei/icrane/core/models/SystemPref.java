package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/26
 * Description: 系统参数表持久层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemPref implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String name;

    private String value;

    private Date modifiedDate;

    private Integer modifiedBy;

    private Integer type;

}