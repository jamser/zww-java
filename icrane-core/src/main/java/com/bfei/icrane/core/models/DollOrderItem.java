package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/26
 * Description: 娃娃抓取成功后的订单明细持久层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DollOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private DollOrder dollOrder;
    private Integer quantity;
    private Doll doll;
    private Date createdDate;
    private Date modifiedDate;
    private Integer newId;
    private String dollCode;
    private String deliverMethod = "";
    private String deliverNumber = "";
    private Account account;
}