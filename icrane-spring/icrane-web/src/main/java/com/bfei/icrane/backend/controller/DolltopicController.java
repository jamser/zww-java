package com.bfei.icrane.backend.controller;

import com.bfei.icrane.backend.service.DollTopicService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollTopic;
import com.bfei.icrane.core.service.DollService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/dollTopicManage")
public class DolltopicController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private DollTopicService dollTopicService;
    @Autowired
    private DollService dollService;

    @RequestMapping("/list")
    public String list(String topicName, Integer dollid, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "50") Integer pageSize, HttpServletRequest request) throws Exception {
        logger.info("查询房间主题输入参数:dollid={},page={},pageSize={}", dollid, page, pageSize);
        try {
            PageBean<DollTopic> pageBean = dollTopicService.getDollTopicList(page, pageSize, dollid, topicName);
            logger.info("查询房间主题列表结果:{}", pageBean.getList().toString());
            request.setAttribute("pageBean", pageBean);
            request.setAttribute("dollid", dollid);
            request.setAttribute("topicName", topicName);
            return "doll/doll_topic_list";
        } catch (Exception e) {
            logger.error("查询房间主题列表SystemError:" + e);
            throw e;
        }
    }


    @RequestMapping("/dollTopicToAdd")
    public String toAdd(HttpServletRequest request) throws Exception {
        List<Doll> dolls = dollService.allDollList();
        logger.info("查询娃娃机:{}", dolls.toString());
        request.setAttribute("dollList", dolls);
        return "doll/doll_topic_add";
    }

    //添加
    @RequestMapping(value = "/dollTopicAdd", method = RequestMethod.POST)
    @ResponseBody
    public String dollTopicAdd(DollTopic dollTopic) throws Exception {
        logger.info("新增主题传入参数：{}", dollTopic.toString());
        try {
            int result = dollTopicService.insertSelective(dollTopic);
            logger.info("新增主题结果:{}", result > 0 ? "success" : "fail");
            if (result > 0) {
                return "1";
            } else {
                return "0";
            }
        } catch (Exception e) {
            logger.error("新增主题SystemError：" + e);
            throw e;
        }
    }


    /**
     * @Title: dollDel
     * @Description: 删除主题
     */
    @RequestMapping(value = "/dollTopicDel", method = RequestMethod.POST)
    @ResponseBody
    public String dollDel(Integer id) throws Exception {
        logger.info("删除主题id：{}", id);
        try {
            int result = dollTopicService.dollTopicDel(id);
            logger.error("删除主题结果：{}", result > 0 ? "success" : "fail");
            if (result > 0) {
                return "1";
            } else {
                return "0";
            }
        } catch (Exception e) {
            logger.error("删除主题SystemError：" + e);
            throw e;
        }
    }

    //编辑
    @RequestMapping("/toEditDollTopic")
    public String toEditMember(Integer id, HttpServletRequest request) throws Exception {
        logger.info("跳转编辑主题id:{}", id);
        try {
            List<Doll> dolls = dollService.allDollList();
            logger.info("查询娃娃机:{}", dolls.toString());
            DollTopic dollTopic = dollTopicService.getDollTopicById(id);
            logger.info("编辑主题查询用户:{}", dollTopic != null ? dollTopic.toString() : "无此主题");
            request.setAttribute("dollTopic", dollTopic);
            request.setAttribute("dollList", dolls);
            return "doll/doll_topic_edit";
        } catch (Exception e) {
            logger.error("编辑用户SystemError：" + e);
            throw e;
        }
    }

    /**
     * 主题更新
     *
     * @param dollTopic
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/dollTopicUpdate", method = RequestMethod.POST)
    @ResponseBody
    public String dollTopicUpdate(DollTopic dollTopic, HttpServletRequest request) throws Exception {
        logger.info("更新主题单传入参数member：{}", dollTopic != null ? dollTopic.toString() : null);
        try {
            int result = dollTopicService.updateByPrimaryKeySelective(dollTopic, request);
            logger.info("更新主题结果：{}", result > 0 ? "success" : "fail");
            if (result > 0) {
                return "1";
            } else {
                return "0";
            }
        } catch (Exception e) {
            logger.error("更新主题SystemError：" + e);
            throw e;
        }
    }


}

