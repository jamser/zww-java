package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.CatchHistoryService;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 抓取记录
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/catch")
public class CatchHistoryController {
    private static final Logger logger = LoggerFactory.getLogger(CatchHistoryController.class);

    @Autowired
    private CatchHistoryService catchHistoryService;
    @Autowired
    private DollService dollService;
    @Autowired
    private ValidateTokenService validateTokenService;

//    @RequestMapping(value = "/addCatchHistory", method = RequestMethod.GET)
//    @ResponseBody
//    public Map<String, Object> markCharge(Integer dollId, Integer memberId, String catchStatus, String token)
//            throws Exception {
//        logger.info("添加抓取记录接口参数：" + "dollId=" + dollId + "," + "memberId=" + memberId + "," + "catchStatus="
//                + catchStatus + "," + "token=" + token);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            // 验证token有效性
//            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
//                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//
//                return resultMap;
//            }
//            String gameNum = StringUtils.getCatchHistoryNum();
//            CatchHistory catchHistory = new CatchHistory();
//            catchHistory.setMemberId(memberId);
//            catchHistory.setDollId(dollId);
//            catchHistory.setCatchStatus(catchStatus);
//            catchHistory.setCatchDate(TimeUtil.getTime());
//            catchHistory.setGameNum(gameNum);
//            catchHistory.setDollCode(dollService.selectByPrimaryKey(dollId).getDollID());
//            logger.info("添加抓取记录参数catchHistory=" + "memberId " + catchHistory.getMemberId() + "dollId " + catchHistory.getDollId() + "catchStatus " + catchHistory.getCatchStatus());
//            Integer result = catchHistoryService.insertCatchHistory(catchHistory);
//            logger.info("添加抓取记录结果result=" + result);
//            if (result == 1) {
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            logger.info("添加抓取记录结果resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("添加抓取记录出错", e);
//            throw e;
//        }
//    }

    // 添加抓取记录
//    @RequestMapping(value = "/catchDetail", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> catchDetail(Integer historyId, Integer userId, String token)
//            throws Exception {
//        logger.info("`获取抓取记录详情参数：" + "historyId=" + historyId + "userId=" + userId + "," + "token=" + token);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            // 验证token有效性
//            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, userId)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
//                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//
//                return resultMap;
//            }
//            GameHistoryDetail gameHistoryDetail = catchHistoryService.queryCatchDetail(historyId, userId);
//            gameHistoryDetail.setStsTokenUrl(userId, token);
//            if (gameHistoryDetail != null) {
//                resultMap.put("resultData", gameHistoryDetail);
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            logger.info("添加抓取记录结果resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("添加抓取记录出错", e);
//            throw e;
//        }
//    }

    // 根据条件获取抓取记录详情
//    @RequestMapping(value = "/catchDetailByGameNum", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> catchDetailByGameNum(String gameNum, Integer userId, String token)
//            throws Exception {
//        logger.info("根据条件获取抓取记录详情参数：" + "gameNum=" + gameNum + "userId=" + userId + "," + "token=" + token);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            // 验证token有效性
//            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, userId)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
//                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//
//                return resultMap;
//            }
//            GameHistoryDetail gameHistoryDetail = null;
//            gameHistoryDetail = catchHistoryService.queryCatchDetailByGameNum(gameNum, userId);
//            if (gameHistoryDetail != null) {
//                resultMap.put("resultData", gameHistoryDetail);
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            logger.info("根据条件获取抓取记录详情resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("根据条件获取抓取记录详情出错", e);
//            throw e;
//        }
//    }


    // 获取游戏视频上传详情
//    @RequestMapping(value = "/uploadVideo", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> uploadVideo(String gameNum, String videoUrl, Integer userId, String token)
//            throws Exception {
//        logger.info("获取游戏视频上传详情参数：" + "gameNum=" + gameNum + ",videoUrl=" + videoUrl + ",userId=" + userId + "," + "token=" + token);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            // 验证token有效性
//            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, userId)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
//                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return resultMap;
//            }
//            if (videoUrl == null || "".equals(videoUrl)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//                logger.info("上传地址为空" + resultMap);
//                return resultMap;
//            }
//            Integer result = catchHistoryService.saveVideo(gameNum, videoUrl, userId);
//            if (result == 1) {
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            logger.info("获取游戏视频上传详情结果resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取游戏视频上传详情出错", e);
//            throw e;
//        }
//    }

    /**
     * 获取分享游戏视频地址接口
     *
     * @param gameNum 游戏编号
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/getVideoUrl", method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public ResultMap getVideoUrl(String gameNum)
//            throws Exception {
//        logger.info("获取分享游戏视频地址情参数：" + "gameNum=" + gameNum);
//        try {
//            String videoUrl = catchHistoryService.queryVideoUrl(gameNum);
//            if (StringUtils.isNotEmpty(videoUrl)) {
//                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, videoUrl);
//            } else {
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//        } catch (Exception e) {
//            logger.error("获取分享游戏视频地址出错", e);
//            throw e;
//        }
//    }
}
