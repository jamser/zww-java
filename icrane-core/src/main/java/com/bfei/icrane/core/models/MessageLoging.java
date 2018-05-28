package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by SUN on 2018/3/15.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageLoging extends BaseModels {


    private String inMessage;

    private String outMessage;

    private Date messageTime;
}