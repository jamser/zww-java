package com.bfei.icrane.core.service;

import com.bfei.icrane.core.models.OemTemplate;

/**
 * Created by moying on 2018/8/22.
 */
public interface OemTemplateService {
    int deleteByPrimaryKey(Integer id);

    int insert(OemTemplate record);

    int insertSelective(OemTemplate record);

    OemTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OemTemplate record);

    int updateByPrimaryKey(OemTemplate record);

    OemTemplate selectByOemIdAndType(Integer oemId,String type);
}
