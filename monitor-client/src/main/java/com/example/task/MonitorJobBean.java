package com.example.task;

import com.example.entity.RuntimeDetail;
import com.example.utils.MonitorUtils;
import com.example.utils.NetUtils;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class MonitorJobBean extends QuartzJobBean {

    @Resource
    MonitorUtils utils;

    @Resource
    NetUtils net;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RuntimeDetail detail = utils.monitorRunTimeDetail();
        net.updateRuntimeDetail(detail);
    }
}
