package com.bfei.icrane.core.service.impl;

import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.dao.*;
import com.bfei.icrane.core.models.*;
import com.bfei.icrane.core.service.AccountService;
import com.bfei.icrane.core.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SUN on 2018/1/10.
 */
@Service("AccountService")
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private VipService vipService;
    @Autowired
    private SystemPrefDao systemPrefDao;
    @Autowired
    private ChargeDao chargeDao;

    /**
     * 根据id查询账户信息
     *
     * @param id
     * @return
     */
    @Override
    public Account selectById(Integer id) {
        Account account = accountDao.selectById(id);
        if (account == null) {
            createAccount(id);
            account = accountDao.selectById(id);
        }
        return account;
    }
    /*
     *  public ResultMap selectById(Integer id) {
        Account account = accountDao.selectById(id);
        if (account == null) {
            createAccount(id);
            account = accountDao.selectById(id);
        }
        return new ResultMap(Enviroment.RETURN_SUCCESS_MESSAGE, account);
    }
     */

    @Override
    public Account select(Integer id) {
        return accountDao.selectById(id);
    }

    /**
     * 创建用户
     *
     * @param id
     */
    @Override
    public Account createAccount(Integer id) {
        Account account = new Account();
        account.setId(id);
        Integer coinsById = memberDao.getCoinsById(id);
        account.setCoins(coinsById == null ? 0 : coinsById);
        accountDao.insert(account);
        return account;
    }

    @Override
    public void insert(Account account) {
        accountDao.insert(account);
    }

    @Override
    public void updateMemberGrowthValue(Account account) {
        accountDao.updateMemberGrowthValue(account);
    }

    @Override
    public void updateMemberCoin(Account account) {
        accountDao.updateMemberCoin(account);
    }

    @Override
    public void updateMemberSuperTicket(Account account) {
        accountDao.updateMemberSuperTicket(account);
    }

    @Override
    public void updateMemberSeeksCardState(Account account) {
        accountDao.updateMemberSeeksCardState(account);
    }

    @Override
    public void updateMemberMonthCardState(Account account) {
        accountDao.updateMemberMonthCardState(account);
    }


    @Override
    public Integer selectId(int id) {
        return accountDao.selectId(id);
    }

    @Override
    public void updateBitStatesById(Account account) {
        accountDao.updateBitStatesById(account);
    }

    @Override
    public Map vip(Integer memberId) {
        HashMap<String, Object> map = new HashMap<>();
        Account account = accountDao.selectById(memberId);
        List<Vip> allVip = vipService.getAll();
        Vip basevip = account.getVip();
        BigDecimal growthValue = account.getGrowthValue();
        map.put("vip", basevip);

        map.put("next", -1);
        for (Vip vip : allVip) {
            if (vip.getLevel() == basevip.getLevel() + 1) {
                BigDecimal nextGrowthValue = allVip.get(basevip.getLevel() + 1).getLeastAllowed();
                map.put("next", nextGrowthValue.subtract(growthValue));
            }
        }

        BigDecimal maxGrowthValue = allVip.get(allVip.size() - 1).getLeastAllowed();
        map.put("max", maxGrowthValue.subtract(growthValue).compareTo(new BigDecimal(0)) == 1 ? maxGrowthValue.subtract(growthValue) : -1);


        switch (basevip.getLevel()) {
            case 0:
                map.put("myPermission", "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/0ff28718-b187-4215-82fd-d776436ef1aa.png");
                break;
            case 1:
                map.put("myPermission", "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/a1892b7c-7ae2-4a80-b9f0-0db0acc88ce7.png");
                break;
            default:
                map.put("myPermission", "https://lanao.oss-cn-shenzhen.aliyuncs.com/other/d8650796-d74c-43ef-add8-9c948d2b821f.png");
                break;
        }

        String allPermission = systemPrefDao.selectByPrimaryKey("ALLPERMISSION").getValue();
        map.put("allPermission", StringUtils.isNotEmpty(allPermission) ? allPermission : "");

        return map;
    }

    @Override
    public List<Account> selectPayingUser() {
        return accountDao.selectPayingUser();
    }

    @Override
    public boolean whetherTheDowngrade(Integer memberId) {
        Charge charge = chargeDao.whetherTheDowngrade(memberId);
        if (charge == null) {
            return false;
        } else if (charge.getGrowthValue().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        return true;
    }

}
