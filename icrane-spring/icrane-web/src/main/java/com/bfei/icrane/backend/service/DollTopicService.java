package com.bfei.icrane.backend.service;

import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.models.DollTopic;

import javax.servlet.http.HttpServletRequest;

public interface DollTopicService {
	public PageBean<DollTopic> getDollTopicList(int page, int pageSize, Integer dollId,String topicName);
	public int totalCount(Integer dollId,String topicName);
	//搜索
	public DollTopic getDollTopicById(Integer id);
	public int updateByPrimaryKeySelective(DollTopic dollTopic, HttpServletRequest request);
	public int insertSelective(DollTopic dollTopic);
	public int dollTopicDel(Integer id);
}
