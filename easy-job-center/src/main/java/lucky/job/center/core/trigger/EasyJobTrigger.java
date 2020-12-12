package lucky.job.center.core.trigger;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.core.EasyJobExecutorManager;
import lucky.job.center.core.route.ExecutorBlockStrategyEnum;
import lucky.job.center.core.route.ExecutorRouteStrategyEnum;
import lucky.job.center.dao.EasyJobGroupMapper;
import lucky.job.center.dao.EasyJobInfoMapper;
import lucky.job.center.dao.EasyJobLogMapper;
import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.EasyJobLog;
import lucky.job.center.entity.TriggerTypeEnum;
import lucky.job.center.util.NetUtil;
import lucky.job.core.biz.ExecutorBiz;
import lucky.job.core.model.ReturnT;
import lucky.job.core.model.TriggerParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author: luckylau
 * @Date: 2020/11/30 21:04
 * @Description:
 */
@Service
@Slf4j
public class EasyJobTrigger {

    @Autowired
    private EasyJobInfoMapper easyJobInfoMapper;

    @Autowired
    private EasyJobGroupMapper easyJobGroupMapper;

    @Autowired
    private EasyJobLogMapper easyJobLogMapper;


    @Value("${easy.job.accessToken}")
    private String accessToken;

    private static boolean isNumeric(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void trigger(int jobId,
                        TriggerTypeEnum triggerType,
                        int failRetryCount,
                        String executorShardingParam,
                        String executorParam,
                        String addressList) {

        // load data
        EasyJobInfo jobInfo = easyJobInfoMapper.selectById(jobId);
        if (jobInfo == null) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount = failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        EasyJobGroup group = easyJobGroupMapper.selectById(jobInfo.getJobGroup());

        // cover addressList
        if (addressList != null && addressList.trim().length() > 0) {
            group.setAddressType(1);
            group.setAddressList(addressList.trim());
        }

        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.valueOf(shardingArr[0]);
                shardingParam[1] = Integer.valueOf(shardingArr[1]);
            }
        }
        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
                && group.getRegistryList() != null && !group.getRegistryList().isEmpty() && shardingParam == null) {
            for (int i = 0; i < group.getRegistryList().size(); i++) {
                processTrigger(group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
            }
        } else {
            if (shardingParam == null) {
                shardingParam = new int[]{0, 1};
            }
            processTrigger(group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
        }

    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private void processTrigger(EasyJobGroup group, EasyJobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) {

        // block strategy
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        // route strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) ? String.valueOf(index).concat("/").concat(String.valueOf(total)) : null;

        // 1、save log-id
        EasyJobLog jobLog = new EasyJobLog();
        jobLog.setJobGroup(jobInfo.getJobGroup());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(LocalDateTime.now());
        easyJobLogMapper.insert(jobLog);
        log.debug(">>>>>>>>>>> easy-job trigger start, jobId:{}", jobLog.getId());

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
        triggerParam.setExecutorParams(jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getNano());
        triggerParam.setGlueType(jobInfo.getGlueType());
        triggerParam.setGlueSource(jobInfo.getGlueSource());
        triggerParam.setGlueUpdateTime(jobInfo.getGlueUpdateTime().getNano());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        ReturnT<String> routeAddressResult = null;
        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
                if (index < group.getRegistryList().size()) {
                    address = group.getRegistryList().get(index);
                } else {
                    address = group.getRegistryList().get(0);
                }
            } else {
                routeAddressResult = executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
                if (routeAddressResult.getCode() == ReturnT.SUCCESS_CODE) {
                    address = routeAddressResult.getContent();
                }
            }
        } else {
            routeAddressResult = new ReturnT<>(ReturnT.FAIL_CODE, "jobconf_trigger_address_empty");
        }

        // 4、trigger remote executor
        ReturnT<String> triggerResult;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = new ReturnT<>(ReturnT.FAIL_CODE, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer()
                .append("jobconf_trigger_type：")
                .append(triggerType.getTitle())
                .append("<br>")
                .append("jobconf_trigger_admin_adress").append("：").append(NetUtil.getLocalIP())
                .append("<br>").append("jobconf_trigger_exe_regtype").append("：")
                .append((group.getAddressType() == 0) ? "jobgroup_field_addressType_0" : "jobgroup_field_addressType_1")
                .append("<br>").append("jobconf_trigger_exe_regaddress").append("：").append(group.getRegistryList())
                .append("<br>").append("jobinfo_field_executorRouteStrategy").append("：").append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(").append(shardingParam).append(")");
        }
        triggerMsgSb.append("<br>").append("jobinfo_field_executorBlockStrategy").append("：").append(blockStrategy.getTitle());
        triggerMsgSb.append("<br>").append("jobinfo_field_timeout").append("：").append(jobInfo.getExecutorTimeout());
        triggerMsgSb.append("<br>").append("jobinfo_field_executorFailRetryCount").append("：").append(finalFailRetryCount);

        triggerMsgSb.append("<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>" + "jobconf_trigger_run" + "<<<<<<<<<<< </span><br>")
                .append((routeAddressResult != null && routeAddressResult.getMsg() != null) ? routeAddressResult.getMsg() + "<br><br>" : "").append(triggerResult.getMsg() != null ? triggerResult.getMsg() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        easyJobLogMapper.updateById(jobLog);

        log.debug(">>>>>>>>>>> easy-job trigger end, jobId:{}", jobLog.getId());
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    private ReturnT<String> runExecutor(TriggerParam triggerParam, String address) {
        ReturnT<String> runResult;
        try {
            ExecutorBiz executorBiz = EasyJobExecutorManager.getExecutorBiz(address, accessToken);
            if (executorBiz == null) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "address: " + address + " executorBiz is null");
            }
            runResult = executorBiz.run(triggerParam);
        } catch (Exception e) {
            log.error(">>>>>>>>>>> easy-job trigger error, please check if the executor[{}] is running.", address, e);
            runResult = new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }

        StringBuffer sb = new StringBuffer("jobconf_trigger_run：")
                .append("<br>address：").append(address)
                .append("<br>code：").append(runResult.getCode())
                .append("<br>msg：").append(runResult.getMsg());

        runResult.setMsg(sb.toString());
        return runResult;
    }


}
