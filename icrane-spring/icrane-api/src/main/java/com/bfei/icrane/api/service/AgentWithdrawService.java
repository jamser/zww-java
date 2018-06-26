package com.bfei.icrane.api.service;

import com.bfei.icrane.core.models.AgentWithdraw;
import com.bfei.icrane.core.models.vo.AgentWithdrawVO;

import java.util.List;

/**
 * Created by moying on 2018/6/6.
 */
public interface AgentWithdrawService {
    int deleteByPrimaryKey(Integer id);

    int insert(AgentWithdraw record);

    int insertSelective(AgentWithdraw record);

    AgentWithdraw selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AgentWithdraw record);

    int updateByPrimaryKey(AgentWithdraw record);

    Long selectByWithdraw(Integer id);

    List<AgentWithdrawVO> selectByWithdrawLists(Integer id);
}
