package com.bfei.icrane.common.util;


/**
 * Created by moying on 2018/6/3.
 */
public enum AgentEnum {
    AGENT_ONE(1, "AGENT_ONE_FEE"),
    AGENT_TWO(2, "AGENT_TWO_FEE"),
    AGENT_THREE(3, "AGENT_THREE_FEE");

    private Integer code;
    private String info;

    AgentEnum(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static AgentEnum getAgentByCode(Integer code) {
        for (AgentEnum typeEnum : AgentEnum.values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
