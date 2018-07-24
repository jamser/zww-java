package com.bfei.icrane.core.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class OemBanner implements Serializable {

    private static final long serialVersionUID = -8727427387007935499L;
    
    private Integer id;

    private Integer oemId;

    private String url;

    private String imgUrl;

    private Integer sort;

    private Integer status;


}