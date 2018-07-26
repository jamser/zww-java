package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.SignedSheetService;
import com.bfei.icrane.api.service.SystemPrefService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 签到  action
 */
@Controller
@RequestMapping(value = "/signedSheet")
@CrossOrigin
public class SignedSheetController {

    private static final Logger logger = LoggerFactory.getLogger(SignedSheetController.class);
    @Autowired
    SignedSheetService signedSheetService;
    @Autowired
    ValidateTokenService validateTokenService;

    /**
     * (旧)签到
     *
     * @param memberId
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/popover", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap popover(@RequestParam(value = "memberId") String memberId) throws Exception {
//        try {
//            logger.info("(旧)签到参数memberId=" + memberId);
//            //验证参数
//            if (StringUtils.isEmpty(memberId)) {
//                logger.info("(旧)签到接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //访问间隔限制
//            RedisUtil redisUtil = new RedisUtil();
//            if (redisUtil.getString("popover" + memberId) != null) {
//                logger.info("(旧)签到接口签到失败=" + Enviroment.PLEASE_SLOW_DOWN);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PLEASE_SLOW_DOWN);
//            }
//            redisUtil.setString("popover" + memberId, "", 20);
//            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "签到失败,请下载新版本APP");
//        } catch (Exception e) {
//            logger.info("(旧)签到接口参数异常=" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.SIGN_IN_ABNORMAL);
//        }
        /*logger.info("签到接口参数memberId=" + memberId);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        resultMap.put("message", "签到失败,请下载新版本");
        resultMap.put("success", Enviroment.RETURN_FAILE);
        resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
        return resultMap;
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SignedSheet existSigned = signedSheetService.selectOneByMemberID(memberId);
            //判断最近的签到是否为当前系统时间
            if (existSigned != null && TimeUtil.isSameDate(existSigned.getSignedDate(), new Date())) {
                logger.info("已签到,重复签到用户" + existSigned.getMemberId() + existSigned.getMemberName());
                resultMap.put("message", "签到失败,不能重复签到");
                resultMap.put("resultData", 0);
                logger.info("已签到过resultMap=" + resultMap);
                return resultMap;
            } else {
                //签到
                SignedSheet signedSheet = new SignedSheet();
                signedSheet.setSignedDate(new Date());
                signedSheet.setCreateTime(new Date());
                signedSheet.setMemberId(memberId);
                String memberName = memberService.selectByMemberID(memberId).getName();
                signedSheet.setMemberName(memberName);
                signedSheet.setSigned(1);
                signedSheetService.insert(signedSheet);
                resultMap.put("message", "签到成功");
                logger.info("签到成功resultMap=" + resultMap);
            }
            resultMap.put("resultData", 1);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取关于我们出错", e);
            resultMap.put("resultData", 0);
            resultMap.put("message", "签到异常");
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            return resultMap;
        }*/
//    }

    /**
     * 检测是否签到
     *
     * @param memberId
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/haspopover", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> haspopover(@RequestParam(value = "memberId") String memberId, HttpServletRequest request) throws Exception {
//        logger.info("关于我们接口参数memberId=" + memberId);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            SignedSheet existSigned = signedSheetService.selectOneByMemberID(memberId);
//            Date currDate = new Date();
//            //判断最近的签到是否为当前系统时间
//            if (existSigned != null && TimeUtil.isSameDate(existSigned.getSignedDate(), new Date())) {
//                logger.info("已签到,重复签到用户" + existSigned.getMemberId() + existSigned.getMemberName());
//                resultMap.put("message", "签到失败,不能重复签到");
//                resultMap.put("resultData", 0);
//                logger.info("已签到过resultMap=" + resultMap);
//                return resultMap;
//            }
//            String head = request.getHeader("head");//android 有head值
//            logger.info("getDollList********请求head=" + head);
//            //IOS功能开关
//            SystemPref syspref = systemPrefService.selectByPrimaryKey("IOS_STATE");
//            if ("0".equals(syspref.getValue()) && head == null) {
//                logger.info("IOS显示已签到,关闭签到" + existSigned.getMemberId() + existSigned.getMemberName());
//                resultMap.put("message", "签到失败,不能重复签到");
//                resultMap.put("resultData", 0);
//                logger.info("已签到过resultMap=" + resultMap);
//                return resultMap;
//            }
//            signedSheetService.giveCoins(memberId);
//            resultMap.put("resultData", 2);
//            resultMap.put("message", "未签到");
//            resultMap.put("success", Enviroment.RETURN_SUCCESS);
//            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取关于我们出错", e);
//            resultMap.put("resultData", 0);
//            resultMap.put("message", "签到异常");
//            resultMap.put("success", Enviroment.RETURN_SUCCESS);
//            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//            return resultMap;
//        }
//    }

    /**
     * 连续签到接口
     *
     * @param memberId 用户UserId
     * @param token    用户Token
     * @return
     */
//    @RequestMapping(value = "/sign", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap sign(String memberId, String token) {
//        try {
//            logger.info("连续签到参数memberId=" + memberId + "token" + token);
//            //验证参数
//            if (StringUtils.isEmpty(memberId) || StringUtils.isEmpty(token)) {
//                logger.info("连续签到接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //访问间隔限制
//            RedisUtil redisUtil = new RedisUtil();
//            if (redisUtil.getString("sign" + memberId) != null) {
//                logger.info("连续签到失败=" + Enviroment.PLEASE_SLOW_DOWN);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PLEASE_SLOW_DOWN);
//            }
//            redisUtil.setString("sign" + memberId, "", Enviroment.ACCESS_CONTROL_TIME);
//            //验证token
//            if (!validateTokenService.validataToken(token, Integer.valueOf(memberId))) {
//                logger.info("连续签到接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            return signedSheetService.sign(memberId);
//        } catch (Exception e) {
//            logger.info("连续签到接口参数异常=" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.SIGN_IN_ABNORMAL);
//        }
//    }

    /**
     * 连续签到检测接口
     *
     * @param memberId 用户UserId
     * @param token    用户Token
     * @return
     */
//    @RequestMapping(value = "/hassign", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap hassign(String memberId, String token, String head) {
//        try {
//            logger.info("连续签到状态检测参数memberId=" + memberId + "token" + token);
//            //验证参数
//            if (memberId == null || token == null || "".equals(token) || "".equals(memberId)) {
//                logger.info("连续签到状态检测接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //验证token
//            if (!validateTokenService.validataToken(token, Integer.valueOf(memberId))) {
//                logger.info("连续签到状态检测接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            return signedSheetService.hassign(memberId, head);
//        } catch (Exception e) {
//            logger.error("获取连续签到状态出错", e);
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.GETS_SIGN_STATUS_EXCEPTION);
//        }
//    }
    @RequestMapping(value = "/isOpen", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap isOpen(@RequestParam String memberId, @RequestParam String token) {
        try {
            if (StringUtils.isEmpty(memberId)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, Integer.valueOf(memberId))) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return signedSheetService.isOpen(memberId);
        } catch (Exception e) {
            logger.error("获取转盘开启失败", e);
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.GETS_SIGN_STATUS_EXCEPTION);
        }
    }


    @RequestMapping(value = "/getTurnAngle", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getTurnAngle(@RequestParam String memberId, @RequestParam String token) {
        try {
            //验证token
            if (!validateTokenService.validataToken(token, Integer.valueOf(memberId))) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return signedSheetService.getTurnProbability(memberId);
        } catch (Exception e) {
            logger.error("获取转盘角度失败", e);
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.GETS_SIGN_STATUS_EXCEPTION);
        }
    }
}
