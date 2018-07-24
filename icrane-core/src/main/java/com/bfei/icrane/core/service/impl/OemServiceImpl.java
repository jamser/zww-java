package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.core.dao.OemBannerMapper;
import com.bfei.icrane.core.dao.OemMapper;
import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.models.OemBanner;
import com.bfei.icrane.core.service.OemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SUN on 2018/1/10.
 */
@Service("OemService")
@Transactional
public class OemServiceImpl implements OemService {

    @Autowired
    private OemMapper oemMapper;
    @Autowired
    private OemBannerMapper oemBannerMapper;


    /**
     * 根据id查询账户信息
     *
     * @param id
     * @return
     */
    @Override
    public Oem selectOemById(Integer id) {
        return oemMapper.selectByPrimaryKey(id);
    }

    @Override
    public Oem selectByCode(String code) {
        return oemMapper.selectByCode(code);
    }

    @Override
    public List<Oem> selectAllOem() {
        return oemMapper.selectAllOem();
    }

    @Override
    public OemBanner selectOemBannerById(Integer id) {
        return oemBannerMapper.selectByPrimaryKey(id);
    }

    @Override
    @Cacheable(value = "H5BannerList", key = "#oemId")
    public List<OemBanner> selectByOemId(Integer oemId) {
        return oemBannerMapper.selectByOemId(oemId);
    }

    @Override
    public List<OemBanner> selectAllOemBanner() {
        return oemBannerMapper.selectAllOemBanner();
    }


}
