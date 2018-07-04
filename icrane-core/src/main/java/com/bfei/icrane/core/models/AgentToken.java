package com.bfei.icrane.core.models;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

@Data
public class AgentToken implements Serializable {
    private static final long serialVersionUID = -3645575585891396520L;
    private String token;

    private Integer agentId;

    private Date validStartDate;

    private Date validEndDate;

    private String host;


}