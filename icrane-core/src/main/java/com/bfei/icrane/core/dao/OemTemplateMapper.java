package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.OemTemplate;

public interface OemTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OemTemplate record);

    int insertSelective(OemTemplate record);

    OemTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OemTemplate record);

    int updateByPrimaryKey(OemTemplate record);
}