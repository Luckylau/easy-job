package lucky.job.center.core.monitor;

import lucky.job.center.dao.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author : luckylau
 * @Date: 2020/11/20 17:43
 * @Description:
 */
@Component
public class EasyJobMonitor implements DisposableBean {

    @Autowired
    private EasyJobGroupMapper easyJobGroupMapper;

    @Autowired
    private EasyJobRegistryMapper easyJobRegistryMapper;

    @Autowired
    private EasyJobLogMapper easyJobLogMapper;

    @Autowired
    private EasyJobInfoMapper easyJobInfoMapper;

    @Autowired
    private EasyJobLogReportMapper easyJobLogReportMapper;


    private JobFailMonitor jobFailMonitor;

    private JobLogReportMonitor jobLogReportMonitor;

    private JobLosedMonitor jobLosedMonitor;

    private JobRegistryMonitor jobRegistryMonitor;


    @PostConstruct
    private void init() {
        jobFailMonitor = new JobFailMonitor(easyJobLogMapper, easyJobInfoMapper);
        jobFailMonitor.startup();

        jobLogReportMonitor = new JobLogReportMonitor(easyJobLogMapper, easyJobLogReportMapper);
        jobLogReportMonitor.startup();

        jobLosedMonitor = new JobLosedMonitor(easyJobLogMapper);
        jobLosedMonitor.startup();

        jobRegistryMonitor = new JobRegistryMonitor(easyJobGroupMapper, easyJobRegistryMapper);
        jobRegistryMonitor.startup();
    }

    @Override
    public void destroy() {
        jobFailMonitor.shutdown();
        jobLogReportMonitor.shutdown();
        jobLosedMonitor.shutdown();
        jobRegistryMonitor.shutdown();
    }

}
