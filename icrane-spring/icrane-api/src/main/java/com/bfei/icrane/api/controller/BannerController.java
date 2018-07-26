package com.bfei.icrane.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/banner")
@CrossOrigin
public class BannerController {
    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);
//
//        @Autowired
//        private BannerService bannerService;
//
//        @Autowired
//        private ValidateTokenService validateTokenService;
//
//        /**
//         * 滚动横幅
//         *
//         * @param token
//         * @return
//         */
//        @RequestMapping(value = "/scrollingBanner", method = RequestMethod.POST)
//        @ResponseBody
//        public ResultMap scrollingBanner(String token, String head) {
//            try {
//                logger.info("滚动横幅接口参数:token=" + token);
//                if (!"ios".equals(head)) {
//                    if (StringUtils.isEmpty(token)) {
//                        logger.info("滚动横幅接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                    }
//
//                    //验证token
//                    if (!validateTokenService.validataToken(token)) {
//                        logger.info("滚动横幅接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                    }
//                }
//                return bannerService.selectscrollingBanner();
//            } catch (Exception e) {
//                logger.error("滚动横幅接口参数异常=" + e.getMessage());
//                e.printStackTrace();
//                return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//            }
//        }
//
//        /**
//         * 弹窗广告
//         *
//         * @param token
//         * @return
//         */
//        @RequestMapping(value = "/popUpAd", method = RequestMethod.POST)
//        @ResponseBody
//        public ResultMap popUpAd(String token, String head,Integer version) {
//            try {
//                //logger.info("弹窗广告接口参数:token=" + token);
//                if (!"ios".equals(head)) {
//                    if (StringUtils.isEmpty(token)) {
//                        //logger.info("滚动横幅接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                    }
//                    //验证token
//                    if (!validateTokenService.validataToken(token)) {
//                        //logger.info("弹窗广告接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                    }
//                }
//                return bannerService.selectPopUpAd(version);
//            } catch (Exception e) {
//                logger.error("弹窗广告接口参数异常=" + e.getMessage());
//                e.printStackTrace();
//                return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//            }
//        }
//
//        /**
//         * 启动页
//         *
//         * @param token
//         * @return
//         */
//        @RequestMapping(value = "/startPage", method = RequestMethod.POST)
//        @ResponseBody
//        public ResultMap startPage(String token, String head, Integer version) {
//            try {
//                logger.info("启动页接口参数:token=" + token);
//                /*if (!"ios".equals(head)) {
//                    if (StringUtils.isEmpty(token)) {
//                        logger.info("启动页接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                    }
//                    //验证token
//                    if (!validateTokenService.validataToken(token)) {
//                        logger.info("启动页接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                        return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                    }
//                }*/
//                return bannerService.selectStartPage(version);
//            } catch (Exception e) {
//                logger.error("启动页接口参数异常=" + e.getMessage());
//                e.printStackTrace();
//                return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//            }
//        }
//
//        /**
//         * 获取banner
//         *
//         * @param token   身份令牌
//         * @param version 新老版本兼容
//         * @param head    手机系统类别(ios就不用验证token)
//         * @return
//         * @throws Exception
//         */
//        @RequestMapping(value = "/getBanner", method = RequestMethod.POST)
//        @ResponseBody
//        public Map<String, Object> getBanner(String token, Integer version, String head)
//                throws Exception {
//            Map<String, Object> resultMap = new HashedMap<>();
//        try {
//            //logger.info("获取banner接口参数：" + "token=" + token + ",version=" + version + ",head=" + head);
//            // 验证token有效性(ios除外)
//            /*if (!"ios".equals(head)) {
//                if (StringUtils.isEmpty(token)) {
//                    logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                    resultMap.put("success", Enviroment.RETURN_FAILE);
//                    resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE1);
//                    resultMap.put("message", Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                    return resultMap;
//                }
//                if (!validateTokenService.validataToken(token)) {
//                    logger.info("获取banner接口异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                    resultMap.put("success", Enviroment.RETURN_FAILE);
//                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                    resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                    return resultMap;
//                }
//            }*/
//            List<Banner> list = bannerService.selectActiveBanner(version);
//            if (version == null) {
//                list.remove(0);
//            }
//            resultMap.put("list", list);
//            resultMap.put("success", Enviroment.RETURN_SUCCESS);
//            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            logger.info("获取banner成功");
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取banner出错", e);
//            e.printStackTrace();
//            resultMap.put("success", Enviroment.RETURN_SUCCESS);
//            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            return resultMap;
//        }
//    }
//
//    @RequestMapping(value = "/getIosBanner", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> getIosBanner(Integer version) throws Exception {
//        logger.info("获取banner接口参数");
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            //获取banner
//            List<Banner> list = bannerService.selectActiveBanner(version);
//            //logger.info("获取banner参数list=" +list);
//            if (version == null) {
//                list.remove(0);
//            }
//            resultMap.put("list", list);
//            resultMap.put("success", Enviroment.RETURN_SUCCESS);
//            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            logger.info("获取banner结果resultMap=" + Enviroment.RETURN_SUCCESS);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取banner出错", e);
//            throw e;
//        }
//    }
}
