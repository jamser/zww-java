package com.bfei.icrane.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by SUN on 2018/1/10.
 * 收藏娃娃控制器
 */
@Controller
@RequestMapping(value = "/collection")
@CrossOrigin
public class CollectionController {
    private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);

//    @Autowired
//    private CollectionService collectionService;
//    @Autowired
//    private ValidateTokenService validateTokenService;
//    @Autowired
//    private DollService dollService;

    /**
     * 收藏娃娃
     *
     * @param memberId
     * @param token
     * @return
     */
//    @RequestMapping(value = "/like", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap like(Integer memberId, String token, String dollCode) {
//        try {
//            logger.info("收藏娃娃接口参数:memberId=" + memberId + ",dollCode=" + dollCode);
//            RedisUtil redisUtil = new RedisUtil();
//            //System.out.println(redisUtil.getSMembers(RedisKeyGenerator.getLikeDollKey(memberId)));
//            //System.out.println();
//            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(dollCode)) {
//                //logger.info("收藏娃娃接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //验证token
//            if (!validateTokenService.validataToken(token, memberId)) {
//                //logger.info("收藏娃娃接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            int result = collectionService.like(memberId, dollCode);
//            if (result == -2) {
//                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.THERE_IS_NO_DOLL);
//            } else if (result == -1) {
//                return new ResultMap(true, Enviroment.RETURN_SUCCESS_MESSAGE, Enviroment.REPEAT_LIKE);
//            } else if (result == 1) {
//                return new ResultMap(true, Enviroment.RETURN_SUCCESS_MESSAGE, Enviroment.LIKE_SUCCESS);
//            } else {
//                return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR + "result=" + result);
//            }
//        } catch (Exception e) {
//            //logger.error("收藏娃娃接口参数异常=" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//        }
//    }

    /**
     * 取消收藏
     *
     * @param memberId
     * @param token
     * @return
     */
//    @RequestMapping(value = "/disLike", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap disLike(Integer memberId, String token, String[] dollCode) {
//        try {
//            //logger.info("取消收藏接口参数:memberId=" + memberId + ",dollCode=" + dollCode);
//            if (memberId == null || StringUtils.isEmpty(token) || StringUtils.isEmpty(dollCode)) {
//                //logger.info("取消收藏接口参数异常=" + Enviroment.RETURN_INVALID_PARA_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            //验证token
//            if (!validateTokenService.validataToken(token, memberId)) {
//                //logger.info("取消收藏接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            int result = collectionService.disLike(memberId, dollCode);
//            if (result == -1) {
//                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.THERE_IS_NO_COLLECTION);
//            } else if (result >= 1) {
//                return new ResultMap(true, Enviroment.RETURN_SUCCESS_MESSAGE, Enviroment.DISLIKE_SUCCESS);
//            } else {
//                return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//            }
//        } catch (Exception e) {
//            //logger.error("取消收藏接口参数异常=" + e.getMessage());
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//        }
//    }

    /**
     * 收藏娃娃列表
     *
     * @param memberId
     * @param token
     * @return
     */
//    @RequestMapping(value = "/likeDollInfo", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap likeList(Integer memberId, String token) {
//        try {
//            if (memberId == null || StringUtils.isEmpty(token)) {
//                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, Enviroment.RETURN_INVALID_PARA_MESSAGE);
//            }
//            if (!validateTokenService.validataToken(token, memberId)) {
//                return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
//            }
//            List<DollInfo> likeList = collectionService.likeList(memberId);
//            if (likeList == null || likeList.size() == 0) {
//                return new ResultMap(Enviroment.FAILE_CODE, Enviroment.NO_COLLECTION);
//            } else {
//                List<Object> list = new ArrayList<>();
//                for (DollInfo dollInfo : likeList) {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("id", dollInfo.getId());
//                    map.put("dollName", dollInfo.getDollName());
//                    map.put("dollTotal", dollInfo.getDollTotal());
//                    map.put("imgUrl", dollInfo.getImgUrl());
//                    map.put("addTime", dollInfo.getAddTime());
//                    String dollCode = dollInfo.getDollCode();
//                    map.put("dollCode", dollCode);
//                    boolean online = dollInfo.isOnline();
//                    map.put("online", online);
//                    if (online) {
//                        Doll dollByDollCode = dollService.getDollByDollCode(dollCode);
//                        map.put("roomType", dollByDollCode.getMachineType());
//                        map.put("roomPrice", dollByDollCode.getPrice());
//                    } else {
//                        map.put("roomType", -1);
//                        map.put("roomPrice", -1);
//                    }
//                    list.add(map);
//                }
//                return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, list);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResultMap(Enviroment.ERROR_CODE, Enviroment.HAVE_ERROR);
//        }
//    }
}