package com.bfei.icrane.backend.service.impl;

import com.bfei.icrane.backend.service.DollTopicService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.dao.DollDao;
import com.bfei.icrane.core.dao.DollTopicDao;
import com.bfei.icrane.core.models.Doll;
import com.bfei.icrane.core.models.DollTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("DollTopicService")
public class DollTopicServiceImpl implements DollTopicService {
	@Autowired
	private DollTopicDao dollTopicDao;

	@Autowired
	private DollDao dollDao;

	@Override
	public int dollTopicDel(Integer id) {
		return dollTopicDao.dollTopicDel(id);
	}

	@Override
	public PageBean<DollTopic> getDollTopicList(int page, int pageSize, Integer dollId,String topicName) {
		PageBean<DollTopic> pageBean = new PageBean<DollTopic>();
		pageBean.setPage(page);
		pageBean.setPageSize(pageSize);
		int totalCount = 0;
		totalCount=dollTopicDao.totalCount(dollId,topicName);
		pageBean.setTotalCount(totalCount);
		int totalPage=0;
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		pageBean.setTotalPage(totalPage);
		int begin = (page - 1) * pageSize;
		List<DollTopic> list = dollTopicDao.getDollTopicList(begin, pageSize,dollId,topicName);
		pageBean.setList(list);
		int start=page%10==0?(page-1)/10*10+1:page/10*10+1;
		int end=page%10==0?((page-1)/10*10+10>totalPage?totalPage:(page-1)/10*10+10):(page/10*10+10>totalPage?totalPage:page/10*10+10);
		pageBean.setStart(start);
		pageBean.setEnd(end);
		return pageBean;
	}


	@Override
	public int totalCount(Integer dollId,String topicName) {
		return dollTopicDao.totalCount(dollId,topicName);
	}

	@Override
	public DollTopic getDollTopicById(Integer id) {
		return dollTopicDao.getDollTopicById(id);
	}

	@Override
	public int insertSelective(DollTopic dollTopic) {
		Doll doll = dollDao.selectByPrimaryKey(dollTopic.getDollId());
		if(doll != null){
			dollTopic.setDollName(doll.getName());
		}
		DollTopic topic = dollTopicDao.getDollTopicByName(dollTopic.getTopicName());
		Integer topicType = null;
		if (topic != null){
			 topicType = topic.getTopicType();
		} else {
			Integer maxT = dollTopicDao.maxType();
			if(maxT==null){
				maxT=0;
			}
			 topicType = maxT+1;
		}
		dollTopic.setTopicType(topicType);
		return dollTopicDao.insertSelective(dollTopic);
	}

	@Override
	public int updateByPrimaryKeySelective(DollTopic dollTopic, HttpServletRequest request) {
		Doll doll = dollDao.selectByPrimaryKey(dollTopic.getDollId());
		if(doll != null){
			dollTopic.setDollName(doll.getName());
		}
		DollTopic topic = dollTopicDao.getDollTopicByName(dollTopic.getTopicName());
		Integer topicType = null;
		if (topic != null){
			topicType = topic.getTopicType();
		} else {
			Integer maxT = dollTopicDao.maxType();
			if(maxT==null){
				maxT=0;
			}
			topicType = maxT+1;
		}
		dollTopic.setTopicType(topicType);
		return dollTopicDao.updateByPrimaryKeySelective(dollTopic);
	}
}
