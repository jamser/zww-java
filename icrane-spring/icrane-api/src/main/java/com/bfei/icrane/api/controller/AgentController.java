package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.AgentService;
import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.form.*;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentToken;
import com.bfei.icrane.core.models.Member;
import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.pojos.AgentPojo;
import com.bfei.icrane.core.service.AdvertisementInfoService;
import com.bfei.icrane.core.service.OemService;
import com.bfei.icrane.core.service.SysNotifyService;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moying on 2018/6/1.
 */
@Controller
@RequestMapping(value = "/agent")
@CrossOrigin
public class AgentController {

    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private ValidateTokenService validateTokenService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AdvertisementInfoService advertisementInfoService;

    @Autowired
    private SysNotifyService sysNotifyService;

    @Autowired
    private OemService oemService;

    @Autowired
    private MemberService memberService;


    private RedisUtil redisUtil = new RedisUtil();

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentLogin(@Valid AgentLoginForm agentLoginForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【代理登陆】参数不正确, agentLoginForm={}", agentLoginForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
        Agent agent = agentService.selectByUserName(agentLoginForm.getUsername());

        if (null == agent) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.USERNAME_ERROR);
        } else {
            if (agent.getStatus() == 1 || agent.getStatus() == 2) {

                if (agent.getPassword().equals(MD5Utils.md5(agentLoginForm.getPassword(), agent.getSalt()))) {
                    AgentToken token = agentService.getAgentLogin(agent);
                    if (null == token) {
                        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.AGENT_LOGIN_ERROR);
                    } else {
                        Oem oem = null;
                        if (agent.getIsOem()) {
                            oem = oemService.selectOemById(agent.getId());
                        } else if (agent.getLevel() != 0) {
                            oem = oemService.selectOemById(agent.getAgentId());
                        }
                        if (null == oem) {
                            oem = oemService.selectByCode("lanaokj");
                        }
                        token.setHost(oem.getUrl());
                        return new ResultMap(Enviroment.RETURN_SUCCESS_CODE, token);
                    }
                }
            } else {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.AGENT_LOGIN_FAILE);
            }
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.PASSWORD_ERROR);
        }
    }

    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap loginOut(@RequestParam Integer memberId, @RequestParam Integer agentId, String token) {

        Member member = memberService.selectById(memberId);
        if (ObjectUtils.isEmpty(member)) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "用户不存在");
        }
        Oem oem = oemService.selectByCode(member.getRegisterChannel());
        Map<String, String> map = new HashMap<>();
        map.put("host", oem.getUrl());
        return new ResultMap(Enviroment.RETURN_SUCCESS_CODE, map);
    }

    @ResponseBody
    @RequestMapping(value = "/uploadPortrait", method = RequestMethod.POST)
    public ResultMap uploadPortrait(@RequestParam("file") MultipartFile file,
                                    @RequestParam("agentId") Integer agentId, @RequestParam("token") String token) throws Exception {
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }

        try {
            PropFileManager propFileMgr = new PropFileManager("interface.properties");
            String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");
            if (!file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                // 获取后缀
                String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                // 修改后完整的文件名称
                String fileKey = StringUtils.getRandomUUID();
                String NewFileKey = "agent/Profile/" + fileKey + "." + suffix;

                byte[] bytes = file.getBytes();
                InputStream fileInputStream = new ByteArrayInputStream(bytes);
                if (!AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, fileInputStream)) {
                    return new ResultMap(Enviroment.RETURN_FAILE_CODE, "文件上传到阿里云OSS时失败");
                }
                String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, 1000000).toString();
                agentService.updateAgentProfile(agentId, newFileUrl);
                Map<Object, Object> map = new HashMap<>();
                map.put("fileUrl", newFileUrl);
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, map);
            } else {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "文件大小为空");
            }
        } catch (Exception e) {
            logger.error("银行卡上传出错", e);
            throw e;
        }
    }


    /**
     * 添加代理商
     *
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap addAgent(@Valid AgentForm agentForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【添加代理】参数不正确, agentForm={}", agentForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
//        验证token
        if (!validateTokenService.validataAgentToken(agentForm.getToken(), agentForm.getAgentId())) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        Agent agent = agentService.selectByPrimaryKey(agentForm.getAgentId());
        if (agent.getLevel() == 0 || agent.getLevel() == 1 || agent.getLevel() == 2) {

            Agent newAgent = agentService.selectByUserName(agentForm.getUsername());
            if (null != newAgent) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.USERNAME_EXIST);
            }
            if (agentService.insertAgent(agent, agentForm) == 1) {
                return new ResultMap(Enviroment.ADD_AGENT_SUCCESS);
            }
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.ADD_AGENT_ERROER);
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.ADD_AGENT_ERROER_MESSAGE);
    }


    /**
     * 代理商信息
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/userInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAccount(@RequestParam(value = "agentId") Integer agentId,
                                @RequestParam(value = "token") String token) {
        try {
            logger.info("【userInfo】参数:memberId=" + agentId + ",token=" + token);
//            验证token
//            if (!validateTokenService.validataAgentToken(token, agentId)) {
//                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
            return agentService.selectByAgent(agentId);
        } catch (Exception e) {
            logger.error("用户账户接口参数异常=" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 获取文案列表
     *
     * @param agentId
     * @param token
     * @return
     */

    @RequestMapping(value = "/ad_list", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap adList(@RequestParam(value = "agentId", required = true) Integer agentId,
                            @RequestParam(value = "token", required = true) String token) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return new ResultMap("文案列表", advertisementInfoService.selectAdInfoLists());
    }

    /**
     * 添加文案次数
     *
     * @param agentId
     * @param token
     * @param adId
     * @return
     */
    @RequestMapping(value = "/ad_add", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap adList(@RequestParam(value = "agentId", required = true) Integer agentId,
                            @RequestParam(value = "token", required = true) String token,
                            @RequestParam(value = "adId", required = true) Integer adId) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        advertisementInfoService.updateByDownCount(adId);
        return new ResultMap("操作成功");
    }

    /**
     * 获取系统通知
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/sysNotify", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap sysNotifyLists(@RequestParam(value = "agentId", required = true) Integer agentId,
                                    @RequestParam(value = "token", required = true) String token) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return new ResultMap("公告列表", sysNotifyService.selectBySysNotifyLists());
    }

    /**
     * 代理获取验证码
     *
     * @param phone
     * @return
     */

    @RequestMapping(value = "/getSmsCodeByAgent", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentChangePwd(@RequestParam String phone) {

        List<Agent> agentList = agentService.selectByPhoneLists(phone);
        if (ObjectUtils.isEmpty(agentList)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_PHONE_NOT_EXIT);
        }
        return agentService.sendPhoneCode(phone, "忘记密码", agentList.get(0));
    }


    /**
     * 忘记密码-预先
     *
     * @param changePwdForm
     * @return
     */
    @RequestMapping(value = "/agentForgetPwd", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentChangePwd(@Valid AgentChangeForgetForm changePwdForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【忘记密码】参数不正确, changePwdForm={}", changePwdForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }

//        验证code
        String trueCode = redisUtil.getString(RedisKeyGenerator.getCodeAentKey(changePwdForm.getPhone()));
        if (StringUtils.isEmpty(trueCode)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.SMSCODE_IS_OVER);
        }
        if (!changePwdForm.getSmsCode().equals(trueCode)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.SMSCODE_IS_FALSE);
        }
        logger.info("【agentChangePwd】忘记密码 参数AgentChangePwdForm={}", changePwdForm);
        List<Agent> agentList = agentService.selectByPhoneLists(changePwdForm.getPhone());
        if (ObjectUtils.isEmpty(agentList)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_NOT_EXIT);
        }
        //判断密码是否一致
        if (!changePwdForm.getPassword().equals(changePwdForm.getConfirmPassword())) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_PASSWORD_ERROR);
        }
        if (agentList.size() == 1) {
            agentList.get(0).setPassword(MD5Utils.md5(changePwdForm.getPassword(), agentList.get(0).getSalt()));
            int i = agentService.updateByPrimaryKeySelective(agentList.get(0));
            if (i == 1) {
                logger.info("【代理修改密码成功】agent={}", agentList.get(0));
                return new ResultMap("修改成功");
            }
        }
        List<AgentPojo> agentPojoList = new ArrayList<>();
        for (int i = 0; i < agentList.size(); i++) {
            AgentPojo agentPojo = new AgentPojo();
            BeanUtils.copyProperties(agentList.get(i), agentPojo);
            agentPojoList.add(agentPojo);
        }
        return new ResultMap("操作成功", agentPojoList);
    }

    /**
     * 忘记密码-确认
     *
     * @return //
     */
    @RequestMapping(value = "/agentForgetPwdConfirm", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentChangePwdConfirm(@Valid AgentChangeForgetConfirmForm changePwdForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
        Agent agent = agentService.selectByPrimaryKey(changePwdForm.getAgentId());
        if (StringUtils.isEmpty(agent)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_LOGIN_FAILE);
        }
        //判断密码是否一致
        if (!changePwdForm.getPassword().equals(changePwdForm.getConfirmPassword())) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_PASSWORD_ERROR);
        }
        agent.setPassword(MD5Utils.md5(changePwdForm.getPassword(), agent.getSalt()));
        int i = agentService.updateByPrimaryKeySelective(agent);
        if (i == 1) {
            logger.info("【代理修改密码成功】agent={}", agent);
            return new ResultMap("修改成功");
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
    }


    /**
     * 修改密码
     *
     * @param changePwdForm
     * @return
     */
    @RequestMapping(value = "/agentChangePassword", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap agentChangePassword(@Valid AgentChangePasswordForm changePwdForm, BindingResult
            bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("{【修改密码】参数不正确, changePwdForm={}", changePwdForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }
//        验证token
        if (!validateTokenService.validataAgentToken(changePwdForm.getToken(), changePwdForm.getAgentId())) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        logger.info("【agentChangePassword】修改密码 AgentChangePasswordForm={}", changePwdForm);

        //验证密码
        if (!changePwdForm.getNewPassword().equals(changePwdForm.getConfirmPassword())) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.AGENT_PASSWORD_ERROR);
        }
        Agent agent = agentService.selectByPrimaryKey(changePwdForm.getAgentId());
        //验证密码
        if (!MD5Utils.md5(changePwdForm.getOldPassword(), agent.getSalt()).equals(agent.getPassword())) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, "密码错误");
        }
        agent.setPassword(MD5Utils.md5(changePwdForm.getNewPassword(), agent.getSalt()));

        int i = agentService.updateByPrimaryKeySelective(agent);
        if (i == 1) {
            logger.info("【代理修改密码成功】agent={}", agent);
            return new ResultMap("修改成功");
        }
        return new ResultMap(Enviroment.ERROR_CODE, Enviroment.AGENT_PHONE_ERROR);
    }


    /**
     * 获取代理商邀请人数
     *
     * @param agentId
     * @param token
     * @return
     */

    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAgentInvites(@RequestParam(value = "agentId") Integer agentId,
                                     @RequestParam(value = "token") String token) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return agentService.getInviteCount(agentId);
    }

    /**
     * 获取代理商邀请列表
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/inviteLists", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getAgentInviteLists(@RequestParam(value = "agentId") Integer agentId,
                                         @RequestParam(value = "token") String token) {
//        验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return agentService.getInviteLists(agentId);
    }
}
