package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.MemberChargeHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by webrx on 2017-11-30.
 */
public interface MemberChargeHistoryDao {
    /**
     * 后台查询列表
     */
    List<MemberChargeHistory> selectChargeOrderList(@Param("userId")Integer userId,@Param("begin")int begin, @Param("pageSize")int pageSize);

    /**
     * 分页总条数
     * @return
     */
    int totalCount(@Param("userId")Integer userId);

    int insertSelective(MemberChargeHistory memberChargeHistory);
}
