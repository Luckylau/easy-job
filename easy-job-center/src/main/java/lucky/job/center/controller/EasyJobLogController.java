package lucky.job.center.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import lucky.job.center.core.EasyJobExecutorManager;
import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.EasyJobLog;
import lucky.job.center.service.IEasyJobGroupService;
import lucky.job.center.service.IEasyJobInfoService;
import lucky.job.center.service.IEasyJobLogService;
import lucky.job.core.biz.ExecutorBiz;
import lucky.job.core.exception.EasyJobException;
import lucky.job.core.model.KillParam;
import lucky.job.core.model.LogParam;
import lucky.job.core.model.LogResult;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
@Controller
@RequestMapping("/joblog")
@Slf4j
public class EasyJobLogController extends BaseController {

    @Resource
    public IEasyJobInfoService easyJobInfoService;
    @Resource
    public IEasyJobLogService easyJobLogService;
    @Resource
    private IEasyJobGroupService easyJobGroupService;
    @Value("${easy.job.accessToken}")
    private String accessToken;


    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

        // 执行器列表
        List<EasyJobGroup> jobGroupListAll = easyJobGroupService.list();

        // filter group
        List<EasyJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupListAll);
        if (jobGroupList == null || jobGroupList.isEmpty()) {
            throw new EasyJobException("jobgroup_empty");
        }

        model.addAttribute("JobGroupList", jobGroupList);

        // 任务
        if (jobId > 0) {
            EasyJobInfo jobInfo = easyJobInfoService.getById(jobId);
            if (jobInfo == null) {
                throw new RuntimeException("jobinfo_field_id system_unvalid");
            }

            model.addAttribute("jobInfo", jobInfo);

            // valid permission
            validPermission(request, jobInfo.getJobGroup());
        }

        return "joblog/joblog.index";
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public ReturnT<List<EasyJobInfo>> getJobsByGroup(int jobGroup) {
        List<EasyJobInfo> list = easyJobInfoService.list(new QueryWrapper<EasyJobInfo>().eq("job_group", jobGroup));
        return new ReturnT<>(list);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int jobId, int logStatus, String filterTime) {

        // valid permission
        // 仅管理员支持查询全部；普通用户仅支持查询有权限的 jobGroup
        validPermission(request, jobGroup);

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (filterTime != null && filterTime.trim().length() > 0) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                triggerTimeStart = DateUtil.parseDateTime(temp[0]);
                triggerTimeEnd = DateUtil.parseDateTime(temp[1]);
            }
        }

        // page query
        List<EasyJobLog> list = easyJobLogService.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);
        int listCount = easyJobLogService.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<>();
        // 总记录数
        maps.put("recordsTotal", listCount);
        // 过滤后的总记录数
        maps.put("recordsFiltered", listCount);
        // 分页列表
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(int id, Model model) {

        // base check
        EasyJobLog jobLog = easyJobLogService.getById(id);
        if (jobLog == null) {
            throw new RuntimeException("joblog_logid_unvalid");
        }

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getNano());
        model.addAttribute("logId", jobLog.getId());
        return "joblog/joblog.detail";
    }

    @RequestMapping("/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, long triggerTime, long logId, int fromLineNum) {
        try {
            ExecutorBiz executorBiz = EasyJobExecutorManager.getExecutorBiz(executorAddress, accessToken);
            ReturnT<LogResult> logResult = executorBiz.log(new LogParam(triggerTime, logId, fromLineNum));

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                EasyJobLog jobLog = easyJobLogService.getById(logId);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }
    }

    @RequestMapping("/logKill")
    @ResponseBody
    public ReturnT<String> logKill(int id) {
        // base check
        EasyJobLog easyJobLog = easyJobLogService.getById(id);
        EasyJobInfo jobInfo = easyJobInfoService.getById(easyJobLog.getJobId());
        if (jobInfo == null) {
            return new ReturnT<>(500, "jobinfo_glue_jobid_unvalid");
        }
        if (ReturnT.SUCCESS_CODE != easyJobLog.getTriggerCode()) {
            return new ReturnT<>(500, "joblog_kill_log_limit");
        }

        // request of kill
        ReturnT<String> runResult;
        try {
            ExecutorBiz executorBiz = EasyJobExecutorManager.getExecutorBiz(easyJobLog.getExecutorAddress(), accessToken);
            runResult = executorBiz.kill(new KillParam(jobInfo.getId()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            runResult = new ReturnT<>(500, e.getMessage());
        }

        if (ReturnT.SUCCESS_CODE == runResult.getCode()) {
            easyJobLog.setHandleCode(ReturnT.FAIL_CODE);
            easyJobLog.setHandleMsg("joblog_kill_log_byman :" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            easyJobLog.setHandleTime(LocalDateTime.now());
            easyJobLogService.updateById(easyJobLog);
            return new ReturnT<>(runResult.getMsg());
        } else {
            return new ReturnT<>(500, runResult.getMsg());
        }
    }

    @RequestMapping("/clearLog")
    @ResponseBody
    public ReturnT<String> clearLog(int jobGroup, int jobId, int type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = -1;
        if (type == 1) {
            // 清理一个月之前日志数据
            clearBeforeTime = DateUtil.addMonths(new Date(), -1);
        } else if (type == 2) {
            // 清理三个月之前日志数据
            clearBeforeTime = DateUtil.addMonths(new Date(), -3);
        } else if (type == 3) {
            // 清理六个月之前日志数据
            clearBeforeTime = DateUtil.addMonths(new Date(), -6);
        } else if (type == 4) {
            // 清理一年之前日志数据
            clearBeforeTime = DateUtil.addYears(new Date(), -1);
        } else if (type == 5) {
            // 清理一千条以前日志数据
            clearBeforeNum = 1000;
        } else if (type == 6) {
            // 清理一万条以前日志数据
            clearBeforeNum = 10000;
        } else if (type == 7) {
            // 清理三万条以前日志数据
            clearBeforeNum = 30000;
        } else if (type == 8) {
            // 清理十万条以前日志数据
            clearBeforeNum = 100000;
        } else if (type == 9) {
            // 清理所有日志数据
            clearBeforeNum = 0;
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, "joblog_clean_type_unvalid");
        }

        List<Long> logIds;
        do {
            logIds = easyJobLogService.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum, 1000);
            if (logIds != null && logIds.size() > 0) {
                easyJobLogService.clearLog(logIds);
            }
        } while (logIds != null && logIds.size() > 0);

        return ReturnT.SUCCESS;
    }

}
