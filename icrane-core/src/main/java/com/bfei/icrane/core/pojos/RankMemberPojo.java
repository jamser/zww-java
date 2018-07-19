package com.bfei.icrane.core.pojos;

import lombok.Data;

/**
 * Created by moying on 2018/7/19.
 */
@Data
public class RankMemberPojo {

    private Integer memberId;

    private String sex;

    private String iconRealPath;

    private String userName;

    private Integer rankToady;

    private Integer rankWeek;

    private Integer rankAll;
}
