package com.bfei.icrane.core.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.bfei.icrane.core.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.core.dao.DollDao;
import com.bfei.icrane.core.dao.DollTopicDao;
import com.bfei.icrane.core.models.vo.DollAndAddress;
import com.bfei.icrane.core.service.DollService;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/19
 * Description: 娃娃机管理业务接口实现类.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
@Service("DollService")
public class DollServiceImpl implements DollService {
    private static final Logger logger = LoggerFactory.getLogger(DollServiceImpl.class);
    @Autowired
    private DollDao dollDao;
    @Autowired
    private DollTopicDao dollTopicDao;

    @Override
    public List<DollTopic> getDollTopics() {
        return dollDao.getDollTopics();
    }

    /**
     * 获取主题列表
     *
     * @return
     */
    @Override
    public ResultMap getTopic(Integer version) {
        List<TopicInfo> topicInfolist = dollDao.getTopicInfo(version);
        if (topicInfolist != null && topicInfolist.size() > 0) {
            logger.info("主题列表查询成功");
            return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, topicInfolist);
        }
        return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE, Enviroment.RETURN_FAILE_MESSAGE);
    }

    public List<Doll> getDollList(int offset, int limit) {
        return dollDao.getDollList(offset, limit);
    }

    public List<Doll> getH5DollList(int offset, int limit) {
        return dollDao.getH5DollList(offset, limit);
    }

    /**
     * 加载主题列表
     */
    @Override
    @Cacheable(value = "dollList", key = "#version+''+#worker+#dollTopic")
    public List<Doll> getDollList(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version) {
        List<Doll> dollTopicList = dollDao.getDollTopicList(offset, limit, dollTopic);
        if ("2".equals(version)) {
            for (Iterator iter = dollTopicList.iterator(); iter.hasNext(); ) {
                Doll doll = (Doll) iter.next();
                if (doll.getMachineType() == 1 || doll.getMachineType() == 3) {
                    iter.remove();
                }
            }
        }
        if (worker) {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, doll.getName().indexOf("-")) + "-" + doll.getMachineCode());
                } else {
                    doll.setName(doll.getName() + "-" + doll.getMachineCode());
                }
            }
        } else {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, endIndex));
                }
            }
        }
        return dollTopicList;
    }

    /**
     * 加载主题列表
     */
    @Override
    @Cacheable(value = "dollListOrderById", key = "#worker+''+#dollTopic")
    public List<Doll> getDollListOrderById(Integer dollTopic, boolean worker) {
        List<Doll> dollTopicList = dollDao.getDollTopicListOrderById(dollTopic);
        if (worker) {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, doll.getName().indexOf("-")) + "-" + doll.getMachineCode());
                } else {
                    doll.setName(doll.getName() + "-" + doll.getMachineCode());
                }
            }
        } else {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, endIndex));
                }
            }
        }
        return dollTopicList;
    }

    public List<Doll> getDollListPage(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version) {
        List<Doll> dollList = getDollList(0, 1000, dollTopic, worker, version);
        if (dollList == null) {
            return null;
        }
        int size = dollList.size();
        if (offset >= size) {
            return null;
        }
        int i = offset + limit;
        List<Doll> dolls = dollList.subList(offset, i <= size ? i : size);
        return dolls;
    }

    /*@Override
    public List<Doll> DollPageById(Integer startId, Integer pageSize, Integer dollTopic, Boolean worker) {
        List<Doll> dollList = getDollList(0, 1000, dollTopic, worker);
        if (dollList == null) {
            return null;
        }
        int size = dollList.size();
        if (startId >= dollList.get(dollList.size() - 1).getId()) {
            return null;
        }
        int offset = 0;
        for (Doll doll : dollList) {
            offset++;
            if (doll.getId() == startId) {
                break;
            }
        }
        int i = offset + 10;
        List<Doll> dolls = dollList.subList(offset, i <= size ? i : size);
        return dolls;
    }*/

    @Override
//    @Cacheable(value = "h5DollList", key = "#worker+''+#type+#channels")
    public List<Doll> getH5DollList(Integer type, boolean worker, List<String> channels, Integer isTest) {
        List<Doll> dollTopicList = dollDao.getH5DollTopicList(type, channels);
        if (isTest == 1) {
            dollTopicList = dollDao.getH5DollTopicListByTest(type, channels);
        }
        if (worker) {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, doll.getName().indexOf("-")) + "-" + doll.getMachineCode());
                } else {
                    doll.setName(doll.getName() + "-" + doll.getMachineCode());
                }
            }
        } else {
            for (Doll doll : dollTopicList) {
                int endIndex = doll.getName().indexOf("-");
                if (endIndex > 0) {
                    doll.setName(doll.getName().substring(0, endIndex));
                }
            }
        }
        return dollTopicList;
    }

    public List<Doll> selectDollHistory(Integer memberId) {
        return dollDao.selectDollHistory(memberId);
    }

    public Doll selectByPrimaryKey(Integer id) {
        // TODO Auto-generated method stub
        return dollDao.selectByPrimaryKey(id);
    }

    public int getTotalCount() {
        // TODO Auto-generated method stub
        return dollDao.getTotalCount();
    }

    @Override
    public PageBean<DollAndAddress> dollList(int page, int pageSize, String name, String machineCode, String machineStates) {
        PageBean<DollAndAddress> pageBean = new PageBean<DollAndAddress>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = dollDao.totalCount(name, machineCode, machineStates);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<DollAndAddress> list = dollDao.dollList(begin, pageSize, name, machineCode, machineStates);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
//		int end=page/10*10+10>totalPage?totalPage:(page%10==0?(page-1)/10*10+10:page/10*10+10);
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }

    @Override
    public int insertSelective(Doll record) {
        record.setMachineStatus("未上线");
        record.setCreatedDate(new Date());
        record.setModifiedDate(new Date());
        record.setTbimgRealPath("1");
        return dollDao.insertSelective(record);
    }

    /**
     * 删除娃娃机
     */
    @Override
    public int dollDel(Doll doll) {
        return dollDao.dollDel(doll);
    }

    /**
     * 根据id查询娃娃机
     */
    @Override
    public Doll selectById(Doll doll) {
        return dollDao.selectById(doll);
    }

    /**
     * 更新娃娃机
     */
    @Override
    public int updateByPrimaryKeySelective(Doll record) {

        //修改主题
        DollTopic dollTopic = new DollTopic();
        dollTopic.setDollId(record.getId());
        dollTopic.setDollName(record.getName());
        dollTopicDao.updateByDollIdSelective(dollTopic);
        //修改时间
        record.setModifiedDate(new Date());
        return dollDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int totalCount(String name, String machineCode, String machineStates) {
        // TODO Auto-generated method stub
        return dollDao.totalCount(name, machineCode, machineCode);
    }

    @Override
    public List<Doll> allDollList() {
        return dollDao.allDollList();
    }

    //删除状态
    @Override
    public Integer updateDeleteStatus(Integer id) {
        dollTopicDao.deleteByDollId(id);
        return dollDao.updateDeleteStatus(id);
    }

    @Override
    public ResultMap DollList(Integer offset, Integer limit, Integer dollTopic, boolean worker, Integer version) {
        if (offset == null) {
            offset = 0;
        }
        if (limit == null) {
            limit = Enviroment.MAX_TABLE_LIMIT;
        }
        if (dollTopic == null) {
            dollTopic = 0;
        }
        List<Doll> dollList = getDollList(offset, limit, dollTopic, worker, version);
        return new ResultMap(Enviroment.RETURN_SUCCESS_CODE, dollList);
    }

    @Override
    public Integer selectTypeById(Integer dollId) {
        return dollDao.selectTypeById(dollId);
    }

    @Override
    public List<Integer> selectDollId() {
        return dollDao.selectDollId();
    }

    @Override
    public Doll getDollByDollCode(String dollCode) {
        return dollDao.getDollByDollCode(dollCode);
    }

    @Override
    public Doll spareRoom() {
        List<Doll> dolls = dollDao.selectSpareRoom();
        if (dolls.size() < 1) {
            return null;

        }
        for (Doll doll : dolls) {
            if ("空闲中".equals(doll.getMachineStatus())) {
                return doll;
            }
        }
        return dolls.get(1);
    }

    @Override
    public int updateClean(Doll doll) {
        return dollDao.updateClean(doll);
    }

    @Override
    public List<Doll> selectByTYpeAndChannel(Integer type, List<String> channel) {
        return dollDao.getH5DollTopicList(type, channel);
    }

}
