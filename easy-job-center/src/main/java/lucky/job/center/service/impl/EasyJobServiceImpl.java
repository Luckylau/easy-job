package lucky.job.center.service.impl;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.dao.*;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.service.IEasyJobService;
import lucky.job.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author: luckylau
 * @Date: 2020/12/15 16:25
 * @Description:
 */
@Service
@Slf4j
public class EasyJobServiceImpl implements IEasyJobService {
    @Autowired
    private EasyJobGroupMapper easyJobGroupMapper;

    @Autowired
    private EasyJobInfoMapper easyJobInfoMapper;

    @Autowired
    private EasyJobLogMapper easyJobLogMapper;

    @Autowired
    private EasyJobLogGlueMapper easyJobLogGlueMapper;

    @Autowired
    private EasyJobLogReportMapper easyJobLogReportMapper;

    @Override
    public Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {
        return null;
    }

    @Override
    public ReturnT<String> add(EasyJobInfo jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> update(EasyJobInfo jobInfo) {
        return null;
    }

    @Override
    public ReturnT<String> remove(int id) {
        return null;
    }

    @Override
    public ReturnT<String> start(int id) {
        return null;
    }

    @Override
    public ReturnT<String> stop(int id) {
        return null;
    }

    @Override
    public Map<String, Object> dashboardInfo() {
        return null;
    }

    @Override
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        return null;
    }
}
