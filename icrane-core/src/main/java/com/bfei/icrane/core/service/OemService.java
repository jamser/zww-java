package com.bfei.icrane.core.service;


import com.bfei.icrane.core.models.Oem;
import com.bfei.icrane.core.models.OemBanner;

import java.util.List;

/**
 * Created by SUN on 2018/3/2.
 * 用户账户相关服务类接口
 */
public interface OemService {

    Oem selectOemById(Integer id);

    Oem selectByCode(String code);

    List<Oem> selectAllOem();

    OemBanner selectOemBannerById(Integer id);

    List<OemBanner> selectByOemId(Integer oemId);

    List<OemBanner> selectAllOemBanner();
}
