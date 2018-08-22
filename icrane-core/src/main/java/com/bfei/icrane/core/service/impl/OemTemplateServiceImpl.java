package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.OemTemplateMapper;
import com.bfei.icrane.core.models.OemTemplate;
import com.bfei.icrane.core.service.OemTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by moying on 2018/8/22.
 */
@Service
public class OemTemplateServiceImpl implements OemTemplateService {

    @Autowired
    private OemTemplateMapper oemTemplateMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return oemTemplateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(OemTemplate record) {
        return oemTemplateMapper.insert(record);
    }

    @Override
    public int insertSelective(OemTemplate record) {
        return oemTemplateMapper.insertSelective(record);
    }

    @Override
    public OemTemplate selectByPrimaryKey(Integer id) {
        return oemTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(OemTemplate record) {
        return oemTemplateMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(OemTemplate record) {
        return oemTemplateMapper.updateByPrimaryKey(record);
    }
}
