package org.jeecg.modules.job.home.service;

import org.jeecg.modules.job.job.service.IJobPostService;
import org.jeecg.modules.job.ums.mapper.UmsAccountMapper;
import org.jeecg.modules.job.ums.service.IUmsAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class HomeService implements IHomeService {
    @Resource
    private UmsAccountMapper accountMapper;

    @Override
    public Map<String,Object> getUserCountInfo(String startTime, String endTime, String roleId) {
        return accountMapper.getUserCountInfo(startTime,endTime,roleId);
    }

    @Override
    public Map<String,Object> getCompanyCountInfo(String startTime, String endTime,String authStatus) {
        return accountMapper.getCompanyCountInfo(startTime,endTime,authStatus);
    }

    @Override
    public Map<String,Object> getPositionCountInfo(String startTime, String endTime,String status) {
        return accountMapper.getPostCountInfo(startTime,endTime,status);
    }

    @Override
    public Map<String, Object> getOrderCountInfo(String startTime, String endTime, String orderStatus) {
        return accountMapper.getOrderCountInfo(startTime,endTime,orderStatus);
    }
}
