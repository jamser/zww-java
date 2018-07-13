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

    private Double xAxis;

    private Double yAxis;

    private Date createDate;

    private Date updateDate;

    private String wordColor;

}