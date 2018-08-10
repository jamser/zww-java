package com.bfei.icrane.game;

public enum GameStatusEnum {
    GAME_GO(0, "下一步"),
    GAME_START(1, "空闲中"),
    GAME_PLAYING(2, "游戏中"),
    GAME_END(3, "游戏结束"),
    GAME_MAINTAINED(4, "维修中"),
    GAME_NOT_ONLINE(5, "未上线"),
    GAME_PRICE_NOT_ENOUGH(6, "游戏不足"),
    GAME_START_SUCCESS(7, "游戏启动成功"),
    GAME_START_FAIL(8, "游戏启动失败"),
    GAME_CONSUME_SUCCESS(9, "游戏消费成功"),
    GAME_CONSUME_FAIL(10, "游戏消费失败"),
    GAME_END_ROUND_SUCCESS(11, "游戏正常结束"),
    GAME_END_FAIL(12, "游戏结束异常"),
    GAME_PRICE_NOT_SuperTicket(13, "钻石不足"),
    GAME_LOVER_NOT_ENOUGH(14, "未充值礼包");

    private final int code;

    private final String desc;

    GameStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


}
