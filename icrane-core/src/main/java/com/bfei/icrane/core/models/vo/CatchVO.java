package com.bfei.icrane.core.models.vo;

import lombok.Data;

import java.util.Date;

/**
 * Created by moying on 2018/7/13.
 */
@Data
public class CatchVO {
    private Integer dollId;
    private String dollName;
    private Date createDate;
    private String userName;
    private Integer memberId;
}
