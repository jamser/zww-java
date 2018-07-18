package com.bfei.icrane.api.service;

import java.util.List;

import com.bfei.icrane.common.util.ResultMap;
import org.apache.ibatis.annotations.Param;

import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollRoom;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.pojos.CatchDollPojo;
import com.bfei.icrane.core.pojos.DollImgPojo;

/**
 * @author lgq Version: 1.0 Date: 2017/9/23 Description: 用户Service接口类. Copyright
 *         (c) 2017 mwan. All rights reserved.
 */
public interface DollRoomService {

    DollRoom getDollId(Integer memberId);

    /**
     * 进房
     *
     * @param roolRoom
     * @return
     */
    Integer insertDollRoom(DollRoom doolRoom, Doll doll);

    /**
     * 退房
     *
     * @param member
     * @return
     */

    Integer outDollRoom(Integer memberId, Doll doll);

    /**
     * 在线人数
     *
     * @return
     */
    DollRoom getDollRoomCount(Integer dollId);

    List<DollRoom> getMemberHead(@Param("dollId") Integer dollId, @Param("offset") int offset, @Param("limit") int limit);

    DollRoom getPlayMember(Integer dollId);

    List<DollImgPojo> getDollImg(Integer dollId);

    List<CatchDollPojo> getCatchDoll(Integer dollId);

    //开始游戏操作，设置某玩家为楼主
    boolean startPlay(Integer dollId, Integer memberId);

    boolean startPlay(Integer dollId, Member member);

    //扣费以及生成消费记录
    boolean consumePlay(Doll doll, Member member, String state);

    //检查当前是否有楼主
    Integer checkPlaying(Integer dollId, Integer memberId);

    //结束游戏操作，设置相应key，更新娃娃机状态
    boolean endPlay(Integer dollId, Integer memberId);

    //结束本轮游戏，生成抓取记录，如成功生成订单
    boolean endRound(Integer dollId, Integer memberId, Integer catchFlag, String state);

    //结束本轮游戏，生成抓取记录，如成功生成订单
    boolean endRound(Integer dollId, Integer memberId, Integer catchFlag, String gameNum, String state);

    /**
     * 获取房间娃娃详情
     *
     * @param dollId
     * @return
     */
    ResultMap selectDollParticularsById(Integer dollId);

    void endPlayByCatchCount(Integer memberId, Integer dollId);

    ResultMap getCatchProParams(Integer memberId);
}
