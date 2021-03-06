package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.AgentService;
import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.form.BankInfoForm;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.BankInfo;
import com.bfei.icrane.core.service.ValidateTokenService;
import com.bfei.icrane.core.service.impl.AliyunServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代理商银行卡管理
 */
@Controller
@RequestMapping(value = "/agent/bank")
@CrossOrigin
public class AgentBankController {

    private static final Logger logger = LoggerFactory.getLogger(AgentBankController.class);

    @Autowired
    private ValidateTokenService validateTokenService;
    @Autowired
    private AgentService agentService;

    RedisUtil redisUtil = new RedisUtil();

    // 银行卡上传
    @ResponseBody
    @RequestMapping(value = "/uploadBankImg", method = RequestMethod.POST)
    public Map<String, Object> uploadBankImg(@RequestParam("file") MultipartFile file,
                                             @RequestParam("agentId") String agentIdStr, @RequestParam("token") String token, HttpSession session) throws Exception {
        int agentId = Integer.valueOf(agentIdStr);
        logger.info("银行卡上传接口参数agentId" + agentId + "token=" + token);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            PropFileManager propFileMgr = new PropFileManager("interface.properties");
            String ossBucketName = propFileMgr.getProperty("aliyun.ossBucketName");
            if (!file.isEmpty()) {
                //验证token有效性
                if (token == null || "".equals(token) || !validateTokenService.validataAgentToken(token)) {
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
                String NewFileKey = "agent/agentbank/" + agentIdStr + "/" + fileKey + "." + suffix;

                byte[] bytes = file.getBytes();
                InputStream fileInputStream = new ByteArrayInputStream(bytes);

                // 上传到阿里云OSS
                logger.info("上传代理agent " + agentIdStr + "的银行卡到阿里云OSS BucketName(" + ossBucketName + ") "
                        + "文件名(" + NewFileKey + ")");

                if (!AliyunServiceImpl.getInstance().putFileStreamToOSS(ossBucketName, NewFileKey, fileInputStream)) {
                    resultMap.put("success", Enviroment.RETURN_FAILE);
                    resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                    resultMap.put("message", "文件上传到阿里云OSS时失败！");
                    return resultMap;
                }
                String newFileUrl = AliyunServiceImpl.getInstance().generatePresignedUrl(ossBucketName, NewFileKey, 1000000).toString();
                //缓存中设置新头像地址
                //  redisUtil.addHashSet(RedisKeyGenerator.getMemberInfoKey(agentId), "iconRealPath", newFileUrl);

                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", "文件上传成功！");
                resultMap.put("fileUrl", newFileUrl);
                logger.info("银行卡上传resultMap" + resultMap);

            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE1);
                resultMap.put("message", "文件大小为空！");
                return resultMap;
            }
        } catch (Exception e) {
            logger.error("银行卡上传出错", e);
            throw e;
        }

        return resultMap;
    }

    /**
     * 获取银行卡列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> list(@RequestParam("agentId") String agentIdStr, @RequestParam("token") String token) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //验证token有效性
        if (token == null || "".equals(token) || !validateTokenService.validataAgentToken(token)) {
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
            resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return resultMap;
        }

        List<BankInfo> list = agentService.getBankInfoList(Integer.valueOf(agentIdStr));
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setCardNo(HideDataUtil.hideCardNo(list.get(i).getCardNo()));
        }
        resultMap.put("success", Enviroment.RETURN_SUCCESS);
        resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
        resultMap.put("message", "获取银行卡列表成功！");
        resultMap.put("resultData", list);
        return resultMap;
    }


    /**
     * 添加银行卡
     */
    @RequestMapping(value = "/addbank", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap addbank(@Valid BankInfoForm bankInfoForm, BindingResult bindingResult, @RequestParam("token") String token) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        if (bindingResult.hasErrors()) {
            logger.error("{【添加银行卡】参数不正确, bankInfoForm={}", bankInfoForm);
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, bindingResult.getFieldError().getDefaultMessage());
        }

        //验证token有效性
        if (token == null || "".equals(token) || !validateTokenService.validataAgentToken(token)) {
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, "token失效");
        }

        String trueCode = redisUtil.getString(RedisKeyGenerator.getCodeAentKey(bankInfoForm.getPhone()));
        if (StringUtils.isEmpty(trueCode)) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "验证码存在");
        }
        if (!bankInfoForm.getSmsCode().equals(trueCode)) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.SMSCODE_IS_FALSE);
        }

        if (bankInfoForm.getId() != null) {
            BankInfo bankInfo = agentService.selectByBankId(bankInfoForm.getId());
            if (null != bankInfo && bankInfo.getStatus() == 2) {
                BeanUtils.copyProperties(bankInfoForm, bankInfo);
                bankInfo.setUpdateTime(new Date());
                bankInfo.setStatus(0);
                agentService.updateBankInfo(bankInfo);
                return new ResultMap("添加银行卡成功！");
            }
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "添加银行卡失败！写入失败");
        }

        BankInfo byCardNo = agentService.selectByCardNo(bankInfoForm.getCardNo());
        if (null != byCardNo) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "添加银行卡失败！该卡号已存在");
        }
        BankInfo bankInfo = new BankInfo();
        BeanUtils.copyProperties(bankInfoForm, bankInfo);
        bankInfo.setCreateTime(new Date());
        bankInfo.setUpdateTime(new Date());
        bankInfo.setStatus(1);
        int i = agentService.insertBankInfo(bankInfo);
        if (i == 0) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "添加银行卡失败！写入失败");
        } else {
            return new ResultMap("添加银行卡成功！");
        }
    }


    /**
     * 获取代理银行验证码
     *
     * @param agentId
     * @param token
     * @return
     */
    @RequestMapping(value = "/getBankSmsCodeByAgent", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getBankSmsCodeByAgent(@RequestParam(value = "agentId") Integer agentId,
                                           @RequestParam(value = "token") String token,
                                           @RequestParam String phone) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        Agent agent = agentService.selectByPrimaryKey(agentId);
        return agentService.sendPhoneCode(phone, "添加银行卡", agent);
    }

    /**
     * 获取银行卡详细信息
     *
     * @param agentId
     * @param token
     * @param bankId
     * @return
     */
    @RequestMapping(value = "/getBankInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getBankInfo(@RequestParam(value = "agentId") Integer agentId,
                                 @RequestParam(value = "token") String token,
                                 @RequestParam Integer bankId) {
        //验证token
        if (!validateTokenService.validataAgentToken(token, agentId)) {
            logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return new ResultMap("获取银行卡信息", agentService.selectByBankId(bankId));
    }
}
