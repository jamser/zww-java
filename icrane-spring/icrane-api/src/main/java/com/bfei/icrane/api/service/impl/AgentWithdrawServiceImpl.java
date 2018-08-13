package com.bfei.icrane.api.service.impl;

import com.bfei.icrane.api.service.AgentWithdrawService;
import com.bfei.icrane.api.service.SystemPrefService;
import com.bfei.icrane.common.util.Enviroment;
import com.bfei.icrane.common.util.ResultMap;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.common.util.WXUtil;
import com.bfei.icrane.core.dao.AgentMapper;
import com.bfei.icrane.core.dao.AgentWithdrawMapper;
import com.bfei.icrane.core.dao.BankInfoMapper;
import com.bfei.icrane.core.models.Agent;
import com.bfei.icrane.core.models.AgentWithdraw;
import com.bfei.icrane.core.models.BankInfo;
import com.bfei.icrane.core.models.SystemPref;
import com.bfei.icrane.core.models.vo.AgentWithdrawVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by moying on 2018/6/6.
 */
@Service("AgentWithdrawService")
public class AgentWithdrawServiceImpl implements AgentWithdrawService {

    @Autowired
    private AgentWithdrawMapper agentWithdrawMapper;

    @Autowired
    private BankInfoMapper bankInfoMapper;

    @Autowired
    private AgentMapper agentMapper;


    @Autowired
    private SystemPrefService systemPrefService;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return agentWithdrawMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(AgentWithdraw record) {
        return agentWithdrawMapper.insert(record);
    }

    @Override
    public int insertSelective(AgentWithdraw record) {
        return agentWithdrawMapper.insertSelective(record);
    }

    @Override
    public AgentWithdraw selectByPrimaryKey(Integer id) {
        return agentWithdrawMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(AgentWithdraw record) {
        return agentWithdrawMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(AgentWithdraw record) {
        return agentWithdrawMapper.updateByPrimaryKey(record);
    }

    @Override
    public Long selectByWithdraw(Integer id) {
        return agentWithdrawMapper.selectByWithdraw(id);
    }

    @Override
    public List<AgentWithdrawVO> selectByWithdrawLists(Integer id) {
        return agentWithdrawMapper.selectByWithdrawLists(id);
    }

    @Override
    @Transactional
    public ResultMap withdrawByAgent(Agent agent, Integer bankId) {
        Date now = new Date();
        SystemPref START_DATE = systemPrefService.selectByPrimaryKey("START_DATE");//提现开始时间
        SystemPref END_DATE = systemPrefService.selectByPrimaryKey("END_DATE");//提现结束时间
        if (START_DATE != null && END_DATE != null) {
            SimpleDateFormat sim = new SimpleDateFormat("HHmmss");
            long start = Long.valueOf(START_DATE.getValue());
            long end = Long.valueOf(END_DATE.getValue());
            long nowTime = Long.valueOf(sim.format(now));
            if (nowTime < start || nowTime > end) {
                return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "提现时间：早8:30到晚17:30，其他时间段关闭提现！");
            }
        }
        Integer isHostory = WXUtil.isHostory(now);
        if (isHostory != null && isHostory != 0) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "暂不支持节假日提现！");
        }
        if (isHostory == null && WXUtil.isWeekend(now)) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "暂不支持周末提现！");
        }
        if (agent.getStatus() == 2) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "该账户已被冻结，请联系上级代理查询！");
        }
        if (agent.getStatus() == 3) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "该账户已失效!");
        }

        BankInfo bankInfo = bankInfoMapper.selectByPrimaryKey(bankId);
        if (bankInfo == null) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "到账卡号选择错误，请重新选择！");
        }
        Long balance = agent.getBalance() - agent.getBalanceDisabled();//余额(单位：分)
        SystemPref MIN_WITHDRAW = systemPrefService.selectByPrimaryKey("MIN_WITHDRAW");
        SystemPref SERVICE_CHARGE = systemPrefService.selectByPrimaryKey("SERVICE_CHARGE");
        long min = MIN_WITHDRAW == null ? 10000 : Long.valueOf(MIN_WITHDRAW.getValue());//最小提现金额(单位：分)
        long fee = SERVICE_CHARGE == null ? 200 : Long.valueOf(SERVICE_CHARGE.getValue());//手续费(单位：分)
        if (balance < min) {
            return new ResultMap(Enviroment.RETURN_UNAUTHORIZED_CODE1, "余额不足" + min * 0.01 + "元,提现失败！");
        }
        AgentWithdraw agentWithdraw = new AgentWithdraw();
        agentWithdraw.setAgentid(bankInfo.getAgentId());
        agentWithdraw.setStatus(Byte.valueOf("0"));
        agentWithdraw.setTradeNo(StringUtils.getOrderNumber());
        agentWithdraw.setAmount(balance);
        agentWithdraw.setFee(fee);
        agentWithdraw.setActualAmount(balance - fee);
        agentWithdraw.setName(bankInfo.getName());
        agentWithdraw.setPhone(bankInfo.getPhone());
        agentWithdraw.setIdCardNo(bankInfo.getIdCardNo());
        agentWithdraw.setCardNo(bankInfo.getCardNo());
        agentWithdraw.setCreateDate(now);
        agentWithdrawMapper.insert(agentWithdraw);
        //减余额
        Agent tAgent = new Agent();
        tAgent.setId(agent.getId());
        tAgent.setBalance(0L);//提现直接提完
        agentMapper.updateByPrimaryKeySelective(tAgent);
        return new ResultMap(Enviroment.AGENT_WITHDRAW_SUCCESS);
    }
}
