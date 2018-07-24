package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.CatchHistory;
import com.bfei.icrane.core.models.Charge;
import com.bfei.icrane.core.models.vo.GameHistoryDetail;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lgq
 *         Version: 1.0
 *         Date: 2017/9/25
 *         Description: 用户Dao接口类.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public interface CatchHistoryDao {
    CatchHistory selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 获取抓取记录详情
     *
     * @param historyId
     * @param userId
     * @return
     */
    GameHistoryDetail selectHistoryDetail(@Param("historyId") Integer historyId, @Param("userId") Integer userId);

    Integer totalCount(@Param("memberId") Integer memberId);

    List<CatchHistory> getCatchHistory(@Param("begin") Integer begin, @Param("pageSize") Integer pageSize, @Param("memberId") Integer memberId);

    Integer insertCatchHistory(CatchHistory catchHistory);

    Integer updateCatchHistory(CatchHistory catchHistory);

    Integer updateVideoUrl(@Param("gameNum") String gameNum, @Param("videoUrl") String videoUrl, @Param("userId") Integer userId);

    String queryVideoUrl(String gameNum);

    GameHistoryDetail selectHistoryDetailByGameNum(@Param("gameNum") String gameNum, @Param("userId") Integer userId);

    String getRecenGameNum(@Param("memberId") Integer memberId, @Param("dollId") Integer dollId);

    List<CatchHistory> selectByDollId(@Param("dollId") Integer dollId);

    List<CatchHistory> selectByDollIdLimit5(@Param("dollId") Integer dollId);

    List<CatchHistory> getCatchHistoryLists(@Param("memberId") Long memberId);
}