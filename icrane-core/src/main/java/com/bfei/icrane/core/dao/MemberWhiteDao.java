package com.bfei.icrane.core.dao;

import com.bfei.icrane.core.models.MemberWhite;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author mwan Version: 1.0 Date: 2017/9/16 Description: 用户Dao接口类. Copyright
 *         (c) 2017 伴飞网络. All rights reserved.
 */
public interface MemberWhiteDao {


	/**
	 * 更新用户白名单属性
	 */
	int updateByPrimaryKeySelective(MemberWhite member);
	int insertSelective(MemberWhite member);
	/**
	 * 分页查询用户白名单列表
	 */
	List<MemberWhite> getUserList(@Param("begin") int begin, @Param("pageSize") int pageSize, @Param("memberId") String memberId);
	/**
	 * 总记录数
	 */
	int totalCount(@Param("memberId") String memberId);

	MemberWhite getMemberById(@Param("id") Integer id);

	int memberDel(@Param("id") Integer id);

}




