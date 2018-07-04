package com.bfei.icrane.backend.service.impl;

import com.bfei.icrane.backend.service.MemberWhiteService;
import com.bfei.icrane.common.util.PageBean;
import com.bfei.icrane.core.dao.MemberWhiteDao;
import com.bfei.icrane.core.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service("MemberWhiteService")
public class MemberWhiteServiceImpl implements MemberWhiteService {
    @Autowired
    private MemberWhiteDao memberWhiteDao;


    @Override
    public int memberDel(Integer id) {
        return memberWhiteDao.memberDel(id);
    }

    @Override
    public PageBean<MemberWhite> getUserList(int page, int pageSize, String memberId) {
        PageBean<MemberWhite> pageBean = new PageBean<MemberWhite>();
        pageBean.setPage(page);
        pageBean.setPageSize(pageSize);
        int totalCount = 0;
        totalCount = memberWhiteDao.totalCount(memberId);
        pageBean.setTotalCount(totalCount);
        int totalPage = 0;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        pageBean.setTotalPage(totalPage);
        int begin = (page - 1) * pageSize;
        List<MemberWhite> list = memberWhiteDao.getUserList(begin, pageSize, memberId);
        pageBean.setList(list);
        int start = page % 10 == 0 ? (page - 1) / 10 * 10 + 1 : page / 10 * 10 + 1;
        int end = page % 10 == 0 ? ((page - 1) / 10 * 10 + 10 > totalPage ? totalPage : (page - 1) / 10 * 10 + 10) : (page / 10 * 10 + 10 > totalPage ? totalPage : page / 10 * 10 + 10);
        pageBean.setStart(start);
        pageBean.setEnd(end);
        return pageBean;
    }


    @Override
    public int totalCount(String name) {
        return memberWhiteDao.totalCount(name);
    }

    @Override
    public MemberWhite getMemberById(Integer id) {
        return memberWhiteDao.getMemberById(id);
    }

    @Override
    public int insertSelective(MemberWhite member) {
        member.setCreatedDate(new Date());
        return memberWhiteDao.insertSelective(member);
    }

    @Override
    public int updateByPrimaryKeySelective(MemberWhite member, HttpServletRequest request) {
        member.setModifiedDate(new Date());
        return memberWhiteDao.updateByPrimaryKeySelective(member);
    }
}
