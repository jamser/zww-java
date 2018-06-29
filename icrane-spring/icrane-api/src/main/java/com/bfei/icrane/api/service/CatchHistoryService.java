package com.bfei.icrane.api.service;

import com.bfei.icrane.core.models.CatchHistory;
import com.bfei.icrane.core.models.vo.GameHistoryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lgq Version: 1.0 Date: 2017/9/25 Description: 用户Service接口类. Copyright
 *         (c) 2017 lgq. All rights reserved.
 */
public interface CatchHistoryService {

	 Integer insertCatchHistory(CatchHistory catchHistory);

	 GameHistoryDetail queryCatchDetail(Integer historyId, Integer userId);
	 //视频上传
	Integer saveVideo(String gameNum, String videoUrl,Integer userId);

	String queryVideoUrl(String gameNum);

	GameHistoryDetail queryCatchDetailByGameNum(String gameNum, Integer userId);

    String getRecenGameNum(Integer memberId, Integer dollId);

	List<CatchHistory> getCatchHistoryLists(Long memberId);
}
