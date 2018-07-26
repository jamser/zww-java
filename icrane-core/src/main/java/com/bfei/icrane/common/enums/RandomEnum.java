package com.bfei.icrane.common.enums;

import lombok.Getter;

/**
 * Created by moying on 2018/7/13.
 */
@Getter
public enum RandomEnum {
    TEN_COIN(0, 10),
    FIFTEEN_COIN(1, 15),
    FIFTY_COIN(2, 50),
    Thanks(3, 0);

    private Integer code;

    private Integer coin;

    RandomEnum(Integer code, Integer coin) {
        this.code = code;
        this.coin = coin;
    }
}
