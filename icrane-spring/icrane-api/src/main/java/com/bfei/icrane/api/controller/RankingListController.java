package com.bfei.icrane.api.controller;

import com.bfei.icrane.api.service.DollOrderService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.service.ValidateTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by SUN on 2018/1/15.
 */
@Controller
@RequestMapping(value = "/rankingList")
@CrossOrigin
public class RankingListController {
    private static final Logger logger = LoggerFactory.getLogger(RankingListController.class);

    @Autowired
    private ValidateTokenService validateTokenService;

    @Autowired
    private DollOrderService dollOrderService;


    /**
     * 获取抓中排行榜
     *
     * @param memberId
     * @param token
     * @param type     类别
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/catchSuccess", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap catchSuccess(@RequestParam Integer memberId, @RequestParam String token, @RequestParam Integer type) throws Exception {
        //验证token有效性
        if (!validateTokenService.validataToken(token, memberId)) {
            logger.info("绑定手机接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return dollOrderService.getCatchSuccessRanks(type,memberId);
    }

    @RequestMapping(value = "/catchSuccess/member", method = RequestMethod.POST)
    @ResponseBody
    public ResultMap catchSuccessByMember(@RequestParam Integer memberId, @RequestParam String token,@RequestParam Integer userId) throws Exception {
        //验证token有效性
        if (!validateTokenService.validataToken(token, memberId)) {
            logger.info("绑定手机接口参数异常=" + Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
            return new ResultMap(Enviroment.RETURN_FAILE_CODE, Enviroment.RETURN_UNAUTHORIZED_MESSAGE);
        }
        return dollOrderService.getCatchSuccessRanksByMember(userId);
    }

    // 充值
//    @RequestMapping(value = "/get", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap payRankingList(Integer type, Integer periodId) throws Exception {
//        logger.info("排行榜插查询接口参数：" + "type=" + type + "," + "periodId=" + periodId);
//        try {
//            //获得redis工具
//            RedisUtil redisUtil = new RedisUtil();
//            String sType;
//            if (type == 1) {
//                sType = "邀请榜";
//            } else if (type == 2) {
//                sType = "抓取榜";
//            } else if (type == 3) {
//                sType = "充值榜";
//            } else {
//                return new ResultMap("301", "参数错误");
//            }
//            String rankingListtype = sType + periodId;
//            List<Object> list = new ArrayList<>();
//            String string = redisUtil.getString(rankingListtype + "人数");
//            Integer integer = Integer.valueOf(string);
//            for (int i = 1; i < integer + 1; i++) {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("排名", redisUtil.getString(rankingListtype + i + "排名"));
//                map.put("id", redisUtil.getString(rankingListtype + i + "id"));
//                map.put("成绩", redisUtil.getString(rankingListtype + i + "成绩"));
//                map.put("奖励", redisUtil.getString(rankingListtype + i + "奖励"));
//                map.put("头像", redisUtil.getString(rankingListtype + i + "头像"));
//                map.put("昵称", redisUtil.getString(rankingListtype + i + "昵称"));
//                list.add(map);
//            }
//            logger.info("排行榜插查询结果resultMap=" + "success");
//            return new ResultMap("", list);
//        } catch (Exception e) {
//            logger.error("排行榜插查询出错", e);
//            throw e;
//        }
//    }


//    @RequestMapping(value = "/importXls", method = RequestMethod.POST)
//    @ResponseBody
//    public ResultMap impotXls(MultipartFile file) throws Exception {
//        try {
//            //获取xls文件
//            Workbook workbook = Workbook.getWorkbook(file.getInputStream());
//            //获取工作簿
//            Sheet sheet = workbook.getSheet(0);
//            //获取总行数
//            int rows = sheet.getRows();
//            //获得redis工具
//            RedisUtil redisUtil = new RedisUtil();
//            //榜单种类期数
//            String rankingListtype = sheet.getCell(0, 0).getContents() + sheet.getCell(2, 0).getContents();
//            //标题不读取
//            for (int i = 2; i < rows; i++) {
//                redisUtil.setString(rankingListtype + (i - 1) + "排名", sheet.getCell(0, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + (i - 1) + "id", sheet.getCell(1, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + (i - 1) + "成绩", sheet.getCell(2, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + (i - 1) + "奖励", sheet.getCell(3, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + (i - 1) + "头像", sheet.getCell(4, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + (i - 1) + "昵称", sheet.getCell(5, i).getContents(), expire);
//                redisUtil.setString(rankingListtype + "人数", sheet.getCell(0, i).getContents(), expire);
//            }
//            //关闭资源
//            workbook.close();
//            return new ResultMap("上传成功");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (BiffException e) {
//            e.printStackTrace();
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//        return new ResultMap("400", "上传失败");
//    }

}
