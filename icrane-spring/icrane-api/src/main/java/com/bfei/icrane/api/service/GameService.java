package com.bfei.icrane.api.service;

import java.util.List;
import java.util.Map;

import com.bfei.icrane.core.pojos.MemberInfoPojo;
import com.bfei.icrane.game.GameStatusEnum;

/**
 * 游戏逻辑service
 *
 * @author Administrator
 */
public interface GameService {

    void enterDoll(Integer dollId, Integer memberId);

    void exitDoll(Integer memberId);

    /**
     * 获取在线人数
     *
     * @param dollId
     * @return
     */
    Long onLineCount(Integer dollId);

    /**
     * 获取当前游戏编号
     *
     * @param dollId
     * @return
     */
    String takeGameNum(Integer userId, Integer dollId);

    /**
     * 获取玩家所在房间编号
     *
     * @param userId
     * @return
     */
    Integer takeMemberRoomId(Integer userId);

    /**
     * 获取房间一定人数  memberId信息
     *
     * @param dollId
     * @param count
     * @return
     */
    List<String> takeRoomMembers(Integer dollId, Integer count);

    /**
     * 获得房间房主
     *
     * @param dollId
     * @return
     */
    String takeRoomHostMember(Integer dollId);

    /**
     * 通过token获取对应人 userId
     *
     * @param token
     * @return
     */
    Integer takeToken2Member(String token);

    /**
     * 房间列表加入 新增用户
     *
     * @param dollId
     * @param userId
     */
    void addRoomQueue(Integer dollId, Integer userId);

    /**
     * 获取房间人数列表
     *
     * @param dollId
     * @return
     */
    List<String> takeRoomQueue(Integer dollId);

    /**
     * 玩家离开房间 列表
     *
     * @param dollId
     * @param userId
     */
    void leaveRoomQueue(Integer dollId, Integer userId);

    /**
     * 检查玩家能否 开始游戏
     *
     * @param dollId
     * @param userId
     * @return
     */
    GameStatusEnum checkPlaying(Integer dollId, Integer userId);

    /**
     * 启动游戏
     *
     * @param dollId
     * @param userId
     * @return
     */
    GameStatusEnum startPlay(Integer dollId, Integer userId);

    String takeRoomState(Integer dollId);

    void roomCleanState(Integer dollId, Integer userId);

    /**
     * 下抓扣费
     *
     * @param userId
     * @return
     */
    GameStatusEnum consumePlay(Integer dollId, Integer userId, String state);

    /**
     * 正常结束游戏
     *
     * @param dollId
     * @param userId
     * @param gotDoll
     * @return
     */
    GameStatusEnum endRound(Integer dollId, Integer userId, Integer gotDoll, String state);

    //添加异常判断
    GameStatusEnum endRound(Integer dollId, Integer userId, Integer gotDoll, String gameNum, String state);

    /**
     * 游戏结束  中途结束
     *
     * @param dollId
     * @param userId
     * @return
     */
    GameStatusEnum end(Integer dollId, Integer userId);

    /**
     * 刷新房间列表添加 部分用户列表显示信息
     */
    void addMemberInfoPojo(Integer userId, MemberInfoPojo memberInfoPojo);

    boolean existsMemberInfoKey(Integer userId);

    /**
     * 获取redis列表用户
     *
     * @param userId
     */
    MemberInfoPojo takeMemberInfoPojo(Integer userId);

    /**
     * 占卜房结果分享地址
     *
     * @param dollId
     * @param memberId
     * @return
     */
    Map<String, Object> getShareUrl(Integer dollId, Integer memberId, Integer version);

    /**
     * 用户邀请图分享地址
     *
     * @param memberId
     * @return
     */
    String getShareUrl(Integer memberId, Integer version);

    /**
     * 获取占卜结果
     *
     * @return
     */
    String getDivinationUrl(Integer dollId, Integer memberId);

    /**
     * 分享得币
     *
     * @param memberId
     * @param gameNum
     * @return
     */
    Integer chackShare(Integer memberId, String gameNum);

    Map<String, Object> Share(Integer memberId);

    Map<String, String> getQRCodeImgUrl(Integer memberId, Integer version, Integer index);

    Map<String, String> getAgentImgUrl(Integer agentId, Integer version, Integer index);

    Integer gameResult(Integer memberId, Integer dollId);
}