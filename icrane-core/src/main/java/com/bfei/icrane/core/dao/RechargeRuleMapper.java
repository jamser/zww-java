package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.RechargeRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeRuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRule record);

    int insertSelective(RechargeRule record);

    RechargeRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRule record);

    int updateByPrimaryKey(RechargeRule record);

    List<RechargeRule> selectByAll(@Param("type") Integer type);
}