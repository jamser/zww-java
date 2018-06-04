package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

/**
 * Created by moying on 2018/6/4.
 */
@Data
public class SysNotify {
    private Integer id;

    private String title;

    private String content;

    private Date createDate;

    private Date updateDate;

    private Integer status;
}
