package com.bfei.icrane.api.controller;

import com.bfei.icrane.common.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bfei.icrane.api.service.LoginService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/member")
@CrossOrigin
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /*@Autowired
    private MemberService memberService;
    @Autowired
    private ValidateTokenService validateTokenService;
    private RedisUtil redisUtil = new RedisUtil();*/
    @Autowired
    private LoginService loginService;


    /**
     * 短信验证码快速登录
     *
     * @param mobile     手机号码
     * @param smsCode    短信验证码
     * @param from       登录用户手机系统
     * @param channel    渠道信息
     * @param phoneModel 登陆用户手机型号
     * @param IMEI       登陆用户手机型号
     * @return
     * @throws Exception 登录结果
     */
//    @RequestMapping(value = "/smsCodeLogin", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap smsCodeLogin(HttpServletRequest request, String mobile, String smsCode, String from, String channel, String phoneModel, String IMEI) throws Exception {
//        logger.info("短信验证码登录接口参数mobile=" + mobile);
//        try {
//            if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(mobile)) {
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.MOBILE_ERROR);
//            }
//            return loginService.smsCodeLogin(mobile, smsCode, from, channel, phoneModel, IMEI, HttpClientUtil.getIpAdrress(request));
//        } catch (Exception e) {
//            //logger.debug("短信验证码快速登录出错" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.SMSCODELOGIN_ERROR);
//        }
//    }

    /**
     * 发送快速登录验证码短信
     *
     * @param mobile 手机号码
     * @return 短信发送结果
     * @throws Exception
     */
    @RequestMapping(value = "/getSmsCodeLogin", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getSmsCodeLogin(String mobile) throws Exception {
        logger.info("发送快速登录验证码短信接口参数mobile=" + mobile);
        try {
            if (StringUtils.isEmpty(mobile)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.MOBILE_ERROR);
            }
            return loginService.getSmsCodeLogin(mobile);
        } catch (Exception e) {
            logger.debug("发送快速登录验证码短信异常" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.SENDSMSCODELOGIN_ERROR);
        }
    }


    /**
     * 用户登出接口
     *
     * @param id
     * @param token
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "/logoff", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap logoff(Integer id, String token) throws Exception {
//        logger.info("用户登出接口参数memberId=" + id);
//        try {
//            if (id == null || StringUtils.isEmpty(token)) {
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            return loginService.logout(id);
//        } catch (Exception e) {
//            logger.debug("用户登出接口异常" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.LOGIN_ERROR);
//        }
//    }



    /**
     * 定位到登录页面
     *
     * @param mobile
     * @param password
     * @return
     * @throws Exception
     */
    /*@RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public Map<String, Object> login(String mobile, String weixinId, String password, String lastLoginFrom, String channel, String phoneModel) throws Exception {
        logger.info("登录接口参数" + "mobile=" + mobile);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            if (mobile == null && weixinId == null) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", "用户名或密码错误");
                return resultMap;
            }
//			if (weixinId != null) {
//				Member wxmember = memberService.selectByOpenId(weixinId);
////				if (wxmember.getPassword().equals(password)) {
////
////				}
//			}
            String mpassword = password;
            Member member = null;
            String token = "";//StringUtils.getRandomUUID();
            if (mobile != null) {
                member = memberService.login(mobile, mpassword);
                token = StringUtils.getMobileToken();
            } else if (weixinId != null) {
                member = memberService.selectByOpenId(weixinId);
                token = StringUtils.getWxToken();
            }
            logger.info("用户信息member=" + member);
            MemberToken mtoken = new MemberToken();
            if (member != null) {
                if (member.getActiveFlg() == false) {
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                    resultMap.put("message", "账号已被禁用");
                    return resultMap;
                }
                redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(member.getId()), "memberID", member.getMemberID());
                redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(member.getId()), "name", member.getName());
                redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(member.getId()), "gender", member.getGender());
                if (member.getIconRealPath() == null) {
                    member.setIconRealPath("");
                }
                redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(member.getId()), "iconRealPath",
                        member.getIconRealPath());
                // if(member.getIconContextPath()!=null&&!"".equals(member.getIconContextPath()))
                // {
                // member.setHeadUrl(AliyunServiceImpl.getInstance()
                // .generatePresignedUrl(member.getIconContextPath(), member.getIconFileName(),
                // 1).toString());
                // }
                member.setLoginChannel(channel);
                member.setPhoneModel(phoneModel);

                member.setOnlineFlg(true);
                mtoken.setToken(token);
                mtoken.setMemberId(member.getId());
                logger.info("插入token=" + mtoken);
                Integer result = memberService.updateLogin(member, mtoken, lastLoginFrom);
                logger.info("插入token结果result" + result);
                //redisUtil.setString(token, "", 3600 * 24);
                redisUtil.setString(token, String.valueOf(member.getId()), 3600 * 24);

                resultMap.put("resultData", member);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                resultMap.put("token", token);
            } else if (member == null) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", "用户名或密码错误");
            }
            logger.info("登录resultMap" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("登录出错", e);
            throw e;
        }
    }*/


    // 获取注册短信验证码
    /*@RequestMapping(value = "/getSmsCode", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public Map<String, Object> getSmsCode(String mobile) throws Exception {
        logger.info("获取注册短信验证码接口参数mobile" + mobile);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            PropFileManager propFileMgr = new PropFileManager("interface.properties");
            Member member = memberService.selectByMobile(mobile);
            logger.info("验证用户是否存在member=" + member);
            // 验证用户是否存在
            if (member == null) {
                // 先查手机号 如果有 就删除
                *//*MemberSmscode mbmsCode = memberService.validateSmsCodeByMobile(mobile);
                if (mbmsCode != null) {
                    memberService.deleteSmscode(mobile);
                }*//*
                // 生成短信验证码
                String smsCode = StringUtils.getSmsCode();
                *//*
                MemberSmscode mSmsCode = new MemberSmscode();
                while (memberService.validateSmsCodeByCode(smsCode) != null) {
                    smsCode = StringUtils.getSmsCode();
                }
                mSmsCode.setMobile(mobile);
                mSmsCode.setSmscode(smsCode);
                mSmsCode.setValidStartTime(TimeUtil.getTime());
                mSmsCode.setValidEndTime(TimeUtil.getEndTime());
                logger.info("插入验证码mSmsCode" + mSmsCode);
                memberService.insertSmsCode(mSmsCode);*//*
                //验证码信息存入redis
                redisUtil.setString(RedisKeyGenerator.getCodeFindPwdKey(mobile), smsCode, Enviroment.SMS_ENDTIME);

                // 发送短信
                if (AliyunServiceImpl.getInstance().sendSMSForCode(mobile, "三六五抓娃娃",
                        propFileMgr.getProperty("aliyun.smsModelCode.reg"), smsCode)) {
                    resultMap.put("success", Enviroment.RETURN_SUCCESS);
                    resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                    resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                } else {
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                    resultMap.put("message", Enviroment.SENDSMSCODELOGIN_ERROR);
                }
                ;

            } else {
                // 手机号已注册
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.SENDSMSCODELOGIN_ERROR);
            }
            logger.info("获取验证码resultMap" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取注册短信验证码出错", e);
            throw e;
        }
    }*/

    /**
     * 验证短信验证码
     *
     * @param mobile  手机号
     * @param smsCode 验证码
     * @return 验证结果
     * @throws Exception
     */
    /*@RequestMapping(value = "/authBySmsCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap authBySmsCode(String mobile, String smsCode) throws Exception {
        logger.info("通过手机号码和短信验证码授权接口参数mobile=" + mobile + "," + "smsCode=" + smsCode);
        try {
            //根据手机号码获取用户信息
            Member member = memberService.selectByMobile(mobile);
            // 验证用户是否存在
            if (member == null) {
                logger.info("获取注册短信验证码异常=" + Enviroment.INFORMATION_NOT_EXIST);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.INFORMATION_NOT_EXIST);
            }
            // 删除已存在的验证码
            //MemberSmscode mbmsCode = memberService.validateSmsCode(mobile, smsCode);
            String codeFindPwdKey = RedisKeyGenerator.getCodeFindPwdKey(mobile);
            String code = redisUtil.getString(codeFindPwdKey);
            if (code != null && code.equals(smsCode)) {
                String token = memberService.generateToken(member.getId());
                // 授权成功返回新生成的token
                ResultMap resultMap = new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE);
                resultMap.setToken(token);
                redisUtil.setString(token, "");
                redisUtil.delKey(codeFindPwdKey);
                logger.info("通过手机号码和短信验证码授权=" + Enviroment.RETURN_SUCCESS_MESSAGE);
                return resultMap;
            } else {
                // 验证码错误
                logger.info("通过手机号码和短信验证码授权异常=" + Enviroment.VERIFICATION_CODE_ERROR);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.VERIFICATION_CODE_ERROR);
            }
        } catch (Exception e) {
            logger.error("通过手机号码和短信验证码授权出错", e);
            throw e;
        }
    }*/

    /**
     * 发送修改密码短信验证码
     *
     * @param mobile 手机号码
     * @return 短信发送结果
     * @throws Exception
     */
    /*@RequestMapping(value = "/getSmsCodeFindPwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getSmsCodeFindPwd(String mobile) throws Exception {
        logger.info("获取注册短信验证码参数mobile=" + mobile);
        try {
            //根据手机号码查询用户信息
            Member member = memberService.selectByMobile(mobile);
            if (member == null) {
                logger.info("获取注册短信验证码异常=" + Enviroment.INFORMATION_NOT_EXIST);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.INFORMATION_NOT_EXIST);
            }
            //根据手机号验证短信code是否重复
            MemberSmscode mbmsCode = memberService.validateSmsCodeByMobile(mobile);
            // 删除已存在的验证码
            if (mbmsCode != null) {
                memberService.deleteSmscode(mobile);
            }
            MemberSmscode mSmsCode = new MemberSmscode();
            // 生成短信验证码
            String smsCode = StringUtils.getSmsCode();
            *//*while (memberService.validateSmsCodeByCode(smsCode) != null) {
                smsCode = StringUtils.getSmsCode();
            }
            mSmsCode.setMobile(mobile);
            mSmsCode.setSmscode(smsCode);
            mSmsCode.setValidStartTime(TimeUtil.getTime());
            mSmsCode.setValidEndTime(TimeUtil.getEndTime(120000));
            memberService.insertSmsCode(mSmsCode);*//*
            //验证码信息存入redis
            redisUtil.setString(RedisKeyGenerator.getCodeFindPwdKey(mobile), smsCode, Enviroment.SMS_ENDTIME);
            //获取配置文件
            PropFileManager propFileMgr = new PropFileManager("interface.properties");
            // 发送短信
            if (AliyunServiceImpl.getInstance().sendSMSForCode(mobile, "三六五抓娃娃",
                    propFileMgr.getProperty("aliyun.smsModelCode.reg"), smsCode)) {
                logger.info("获取注册短信验证码成功=" + Enviroment.TEXT_MESSAGING_SUCCESS);
                return new ResultMap(Enviroment.TEXT_MESSAGING_SUCCESS);
            } else {
                logger.info("获取注册短信验证码异常=" + Enviroment.TEXT_MESSAGING_FAILURE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.TEXT_MESSAGING_FAILURE);
            }
        } catch (Exception e) {
            logger.error("获取找回密码验证码出错", e);
            throw e;
        }
    }*/

    /**
     * 注册
     *
     * @param mobile
     * @param password
     * @param smsCode
     * @param registerFrom
     * @param channel
     * @param phoneModel
     * @return
     * @throws Exception
     */
    /*@RequestMapping(value = "/register", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> register(String mobile, String password, String smsCode, String registerFrom, String channel, String phoneModel) throws Exception {
        logger.info("注册接口参数mobile=" + mobile + "," + "password=" + password + "," + "smsCode=" + smsCode);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            String codeKey = RedisKeyGenerator.getCodeFindPwdKey(mobile);
            String codeFindPwdKey = redisUtil.getString(codeKey);
            if (codeFindPwdKey != null) {
                if (smsCode.equals(codeFindPwdKey)) {
                    Member memb = new Member();
                    PrefSet ps = new PrefSet();
                    memb.setMobile(mobile);
                    // memb.setPassword(StringUtils.EncoderByMd5(password));
                    memb.setPassword(password);
                    memb.setGender("n");
                    String memberID = StringUtils.getMemberCode();
                    while (memberService.selectByMemberID(memberID) != null) {
                        memberID = StringUtils.getMemberCode();
                    }
                    memb.setMemberID(memberID);
                    // ps.setMemberId(memberId);
                    // logger.info("注册service参数memb="+memb+","+"ps"+ps);
                    if (registerFrom != null) {
                        memb.setRegisterFrom(registerFrom);
                    }
                    memb.setRegisterChannel(channel);
                    memb.setPhoneModel(phoneModel);
                    memb.setCoins(0);
                    memb.setFirstCharge(0);
                    memb.setFirstLogin(0);
                    memberService.insertRegister(memb, ps);
                    resultMap.put("success", Enviroment.RETURN_SUCCESS);
                    resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                    resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                    redisUtil.delKey(codeKey);
                    return resultMap;
                } else {
                    // 验证码错误
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                    resultMap.put("message", "验证码错误");
                }
            } else {
                // 验证码已过期
                memberService.deleteSmscode(mobile);
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", "验证码已过期或者手机号码错误");
            }
            logger.info("注册resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("注册出错", e);
            throw e;
        }
    }*/

    /**
     * 修改密码
     *
     * @param mb
     * @param token
     * @return
     * @throws Exception
     */
    /*@RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @ResponseBody
    public IcraneResult updatePwd(Member mb, String token) throws Exception {
        logger.info("修改密码接口参数mb=" + mb + "," + "token=" + token);
        try {
            boolean isToken = validateTokenService.validataToken(token);
            if (isToken) {
                // mb.setPassword(StringUtils.EncoderByMd5(mb.getPassword()));
                // mb.setPassword(mb.getPassword());
                Member codeBymobile = memberService.getCodeBymobile(mb.getMobile());
                logger.info("用户邀请码:{}", codeBymobile.getMemberID());
                EasemobIMUsersController em = new EasemobIMUsersController();
                NewPassword psd = new NewPassword().newpassword(mb.getPassword());
                Object emResult = em.modifyIMUserPasswordWithAdminToken(codeBymobile.getMemberID(), psd);
                logger.info(emResult.toString());
                int result = memberService.updatePwd(mb);
                redisUtil.delKey(token);
                if (result > 0) {
                    return IcraneResult.ok();
                } else {
                    return IcraneResult.build(Enviroment.RETURN_FAILE, Enviroment.RETURN_FAILE_CODE,
                            Enviroment.RETURN_FAILE_MESSAGE);
                }
            } else {
                return IcraneResult.build(Enviroment.RETURN_FAILE, "400", "token已失效");
            }
        } catch (Exception e) {
            logger.error("修改密码出错", e);
            throw e;
        }
    }*/
}
