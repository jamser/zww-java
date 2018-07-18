package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

@Data
public class MemberComment {
    private Long id;

    private Integer memberId;

    private String comment;

    private Integer dollId;

    private Date createDate;

    private Date updateDate;

    private Byte status;

    private String username;
}