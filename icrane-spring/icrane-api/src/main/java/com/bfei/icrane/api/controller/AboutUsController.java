package com.bfei.icrane.api.controller;

import java.util.Map;

import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bfei.icrane.api.service.AboutUsService;
import com.bfei.icrane.api.service.AppVersionService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.core.models.SystemPref;
import com.bfei.icrane.core.models.TAppVersion;
import com.bfei.icrane.core.service.ValidateTokenService;

/**
 * Author: mwan Version: 1.1 Date: 2017/09/28 Description: 用户信息编辑管理控制层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/aboutUs")
@CrossOrigin
public class AboutUsController {

    private static final Logger logger = LoggerFactory.getLogger(AboutUsController.class);
    @Autowired
    private AboutUsService aboutUsService;
    @Autowired
    private AppVersionService appVersionService;

    /**
     * 关于我们
     *
     * @param code
     * @returnk
     * @throws Exception
     */
    @RequestMapping(value = "/getAboutUs", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getAboutUs(String code) throws Exception {
        //logger.info("关于我们接口参数code=" + code);

        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            SystemPref systemPref = aboutUsService.selectByPrimaryKey(code);
            logger.info("获取关于我们systemPref=" + systemPref);
            if (systemPref != null) {
                //logger.info("获取关于我们systemPref=" + systemPref.getCode() + systemPref.getName() + systemPref.getValue());
                resultMap.put("resultData", systemPref);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);

            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            //logger.info("关于我们resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取关于我们出错", e);
            throw e;
        }
    }

    /**
     * 系统参数
     *
     * @param code 参数code
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/systemPref", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap systemPref(String code) throws Exception {
//        try {
//            if (StringUtils.isEmpty(code)) {
//                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, aboutUsService.selectAll());
//            }
//            SystemPref systemPref = aboutUsService.selectByPrimaryKey(code);
//            if (systemPref != null) {
//                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, systemPref);
//            } else {
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
//            }
//        } catch (Exception e) {
//            logger.error("获取系统参数出错", e);
//            throw e;
//        }
//    }

    // 获取版本号

    /**
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/getVersion", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> getVersion(String appKey, String token) throws Exception {
//        //logger.info("获取版本号接口参数appKey=" + appKey);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            // 验证token有效性
//            /*if (token == null || "".equals(token) || !validateTokenService.validataToken(token)) {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//				resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
//				resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//				return resultMap;
//			}*/
//            if (StringUtils.isEmpty(appKey)){
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//				resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE1);
//				resultMap.put("message", Enviroment.RETURN_INVALID_PARA_MESSAGE);
//				return resultMap;
//            }
//            TAppVersion version = appVersionService.selectByPrimaryKey(appKey);
//            //logger.info("获取版本号version=" + version);
//            if (version != null) {
//                //logger.info("获取版本号version=" + version);
//                resultMap.put("resultData", version);
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            //logger.info("获取版本号resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取版本号出错", e);
//            throw e;
//        }
//    }

    // 获取版本隐藏

    /**
     * @param appKey
     * @param version
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/getVersionHide", method = RequestMethod.POST)
//    @ResponseBody
//    public Map<String, Object> getVersionHide(String appKey, String version) throws Exception {
//        //logger.info("获取版本隐藏接口参数appKey=" + appKey + "version=" + version);
//        Map<String, Object> resultMap = new HashedMap<String, Object>();
//        try {
//            TAppVersion versionHide = appVersionService.selectVersionHide(appKey, version);
//            //logger.info("获取版本隐藏versionHide=" + versionHide);
//                /*if (version != null) {
//                    resultMap.put("resultData", versionHide);
//					resultMap.put("success", Enviroment.RETURN_SUCCESS);
//					resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
//					resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
//				} else {
//					resultMap.put("success", Enviroment.RETURN_FAILE);
//					resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//					resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//				}*/
//            Integer curVersion = Integer.parseInt(version.replace(".", ""));
//            Integer dbVersion = Integer.parseInt(versionHide.getVersion().replace(".", ""));
//            if (versionHide != null && curVersion < dbVersion) {
//                //logger.info("获取版本号version=" + versionHide);
//                //resultMap.put("resultData", versionHide);
//                //versionHide = new TAppVersion();
//                //versionHide.setAppKey("iOS");
//                //versionHide.setUpgradeUrl("itms-apps://itunes.apple.com/cn/app/id1314921684?mt=8");
//                resultMap.put("resultData", versionHide);
//                resultMap.put("success", Enviroment.RETURN_SUCCESS);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            } else {
//                resultMap.put("success", Enviroment.RETURN_FAILE);
//                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
//                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
//            }
//            //logger.info("获取版本隐藏resultMap=" + resultMap);
//            return resultMap;
//        } catch (Exception e) {
//            logger.error("获取版本隐藏出错", e);
//            throw e;
//        }
//    }
}
