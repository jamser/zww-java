package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: mwan Version: 1.1 Date: 2017/09/28 Description: 用户信息编辑管理控制层.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/member/info")
@CrossOrigin
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private MemberService memberService;

    RedisUtil redisUtil = new RedisUtil();

    // 头像上传
    @RequestMapping(value = "/uploadPortrait", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request, String token, HttpSession session) throws Exception {
        String memberStr = request.getParameter("memberId").toString();
        logger.info("头像上传接口参数memberId" + Integer.parseInt(memberStr) + "token=" + token);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            int memberId = Integer.parseInt(memberStr);

            PropFileManager propFileMgr = new PropFileManager("interface.properties");
            String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");

            if (!file.isEmpty()) {
                //验证token有效性
                if (StringUtils.isEmpty(token)) {
                    token = request.getParameter("token");
                }
                if (token == null || "".equals(token) ||
                        !validateTokenService.validataToken(token)) {
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                    resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                    return resultMap;
                }
                String originalFileName = file.getOriginalFilename();
                // 获取后缀
                String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                // 修改后完整的文件名称
                String fileKey = StringUtils.getRandomUUID();
                String NewFileKey = "member/" + memberStr + "/" +fileKey + "." + suffix;

                byte[] bytes = file.getBytes();
                InputStream fileInputStream = new ByteArrayInputStream(bytes);

                // 设置用户属性
                Member member = memberService.selectById(memberId);
                member.setIconContextPath(ossBucketName);
                String oldFileKey = member.getIconFileName();
                member.setIconFileName(NewFileKey);
                logger.info("设置用户属性member=" + member);
                //memberService.updateByPrimaryKeySelective(member);
                // 上传到阿里云OSS
                logger.info("上传用户member " + member.getId() + "的头像到阿里云OSS BucketName(" + ossBucketName + ") "
                        + "文件名(" + NewFileKey + ")");

                if (!AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, fileInputStream)) {
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                    resultMap.put("message", "文件上传到阿里云OSS时失败！");
                    return resultMap;
                }

                //如果有则删除原来的头像
                if (!"".equals(oldFileKey) && oldFileKey != null) {
                    logger.info("删除用户member " + member.getId() + "原来的头像从阿里云OSS BucketName(" + ossBucketName + ") "
                            + "文件名(" + oldFileKey + ")");
                    AliyunServiceImpl.getInstance().deleteFileFromOSS(ossBucketName, oldFileKey);
                }
                String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, 1000000).toString();
                member.setIconRealPath(newFileUrl);
                memberService.updateByPrimaryKeySelective(member);
                //缓存中设置新头像地址
                redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(memberId), "iconRealPath", newFileUrl);

                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", "文件上传成功！");
                resultMap.put("fileUrl", newFileUrl);
                logger.info("上传头像resultMap" + resultMap);

            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", "文件大小为空！");
                return resultMap;
            }
        } catch (Exception e) {
            logger.error("头像上传出错", e);
            throw e;
        }

        return resultMap;
    }

    /**
     * 更新用户信息
     *
     * @param memberId 用户userId
     * @param name     新用户昵称
     * @param gender   新用户性别
     * @param birthday 新用户生日
     * @param token    登录凭证
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateMember", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateMember(Integer memberId, String name, String gender, String birthday, String token) {
        logger.info("更新用户信息接口参数memberId=" + memberId + "," + "name=" + name + "," + "gender=" + gender + "," + "birthday=" + birthday);
        Map<String, Object> resultMap = new HashedMap<>();
        try {
            // 验证token有效性
            if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                logger.info("更新用户信息失败" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return resultMap;
            }
            name = StringUtils.replaceBlank(name);
            if (StringUtils.isEmpty(name)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.FAILE_CODE);
                resultMap.put("message", "名字不能为空哦");
                //logger.info("更新用户信息失败" + "名称为空");
                return resultMap;
            }
            Member member = new Member();
            member.setId(memberId);
            if (StringUtils.isNotEmpty(birthday)) {
                member.setBirthday(TimeUtil.stringToDate(birthday.trim()));
            }
            String memberInfoKey = RedisKeyGenerator.getMemberInfoKey(member.getId());
            if (StringUtils.isNotEmpty(gender)) {
                member.setGender(gender);
                redisUtil.addHashSet(memberInfoKey, "gender", member.getGender());
            }
            if (StringUtils.isNotEmpty(name)) {
                member.setName(name);
                redisUtil.addHashSet(memberInfoKey, "name", member.getName());
            }
            Integer result = memberService.updateByPrimaryKeySelective(member);
            if (result != 0) {
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                resultMap.put("member", member);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("更新用户信息resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("更新用户信息出错", e.getMessage());
            e.printStackTrace();
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.ERROR_CODE);
            resultMap.put("message", Enviroment.HAVE_ERROR);
            return resultMap;
        }
    }

    /**
     * 更新用户操作系统
     *
     * @param memberId   用户userId
     * @param token      登录凭证
     * @param phoneModel 登录凭证
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateMemberPhoneModel", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap updateMemberPhoneModel(Integer memberId, String phoneModel, String token) {
        try {
            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(phoneModel)) {
                logger.info("更新用户操作系统接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("更新用户操作系统接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            int result = memberService.updateMemberPhoneModel(memberId, phoneModel);
            if (result == 1) {
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_FAILE_MESSAGE);
            }
        } catch (Exception e) {
            logger.error("更新用户操作系统信息出错", e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /***
     * 发送绑定手机验证码
     *
     * @param mobile
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendLinkMobileSMS", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap sendLinkMobileSMS(String mobile, String token) throws Exception {
        logger.info("发送绑定手机验证码短信接口参数mobile=" + mobile + "token" + token);
        try {
            if (mobile == null || "".equals(mobile) || token == null || "".equals(token)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token)) {
                logger.info("发送绑定手机验证码短信接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return memberService.sendLinkMobileSMS(mobile, token);
        } catch (Exception e) {
            logger.debug("发送绑定手机验证码短信异常" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }


    /**
     * 绑定手机
     *
     * @param memberId
     * @param mobile
     * @param code
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/linkMobile", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap linkMobile(Integer memberId, String mobile, String code, String token) throws Exception {
        logger.info("绑定手机接口参数memberId=" + "memberId=" + "," + "mobile=" + mobile + "," + "code=" + code + "," + "token=" + token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            //参数校验
            if (mobile == null || "".equals(mobile) || token == null || "".equals(token) ||
                    code == null || "".equals(code) || memberId == null || "".equals(memberId)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token有效性
            if (!validateTokenService.validataToken(token)) {
                logger.info("绑定手机接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return memberService.linkMobile(memberId, mobile, code);
        } catch (Exception e) {
            logger.debug("绑定手机异常" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 获得用户信息
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult getUserInfo(Integer memberId, String token) throws Exception {
        //logger.info("获取用户信息输入参数: memberId=" + memberId + " token=" + token);
        boolean isToken = validateTokenService.validataToken(token, memberId);
        try {
            if (isToken) {
                Member userInfo = memberService.getUserInfo(memberId);
                if (userInfo != null) {
                    //logger.info("获取用户信息成功");
                    return IcraneResult.ok(userInfo);
                } else {
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_FAILE_MESSAGE);
                }
            } else {
                return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "token已失效");
            }
        } catch (Exception e) {
            logger.error("获取用户信息出错", e);
            throw e;
        }
    }

    /**
     * 获得用户信息
     */
    @RequestMapping(value = "/superUser", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap superUser(String mima) throws Exception {
        try {
            if ("mima".equals(mima)) {
                String[] superUsers = memberService.getSuperUsers();
                return new ResultMap("密码正确", superUsers);
            } else {
                return new ResultMap(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE, "密码错误");
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
