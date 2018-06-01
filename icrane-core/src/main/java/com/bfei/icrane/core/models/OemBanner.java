package com.bfei.icrane.core.models;

import lombok.Data;

@Data
public class OemBanner {
    private Integer id;

    private Integer oemId;

    private String url;

    private String imgUrl;

    private Integer sort;

    private Integer status;


}