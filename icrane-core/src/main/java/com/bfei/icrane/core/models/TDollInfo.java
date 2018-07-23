package com.bfei.icrane.core.models;

import lombok.Data;

import java.util.Date;

@Data
public class TDollInfo {
    private Integer id;

    private String dollname;

    private Integer dolltotal;

    private String dollcode;

    private String agency;

    private String size;

    private String type;

    private String note;

    private Integer redeemcoins;

    private Long dollcoins;

    private Long delivercoins;

    private Date addtime;

    private String imgUrl;
}