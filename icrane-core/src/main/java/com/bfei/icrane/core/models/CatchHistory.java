package com.bfei.icrane.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatchHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer dollId;
    private Integer memberId;
    private Timestamp catchDate;
    private String catchStatus;
    private String videoUrl;
    private String gameNum;
    private Integer machineType;
    private String dollCode;
    private String dollName;
    private String dollUrl;


}
