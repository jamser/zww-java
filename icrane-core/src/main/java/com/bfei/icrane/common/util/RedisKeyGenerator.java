package com.bfei.icrane.common.util;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/10/14
 * Description: Methods used to generate redis key.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public class RedisKeyGenerator {

    /**
     * 投币计数防重复
     *
     * @param gameNum
     * @return
     */
    public static String getGameCoin(String gameNum) {
        return "game_" + gameNum + "_coin";
    }

    /**
     * 下抓命令计数
     *
     * @param dollId
     * @return
     */
    public static String getGameConsume(String gameNum) {
        return "game_" + gameNum + "_consume";
    }

    /**
     * 订单计数
     *
     * @param memberId
     * @return
     */
    public static String getGameSetter(String gameNum) {
        return "game_" + gameNum + "_setter";
    }

    /**
     * 游戏记录计数
     */
    public static String getGameCatchHistory(String gameNum) {
        return "game_" + gameNum + "_history";
    }

    /**
     * 游戏消费记录
     *
     * @param gameNum
     * @return
     */
    public static String getGameChargeHistory(String gameNum) {
        return "game_" + gameNum + "_chargeHistory";
    }

    /**
     * 下抓扣费计数
     *
     * @param memberId
     * @return
     */
    public static String getGameClaw(String gameNum) {
        return "game_" + gameNum + "_claw";
    }

    /**
     * 回复下抓
     *
     * @param gameNum
     * @return
     */
    public static String getGameClaw2(String gameNum) {
        return "game_" + gameNum + "_claw2";
    }

    /**
     * 是否抓中娃娃
     *
     * @return
     */
    public static String getGameCatch(String gameNum) {
        return "game_" + gameNum + "_catchFlag";
    }

    /**
     * 游戏结束状态
     *
     * @return
     */
    public static String getGameEndState(String gameNum) {
        return "game_" + gameNum + "_gameEndState";
    }

    /**
     * 游戏空闲状态  指令防重复
     *
     * @param dollId
     * @return
     */
    public static String getGameIDLE(Integer dollId) {
        return "room_" + dollId + "_IDLE";
    }

    /**
     * 游戏就绪状态 指令防重复
     *
     * @param dollId
     * @return
     */
    public static String getGameREADY(Integer dollId) {
        return "room_" + dollId + "_READY";
    }


    //获取房间主播id值的key
    public static String getRoomHostKey(Integer dollId) {

        return "room_" + dollId + "_host";
    }

    //获取房间抓取概率1
    public static String getMachineP1(Integer dollId) {

        return "machine_" + dollId + "_1";
    }

    //获取房间抓取概率2
    public static String getMachineP2(Integer dollId) {

        return "machine_" + dollId + "_2";
    }

    //获取房间抓取概率3
    public static String getMachineP3(Integer dollId) {

        return "machine_" + dollId + "_3";
    }

    //机器概率基数
    public static String getMachineBaseNum(Integer dollId) {

        return "machine_" + dollId + "_basenum";
    }

    //获取房主抓取概率编号
    public static String getMachineHost(Integer dollId) {

        return "machine_" + dollId + "_host";
    }

    //用户已经充值金额
    public static String getMachineCharge(Integer dollId) {
        return "machine_" + dollId + "_charge";
    }

    //机器类型
    public static String getMachineType(Integer dollId) {
        return "machine_" + dollId + "_type";
    }

    //新用户标志  1为新用户 0为老用户
    public static String getMemberNew(Integer dollId) {
        return "machine_" + dollId + "_MemberNew";
    }

    //获取用户抓取高概率次数  每天重置
    public static String getMemberClawNum(Integer memberId, Integer dollId) {

        return "member_" + memberId + "_" + dollId + "_num";
    }

    //连续强抓
    public static String getMemberStrongClawNum(Integer userId) {

        return "member_" + userId + "_Strongclaw";
    }

    //获取短信验证码的key
    public static String getLinkMobileCodeKey(String mobile) {

        return "linkMobileCode_" + mobile + "_code";
    }

    //获取房间状态的key
    public static String getRoomStatusKey(Integer dollId) {

        return "room_" + dollId + "_status";
    }

    //获取gameNum key
    public static String getRoomGameNumKey(Integer userId, Integer dollId) {

        return "room_" + userId + "_" + dollId + "_GameNum";
    }

    //获取房间在线用户id集合的key
    public static String getRoomKey(Integer dollId) {

        return "room_" + dollId;
    }

    //获取用户抓取娃娃数量    占卜房    练习房除外
    public static String getMemberToyNum(Integer memberId) {

        return "member_" + memberId + "_toyNum";
    }

    //获取用户所在房间id值的key
    public static String getMemberRoomKey(Integer memberId) {

        return "member_" + memberId + "_room";
    }

    //获取用户常用信息hashset的key
    public static String getMemberInfoKey(Integer memberId) {

        return "member_" + memberId + "_info_";
    }

    //获取房间排队用户id列表的key
    public static String getRoomQueueKey(Integer dollId) {

        return "room_" + dollId + "_queue";
    }

    //获取房间已排到但等待响应的用户id的key
    public static String getRoomQueueWaitKey(Integer dollId) {

        return "room_" + dollId + "_queue_wait";
    }

    //获取用户正在排队的房间id的key
    public static String getMemberQueueRoomKey(Integer memberId) {

        return "member_" + memberId + "_room_queue";
    }

    public static String getCodeLoginKey(String mobile) {
        return "codeLoginKey_" + mobile + "_code";
    }

    public static String getdivinationKey(long divinationId, Integer memberId) {
        return "divination_" + divinationId + "_" + memberId + "_Key";
    }

    public static String getShareImgKey(long divinationId, Integer memberId) {
        return "shareImg_" + divinationId + "_" + memberId + "_Key";
    }

    public static String getShareKey(Integer memberId, String gameNum) {
        return "share" + memberId + "_" + gameNum + "_Key";
    }

    public static String getShareUrlKey(Integer memberId) {
        return "shareUrl" + memberId + "_Key";
    }

    public static String getLikeDollKey(Integer memberId) {
        return "likeDoll" + memberId + "_Key";
    }

    public static String getQRCodeKey(String memberId) {
        return "QRCodeKey" + memberId + "_Key";
    }

    public static String getAgentCodeKey(String agent) {
        return "AgentCodeKey" + agent + "_Key";
    }
    public static String getCodeAentKey(String mobile) {
        return "codeAgentPwdKey_" + mobile + "_code";
    }
}
