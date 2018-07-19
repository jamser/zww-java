package com.bfei.icrane.core.pojos;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by moying on 2018/7/18.
 */
@Data
public class Rankpojo implements Serializable {
    private static final long serialVersionUID = 3538143879605641670L;
    private Integer id;
    private Integer memberId;
    private String userName;
    private String iconRealPath;
    private String sex;
    private Integer number;
}
