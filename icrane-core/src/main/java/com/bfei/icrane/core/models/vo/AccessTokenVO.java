package com.bfei.icrane.core.models.vo;

import lombok.Data;

/**
 * Created by moying on 2018/7/4.
 */
@Data
public class AccessTokenVO {
    private String access_token;
    private Long expires_in_access;

    private String ticket;
    private Long expires_in_ticket;
}

