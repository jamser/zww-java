package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.Oem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Oem record);

    int insertSelective(Oem record);

    Oem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Oem record);

    int updateByPrimaryKey(Oem record);

    Oem selectByCode(@Param("code") String code);

    List<Oem> selectAllOem();
}