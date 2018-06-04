package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

@Data
public class AdvertisementInfo {
    private Integer id;

    private String title;

    private String content;

    private String imgUrl;

    private Long downCount;

    private Date createDate;

    private Date updateDate;

    public Integer getId() {
        return id;
    }

}