package com.bfei.icrane.core.pojos;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by moying on 2018/7/19.
 */
@Data
public class RankMemberPojo implements Serializable {


    private static final long serialVersionUID = -2608663222013851513L;
    private Integer memberId;

    private String sex;

    private String iconRealPath;

    private String userName;

    private Integer rankToady;

    private Integer rankWeek;

    private Integer rankAll;
}
