package com.bfei.icrane.api.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.bfei.icrane.common.util.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.OemService;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.bfei.icrane.api.service.MemberService;
import com.bfei.icrane.api.service.SystemPrefService;
import com.bfei.icrane.core.pojos.MemberInfoPojo;
import com.bfei.icrane.core.pojos.RoomStatusPojo;
import com.bfei.icrane.core.service.DollService;
import com.bfei.icrane.core.service.ValidateTokenService;

/**
 * Author: lcc Version: 1.1 Date: 2018/03/26 Description: 娃娃机管理控制层. Copyright
 * (c) 2018 365zww网络. All rights reserved.
 */
@Controller
@RequestMapping(value = "/doll")
@CrossOrigin
public class DollController {

    private static final Logger logger = LoggerFactory.getLogger(DollController.class);

    @Autowired
    ValidateTokenService validateTokenService;
    @Autowired
    private DollService dollService;
    @Autowired
    private MemberService memberService;
    @Autowired
    SystemPrefService systemPrefService;
    @Autowired
    private OemService oemService;

    //level的失效时间
    public static Long expiresTime = 0L;

    /**
     * 主题列表(旧)(免验证)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getIosDollTopicList", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getIosDollTopicList(HttpServletRequest request) throws Exception {
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性

            List<DollTopic> dollTopic = new ArrayList<DollTopic>();
            DollTopic all = new DollTopic();
            all.setId(0);
            all.setDollId(0);
            all.setDollName("全部");
            all.setRoomType(-1);
            all.setTopicType(0);
            all.setTopicName("全部");
            dollTopic.add(all);
            List<DollTopic> dollTopicList = dollService.getDollTopics();
            if (dollTopicList != null && dollTopicList.size() > 0) {
                dollTopic.addAll(dollTopicList);
            }
            resultMap.put("resultData", dollTopic);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);

            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机列表出错", e);
            //throw e;
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", "查询房间主题列表信息失败");
            return resultMap;
        }
    }

    /**
     * 房间详情(根据dollCode查询)
     *
     * @param memberId 用户userId
     * @param token    登录令牌
     * @param dollCode 娃娃code
     * @return 房间详情
     * @throws Exception
     */
    @RequestMapping(value = "/getDollByDollCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getDollByDollCode(Integer memberId, String token, String dollCode) throws Exception {
        try {
            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(dollCode)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Doll doll = dollService.getDollByDollCode(dollCode);
            if (doll == null) {
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.ON_ONLINE);
            } else {
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, doll);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 随机房间(优先空闲练习房,其次游戏中练习房)
     *
     * @param memberId 用户userId
     * @param token    登录令牌
     * @return 房间详情
     * @throws Exception
     */
    @RequestMapping(value = "/spareRoom", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap spareRoom(Integer memberId, String token) {
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Doll doll = dollService.spareRoom();
            if (doll == null) {
                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.NO_LIANXIFANG);
            } else {
                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, doll);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 主题列表(旧)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getDollTopicList", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollTopicList(HttpServletRequest request) throws Exception {
        Map<String, Object> resultMap = new HashedMap<>();
        try {
            // 验证token有效性
            /*if (request.getParameter("token") == null || "".equals(request.getParameter("token"))
                    || !validateTokenService.validataToken(request.getParameter("token"))) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }*/
            List<DollTopic> dollTopic = new ArrayList<DollTopic>();
            DollTopic all = new DollTopic();
            all.setId(0);
            all.setDollId(0);
            all.setDollName("全部");
            all.setRoomType(-1);
            all.setTopicType(0);
            all.setTopicName("全部");
            dollTopic.add(all);
            List<DollTopic> dollTopicList = dollService.getDollTopics();
            if (dollTopicList != null && dollTopicList.size() > 0) {
                dollTopic.addAll(dollTopicList);
            }
            resultMap.put("resultData", dollTopic);
            resultMap.put("success", Enviroment.RETURN_SUCCESS);
            resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
            resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);

            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机列表出错", e);
            //throw e;
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", "查询房间主题列表信息失败");
            return resultMap;
        }
    }

    /**
     * 主题列表
     *
     * @param token 登录令牌
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/topic", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap topic(String token, String head, Integer version) throws Exception {
        try {
            logger.info("主题列表接口参数:token=" + token + "version=" + version);
            if (!"ios".equals(head)) {
                //验证参数
                if (StringUtils.isEmpty(token)) {
                    logger.info("主题列表接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                    return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
                }
                //验证token
                if (!validateTokenService.validataToken(token)) {
                    logger.info("主题列表接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                    return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                }
            }
            return dollService.getTopic(version);
        } catch (Exception e) {
            logger.error("主题列表出错" + e.getMessage());
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    /**
     * 获取娃娃机列表
     *
     * @param token
     * @param offset
     * @param limit
     * @param dollTopic
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getDollList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getDollList(String token, Integer offset, Integer limit, Integer dollTopic, Integer memberId, Integer version) throws Exception {
        try {
            logger.info("获取娃娃机列表接口参数：offset=" + offset + ",limit=" + limit + ",token=" + token + ",dollTopic=" + dollTopic + ",memberId=" + memberId);
            //校验参数
            if (StringUtils.isEmpty(token)) {
                logger.info("获取娃娃机列表接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token)) {
                logger.info("获取娃娃机列表接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            return dollService.DollList(offset, limit, dollTopic, memberService.isWorker(memberId), version);
        } catch (Exception e) {
            logger.info("获取娃娃机列表接口异常=" + e.getMessage());
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.PAY_ERROR);
        }
    }

    @RequestMapping(value = "/getIosDollList", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getIosDollList(HttpServletRequest request, Integer memberId, Integer version) throws Exception {
        Map<String, Object> resultMap = new HashedMap<String, Object>();

        int offset = 0;
        int limit = 0;
        int dollTopic = 0;// 主题参数
        try {
            // 验证token有效性
            /*if (request.getParameter("token") == null || "".equals(request.getParameter("token"))
                    || !validateTokenService.validataToken(request.getParameter("token"))) {
				resultMap.put("success", Enviroment.RETURN_FAILE);
				resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
				resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

				return resultMap;
			}*/

            //if (!"".equals(request.getParameter("offset")) && request.getParameter("offset") != null) {
            if (StringUtils.isNotEmpty(request.getParameter("offset"))) {
                offset = Integer.parseInt(request.getParameter("offset"));
            } else {
                offset = 0;
            }
            //if (!"".equals(request.getParameter("limit")) && request.getParameter("limit") != null) {
            if (StringUtils.isNotEmpty(request.getParameter("limit"))) {
                limit = Integer.parseInt(request.getParameter("limit"));
            } else {
                limit = Enviroment.MAX_TABLE_LIMIT;
            }
            String sdollTopic = request.getParameter("dollTopic");
            //System.out.println(sdollTopic);
            if (StringUtils.isNotEmpty(sdollTopic) && !"-5764607523034234877".equals(sdollTopic)) {
                dollTopic = Integer.parseInt(sdollTopic);
            }
            logger.info("获取娃娃机列表接口参数：offset=" + offset + ",limit=" + limit +
                    //",token=" + request.getParameter("token") +
                    ",dollTopic=" + dollTopic + ",memberId=" + memberId);
            String head = request.getParameter("head");
            String head1 = request.getHeader("head");
            logger.info("getIosDollList接口请求head=" + head + "," + head1);
            List<Doll> dollList = dollService.getDollList(offset, limit, dollTopic, memberService.isWorker(memberId), version);
            if (dollList != null) {
                //IOS功能开关
                SystemPref syspref = systemPrefService.selectByPrimaryKey("IOS_STATE");
                if ("0".equals(syspref.getValue()) && head1 == null) {
                    List<Doll> dollListNew = new ArrayList<Doll>();
                    Doll one = dollList.get(0);
                    one.setMachineStatus("维修中");
                    dollListNew.add(one);
                    resultMap.put("resultData", dollListNew);
                    resultMap.put("success", Enviroment.RETURN_SUCCESS);
                    resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                    resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                } else {
                    resultMap.put("resultData", dollList);
                    resultMap.put("success", Enviroment.RETURN_SUCCESS);
                    resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                    resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
                }

            } else if (dollList == null) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("获取娃娃机列表resultMap=" + Enviroment.RETURN_SUCCESS_MESSAGE);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机列表出错", e);
            //throw e;
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", "获取娃娃机列表信息失败");
            return resultMap;
        }

    }

    /**
     * 房间列表(H5用)
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getH5DollList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getH5DollList(String token, Integer memberId) throws Exception {
        try {
            //logger.info("获取娃娃机列表接口参数：token=" + token + ",memberId=" + memberId);
            //验证参数
            /*if (StringUtils.isEmpty(token) || memberId == null) {
                //logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                //logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }*/
            List<Doll> dollList = dollService.getH5DollList(memberService.isWorker(memberId));
            //logger.info("获取娃娃机列表resultMap=" + Enviroment.RETURN_SUCCESS_MESSAGE);
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, dollList);
        } catch (Exception e) {
            //logger.error("获取H5娃娃机列表出错", e);
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }

    // 获取娃娃机列表分页
    @RequestMapping(value = "/getDollListTotalPage", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollListTotalPage(String token, Integer pageSize) throws Exception {

        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }

            if (pageSize == null) {
                pageSize = 10;
            }

            int totalCount = dollService.getTotalCount();
            logger.info("totalCount=" + totalCount);
            int totalPage = (totalCount - 1) / pageSize + 1;
            logger.info("totalPage=" + totalPage);
            if (totalCount > 0) {
                resultMap.put("totalPages", totalPage);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_NODATA_MESSAGE);
            }
            //logger.info("获取娃娃机列表分页resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机列表分页出错", e);
            throw e;
        }

    }

    // 获取娃娃机列表分页
    @RequestMapping(value = "/getDollListPage", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollListPage(HttpServletRequest request, String token, Integer memberId, Integer version) throws Exception {

        Map<String, Object> resultMap = new HashedMap<>();

        int pageNo = 0;
        int pageSize = 0;
        int dollTopic = 0;

        try {
            //验证token有效性
            /*if (StringUtils.isEmpty(token) || !validateTokenService.validataToken(token)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return resultMap;
            }*/
            if (StringUtils.isNotEmpty(request.getParameter("pageNo"))) {
                pageNo = Integer.parseInt(request.getParameter("pageNo"));
            } else {
                pageNo = 1;
            }
            //if (!"".equals(request.getParameter("pageSize")) && request.getParameter("pageSize") != null) {
            if (StringUtils.isNotEmpty(request.getParameter("pageSize"))) {
                pageSize = Integer.parseInt(request.getParameter("pageSize"));
            } else {
                pageSize = 10;
            }
            if (StringUtils.isNotEmpty(request.getParameter("dollTopic"))) {
                dollTopic = Integer.parseInt(request.getParameter("dollTopic"));
            }
            String head = request.getParameter("head");
            String head1 = request.getHeader("head");
            logger.info("getDollListPage********请求head=" + head + "," + head1);
            int offset = pageSize * (pageNo - 1);
            if (offset < 0) {
                offset = 0;
            }
            logger.info("获取娃娃机列表分页传入参数offset=" + offset + ",pageSize=" + pageSize + ",dollTopic=" + dollTopic + ",memberId=" + memberId);
            //加载主题列表房间
            List<Doll> dollList = dollService.getDollListPage(offset, pageSize, dollTopic, memberService.isWorker(memberId), version);
            if (dollList != null) {
                resultMap.put("resultData", dollList);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            //logger.info("获取娃娃机列表分页resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机列表分页出错", e);
            //throw e;
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", "获取娃娃机列表信息失败");
            return resultMap;
        }

    }


    /*
     * 获取娃娃机列表(Id分页查询)
     *
     * @param memberId 用户UserId
     * @param token    登陆令牌
     * @param startId  起始房间Id
     * @return
     * @throws Exception
     */
   /* @RequestMapping(value = "/DollPage", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap DollPage(Integer memberId, String token, Integer startId, Integer pageSize, Integer dollTopic) throws Exception {
        try {
            if (memberId == null || StringUtils.isEmpty(token)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            if (!validateTokenService.validataToken(token, memberId)) {
                logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            List<Doll> dollList = dollService.DollPageById(startId, pageSize, dollTopic, memberService.isWorker(memberId));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/
    @RequestMapping(value = "/getDollHistory", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollHistory(Integer memberId, String token) throws Exception {
        logger.info("获取抓取记录接口传入参数memberId=" + memberId + ",token=" + token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();
        try {
            // 验证token有效性
            if (token == null || "".equals(token) || !validateTokenService.validataToken(token, memberId)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }
            List<Doll> dollHisTory = dollService.selectDollHistory(memberId);
            if (dollHisTory != null) {
                List<Map<String, Object>> dh = new ArrayList<>();
                for (Doll doll : dollHisTory) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("historyId", doll.getHistoryId());
                    map.put("name", doll.getName());
                    map.put("catchDate", doll.getCatchDate());
                    map.put("catchStatus", doll.getCatchStatus());
                    map.put("tbimgContextPath", doll.getTbimgContextPath());
                    map.put("tbimgFileName", doll.getTbimgFileName());
                    map.put("tbimgRealPath", doll.getTbimgRealPath());
                    map.put("videoUrl", doll.getVideoUrl());
                    map.put("gameNum", doll.getGameNum());
                    map.put("checkState", doll.checkStateString());
                    dh.add(map);
                }
                resultMap.put("resultData", dh);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            } else if (dollHisTory == null) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", Enviroment.RETURN_FAILE_MESSAGE);
            }
            logger.info("获取抓取记录成功");
            return resultMap;
        } catch (Exception e) {
            logger.error("获取我的娃娃出错", e);
            throw e;
        }
    }

    //从缓存中获取娃娃机状态和人数
    @RequestMapping(value = "/getDollStatus", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getDollStatus(@RequestParam("dollIds") Integer[] dollIds, String token, HttpServletRequest request) throws Exception {
        logger.debug("从缓存中获取娃娃机状态和人数：{},token:{}", dollIds, token);
        Map<String, Object> resultMap = new HashedMap<String, Object>();

        try {
            //验证token有效性
            /*if (token == null || "".equals(token) || !validateTokenService.validataToken(token)) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_UNAUTHORIZED_CODE);
                resultMap.put("message", Enviroment.RETURN_UNAUTHORIZED_MESSAGE);

                return resultMap;
            }*/

            logger.debug("获取娃娃机状态和在线人数接口参数是：{}", dollIds.toString());
            if (dollIds == null || dollIds.length == 0) {
                resultMap.put("success", Enviroment.RETURN_FAILE);
                resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
                resultMap.put("message", "传入娃娃机id参数不对");
            } else {
                RedisUtil redisUtil = new RedisUtil();
                List<RoomStatusPojo> resultData = new ArrayList<>();

                int dollId;
                int hostId;
                String hostInfoKey;
                Doll doll;
                String head = request.getHeader("head");
                //logger.info("getDollList********请求head="+head);
                SystemPref syspref = systemPrefService.selectByPrimaryKey("IOS_STATE");
                for (int i = 0; i < dollIds.length; i++) {
                    dollId = dollIds[i];
                    RoomStatusPojo roomStatus = new RoomStatusPojo();
                    roomStatus.setRoomId(dollId);

                    if (redisUtil.existsKey(RedisKeyGenerator.getRoomStatusKey(dollId))) {
                        roomStatus.setStatus(redisUtil.getString(RedisKeyGenerator.getRoomStatusKey(dollId)));
                    } else {
                        doll = dollService.selectByPrimaryKey(dollId);
                        redisUtil.setString(RedisKeyGenerator.getRoomStatusKey(dollId), doll.getMachineStatus());
                        roomStatus.setStatus(doll.getMachineStatus());//从数据库的表中取状态
                    }
                    if (redisUtil.existsKey(RedisKeyGenerator.getRoomKey(dollId))) {
                        roomStatus.setActiveMembers(redisUtil.getSCard(RedisKeyGenerator.getRoomKey(dollId)));
                    } else {
                        roomStatus.setActiveMembers(0);//默认0
                    }
                    if (redisUtil.existsKey(RedisKeyGenerator.getRoomHostKey(dollId))) {
                        hostId = Integer.parseInt(redisUtil.getString(RedisKeyGenerator.getRoomHostKey(dollId)));
                        hostInfoKey = RedisKeyGenerator.getMemberInfoKey(hostId);
                        MemberInfoPojo hostInfo = new MemberInfoPojo();
                        if (redisUtil.existsKey(hostInfoKey)) {
                            hostInfo.setId(hostId);
                            hostInfo.setName(redisUtil.getHashSet(hostInfoKey, "name"));
                            hostInfo.setGender(redisUtil.getHashSet(hostInfoKey, "gender"));
                            hostInfo.setIconRealPath(redisUtil.getHashSet(hostInfoKey, "iconRealPath"));
                            hostInfo.setLevel(memberService.selectLevelById(hostId));
                            roomStatus.setHostInfo(hostInfo);
                        } else {
                            Member host = memberService.selectById(hostId);
                            if (host != null) {
                                hostInfo.setId(hostId);
                                if (host.getName() != null) {
                                    hostInfo.setName(host.getName());
                                }
                                if (host.getGender() != null) {
                                    hostInfo.setGender(host.getGender());
                                }
                                if (host.getIconRealPath() != null) {
                                    hostInfo.setIconRealPath(host.getIconRealPath());
                                }
                                if (host.getAccount() != null) {
                                    if (host.getAccount().getVip() != null) {
                                        hostInfo.setLevel(String.valueOf(host.getAccount().getVip().getLevel()));
                                    }
                                }
                                roomStatus.setHostInfo(hostInfo);

                                redisUtil.addHashSet(hostInfoKey, "name", hostInfo.getName());
                                redisUtil.addHashSet(hostInfoKey, "gender", hostInfo.getGender());
                                redisUtil.addHashSet(hostInfoKey, "iconRealPath", hostInfo.getIconRealPath());
                                redisUtil.addHashSet(hostInfoKey, "level", hostInfo.getLevel());
                            }
                        }
                    }
                    if ("0".equals(syspref.getValue()) && head == null) {//IOS状态开关
                        roomStatus.setStatus("维修中");
                    }
                    resultData.add(roomStatus);
                    if (StringUtils.isEmpty(resultData.get(0).getHostInfo()) && !StringUtils.isEmpty(dollId)) {
                        redisUtil.delKey(RedisKeyGenerator.getRoomStatusKey(dollId));
                        Doll newDoll = new Doll();
                        newDoll.setId(dollId);
                        newDoll.setMachineStatus("空闲中");
                        dollService.updateClean(newDoll);
                    }
                }

                resultMap.put("resultData", resultData);
                resultMap.put("success", Enviroment.RETURN_SUCCESS);
                resultMap.put("statusCode", Enviroment.RETURN_SUCCESS_CODE);
                resultMap.put("message", Enviroment.RETURN_SUCCESS_MESSAGE);
            }

            logger.debug("获取娃娃机状态和在线人数resultMap=" + resultMap);
            return resultMap;
        } catch (Exception e) {
            logger.error("获取娃娃机状态和在线人数出错", e);
            //throw e;
            resultMap.put("success", Enviroment.RETURN_FAILE);
            resultMap.put("statusCode", Enviroment.RETURN_FAILE_CODE);
            resultMap.put("message", "获取娃娃机状态和在线人数信息失败");
            return resultMap;
        }

    }

    /**
     * 获取bannner(H5用)
     *
     * @param memberId
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getH5BannerList", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap getH5BannerList(String token, Integer memberId) throws Exception {
        try {
            //logger.info("获取娃娃机列表接口参数：token=" + token + ",memberId=" + memberId);
            //验证参数
            if (StringUtils.isEmpty(token) || memberId == null) {
                //logger.info("用户账户接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
            }
            //验证token
            if (!validateTokenService.validataToken(token, memberId)) {
                //logger.info("用户账户接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            }
            Member member = memberService.selectById(memberId);
            Oem oem = oemService.selectByCode(member.getLoginChannel());
            if (oem == null) {
                oem = oemService.selectByCode("lanaokj");
            }
            List<OemBanner> list = oemService.selectByOemId(oem.getId());
            //logger.info("获取娃娃机列表resultMap=" + Enviroment.RETURN_SUCCESS_MESSAGE);
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, list);
        } catch (Exception e) {
            //logger.error("获取H5娃娃机列表出错", e);
            e.printStackTrace();
            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
        }
    }
}
