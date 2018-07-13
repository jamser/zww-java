package com.bfei.icrane.api.service;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.SignedSheet;

/**
 * 签到  接口
 */
public interface SignedSheetService {

    void insert(SignedSheet signedSheet);

    /**
     * 连续签到送币及流水记录
     *
     * @param memberId 用户UserId
     */
    void insert(String memberId);

    SignedSheet selectOneByMemberID(String memberId);

    //购买时长包 赠送娃娃币
    void giveCoins(String memberId);

    /**
     * 连续签到
     *
     * @param memberId 用户UserId
     */
    void updateByMemberId(String memberId);

    /**
     * 重新开始签到
     *
     * @param memberId 用户UserId
     */
    void updateNewDayByMemberId(String memberId);

    /**
     * 第一次签到
     *
     * @param memberId 用户UserId
     */
    void createSignedSheet(String memberId, String memberName);

    /**
     * 签到
     *
     * @param memberId
     * @return
     */
    ResultMap sign(String memberId);

    /**
     * 签到状态检测
     *
     * @param memberId
     * @param head
     * @return
     */
    ResultMap hassign(String memberId, String head);

    /**
     * 是否开启
     *
     * @param memberId
     * @return
     */
    ResultMap isOpen(String memberId);

    /**
     * 获取转盘概率
     *
     * @param memberId
     * @return
     */
    ResultMap getTurnProbability(String memberId);


}
