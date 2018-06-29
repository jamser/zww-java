package com.bfei.icrane.api.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bfei.icrane.api.service.CatchHistoryService;
import com.bfei.icrane.core.dao.CatchHistoryDao;
import com.bfei.icrane.core.models.CatchHistory;
import com.bfei.icrane.core.models.vo.GameHistoryDetail;

import java.util.List;

/**
 * @author lgq Version: 1.0 Date: 2017年9月22日date Description: 用户Service接口实现类.
 *         Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("CatchHistoryService")
public class CatchHistoryServiceImpl implements CatchHistoryService {
    private static final Logger logger = LoggerFactory.getLogger(CatchHistoryServiceImpl.class);
    @Autowired
    private CatchHistoryDao catchHistoryDao;

    public Integer insertCatchHistory(CatchHistory catchHistory) {
        logger.info("insertCatchHistory catchHistory:{}", catchHistory);
        Integer result = catchHistoryDao.insertCatchHistory(catchHistory);
        logger.info("返回 result:{}", result);
        return result;
    }

    @Override
    public GameHistoryDetail queryCatchDetail(Integer historyId, Integer userId) {
        return catchHistoryDao.selectHistoryDetail(historyId, userId);
    }

    @Override
    public Integer saveVideo(String gameNum, String videoUrl, Integer userId) {
        return catchHistoryDao.updateVideoUrl(gameNum, videoUrl, userId);
    }

    @Override
    public String queryVideoUrl(String gameNum) {
        return catchHistoryDao.queryVideoUrl(gameNum);
    }

    @Override
    public GameHistoryDetail queryCatchDetailByGameNum(String gameNum, Integer userId) {
        // TODO Auto-generated method stub
        return catchHistoryDao.selectHistoryDetailByGameNum(gameNum, userId);
    }

    @Override
    public String getRecenGameNum(Integer memberId, Integer dollId) {
        return catchHistoryDao.getRecenGameNum(memberId, dollId);
    }

    @Override
    public List<CatchHistory> getCatchHistoryLists(Long memberId) {
        return catchHistoryDao.getCatchHistoryLists(memberId);
    }
}
