package lucky.job.center.service.impl;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.core.trigger.EasyJobTriggerPool;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.EasyJobLog;
import lucky.job.center.entity.TriggerTypeEnum;
import lucky.job.center.service.IEasyJobGroupService;
import lucky.job.center.service.IEasyJobInfoService;
import lucky.job.center.service.IEasyJobLogService;
import lucky.job.center.service.IEasyJobRegistryService;
import lucky.job.core.biz.AdminBiz;
import lucky.job.core.handler.IJobHandler;
import lucky.job.core.model.HandleCallbackParam;
import lucky.job.core.model.RegistryParam;
import lucky.job.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/12/15 16:46
 * @Description:
 */
@Service
@Slf4j
public class AdminBizImpl implements AdminBiz {

    @Autowired
    public IEasyJobLogService easyJobLogService;
    @Autowired
    private IEasyJobInfoService easyJobInfoService;
    @Autowired
    private IEasyJobRegistryService easyJobRegistryService;
    @Autowired
    private IEasyJobGroupService easyJobGroupService;

    @Autowired
    private EasyJobTriggerPool easyJobTriggerPool;

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            ReturnT<String> callbackResult = callback(handleCallbackParam);
            log.debug(">>>>>>>>> JobApiController.callback {}, handleCallbackParam={}, callbackResult={}",
                    (callbackResult.getCode() == IJobHandler.SUCCESS.getCode() ? "success" : "fail"), handleCallbackParam, callbackResult);
        }

        return ReturnT.SUCCESS;
    }

    private ReturnT<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        EasyJobLog log = easyJobLogService.getById(handleCallbackParam.getLogId());
        if (log == null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            // avoid repeat callback, trigger child job etc
            return new ReturnT<>(ReturnT.FAIL_CODE, "log repeat callback.");
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (IJobHandler.SUCCESS.getCode() == handleCallbackParam.getExecuteResult().getCode()) {
            EasyJobInfo easyJobInfo = easyJobInfoService.getById(log.getJobId());
            if (easyJobInfo != null && easyJobInfo.getChildJobId() != null && easyJobInfo.getChildJobId().trim().length() > 0) {
                callbackMsg = "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>jobconf_trigger_child_run <<<<<<<<<<< </span><br>";

                String[] childJobIds = easyJobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId = (childJobIds[i] != null && childJobIds[i].trim().length() > 0 && isNumeric(childJobIds[i])) ? Integer.valueOf(childJobIds[i]) : -1;
                    if (childJobId > 0) {

                        easyJobTriggerPool.addTrigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null, null);
                        ReturnT<String> triggerChildResult = ReturnT.SUCCESS;

                        // add msg
                        callbackMsg += MessageFormat.format("jobconf_callback_child_msg1",
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i],
                                (triggerChildResult.getCode() == ReturnT.SUCCESS_CODE ? "system_success" : "system_fail"),
                                triggerChildResult.getMsg());
                    } else {
                        callbackMsg += MessageFormat.format("jobconf_callback_child_msg2",
                                (i + 1),
                                childJobIds.length,
                                childJobIds[i]);
                    }
                }

            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMsg() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMsg());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        if (handleMsg.length() > 15000) {
            // text最大64kb 避免长度过长
            handleMsg = new StringBuffer(handleMsg.substring(0, 15000));
        }

        // success, save log
        log.setHandleTime(LocalDateTime.now());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        easyJobLogService.updateHandleInfo(log);

        return ReturnT.SUCCESS;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument");
        }

        int ret = easyJobRegistryService.registryUpdate(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
        if (ret < 1) {
            easyJobRegistryService.registrySave(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue(), new Date());
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        // valid
        if (!StringUtils.hasText(registryParam.getRegistryGroup())
                || !StringUtils.hasText(registryParam.getRegistryKey())
                || !StringUtils.hasText(registryParam.getRegistryValue())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "Illegal Argument.");
        }

        easyJobRegistryService.registryDelete(registryParam.getRegistryGroup(), registryParam.getRegistryKey(), registryParam.getRegistryValue());
        return ReturnT.SUCCESS;
    }
}
