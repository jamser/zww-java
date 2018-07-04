package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.GameService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.models.Account;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Created by SUN on 2018/3/1.
 * 用户邀请控制器
 */
@Controller
@RequestMapping(value = "/share")
@CrossOrigin
public class ShareController {
    private static final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private GameService gameService;
    @Autowired
    private ValidateTokenService validateTokenService;

    /**
     * 用户邀请分享图
     *
     * @param memberId
     * @param token
     * @return
     */
    @RequestMapping(value = "/shareImgUrl", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap share(Integer memberId, String token, Integer version) {
        try {
            //logger.info("邀请分享图接口参数:memberId=" + memberId + ",token=" + token);
            if (memberId == null || StringUtils.isEmpty(token)) {
                //logger.info("邀请分享图接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                //logger.info("邀请分享图接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            String shareUrl = gameService.getShareUrl(memberId, version);
            ResultMap resultMap = new ResultMap("right token");
            resultMap.setResultData(shareUrl);
            return resultMap;
        } catch (Exception e) {
            //logger.error("邀请分享图接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 用户邀请二维码图
     *
     * @param memberId
     * @param token
     * @return
     */
    @RequestMapping(value = "/QRCodeImgUrl", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap QRCodeImgUrl(Integer memberId, String token, Integer version, String index) {
        //logger.info("用户邀请二维码图 memberId=" + memberId);
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("用户邀请二维码图接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            //if (!validateTokenService.validataToken(token, memberId)) {
            //    logger.info("用户邀请二维码图接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            //    return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            //}
            Map<String, String> shareUrl = gameService.getQRCodeImgUrl(memberId, version, index);
            ResultMap resultMap = new ResultMap("right token");
            resultMap.setResultData(shareUrl);
            return resultMap;
        } catch (Exception e) {
            logger.error("邀请分享图接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }


    /**
     * 代理分享
     *
     * @param agentId
     * @param token
     * @param version
     * @param index
     * @return
     */
    @RequestMapping(value = "/AgentImgUrl", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap AgentImgUrl(@RequestParam Integer agentId,
                                 @RequestParam String token, Integer version, String index) {

        Map<String, String> shareUrl = gameService.getAgentImgUrl(agentId, version, index);
        ResultMap resultMap = new ResultMap("right token");
        resultMap.setResultData(shareUrl);
        return resultMap;

    }
}
