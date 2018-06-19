package com.bfei.icrane.core.service;

import java.util.List;
import java.util.Set;

import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.models.CatchHistory;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollInfo;
import com.bfei.icrane.core.models.DollTopic;
import com.bfei.icrane.core.models.vo.DollAndAddress;
import org.springframework.cache.annotation.Cacheable;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/19
 * Description: 娃娃机管理业务接口.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public interface DollService {

    /**
     * 获取房间主题(旧)
     *
     * @return
     */
    List<DollTopic> getDollTopics();

    /**
     * 获取房间主题
     *
     * @return
     */
    ResultMap getTopic(Integer version);

    /**
     * 获取娃娃机列表
     */
    List<Doll> getDollList(int offset, int limit);

    List<Doll> getDollList(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version);

    List<Doll> getDollListOrderById(Integer dollTopic, boolean worker);

    List<Doll> getDollListPage(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version);

    /**
     * 根据房间id分页查询(去重用)
     */
    //List<Doll> DollPageById(Integer startId, Integer pageSize, Integer dollTopic, Boolean worker);

    List<Doll> getH5DollList(Integer type,boolean worker);

    List<Doll> selectDollHistory(Integer memberId);

    Doll selectByPrimaryKey(Integer id);

    int getTotalCount();

    //获取娃娃机列表PageBean分页
    PageBean<DollAndAddress> dollList(int page, int pageSize, String name, String machineCode, String machineStates);

    //新增娃娃机
    int insertSelective(Doll record);

    /**
     * 删除娃娃机
     */
    int dollDel(Doll doll);

    /**
     * 根据id查询娃娃机
     */
    Doll selectById(Doll doll);

    /**
     * 更新娃娃机
     */
    int updateByPrimaryKeySelective(Doll record);

    int totalCount(String name, String machineCode, String machineStates);

    List<Doll> allDollList();

    Integer updateDeleteStatus(Integer id);

    ResultMap DollList(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version);

    Integer selectTypeById(Integer dollId);

    List<Integer> selectDollId();

    Doll getDollByDollCode(String dollCode);

    Doll spareRoom();

    int updateClean(Doll doll);

}
