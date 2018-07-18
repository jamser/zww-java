package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.MemberComment;

public interface MemberCommentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MemberComment record);

    int insertSelective(MemberComment record);

    MemberComment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MemberComment record);

    int updateByPrimaryKey(MemberComment record);
}