package lucky.job.center.controller;


import lucky.job.center.core.route.ExecutorBlockStrategyEnum;
import lucky.job.center.core.route.ExecutorRouteStrategyEnum;
import lucky.job.center.core.trigger.EasyJobTriggerPool;
import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.entity.TriggerTypeEnum;
import lucky.job.center.service.IEasyJobGroupService;
import lucky.job.center.service.IEasyJobService;
import lucky.job.center.service.LoginService;
import lucky.job.core.cron.CronExpression;
import lucky.job.core.exception.EasyJobException;
import lucky.job.core.glue.GlueTypeEnum;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/jobInfo")
public class EasyJobInfoController extends BaseController {

    @Resource
    public IEasyJobGroupService easyJobGroupService;
    @Resource
    private IEasyJobService easyJobService;
    @Resource
    private EasyJobTriggerPool easyJobTriggerPool;


    public static void validPermission(HttpServletRequest request, int jobGroup) {
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (!loginUser.validPermission(jobGroup)) {
            throw new RuntimeException("system_permission_limit" + "[username=" + loginUser.getUsername() + "]");
        }
    }

    @RequestMapping
    public String index(HttpServletRequest request, Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

        // 枚举-字典
        // 路由策略-列表
        model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());
        // Glue类型-字典
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());
        // 阻塞处理策略-字典
        model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());

        // 执行器列表
        List<EasyJobGroup> jobGroupAll = easyJobGroupService.list();

        // filter group
        List<EasyJobGroup> jobGroupList = filterJobGroupByRole(request, jobGroupAll);
        if (jobGroupList == null || jobGroupList.size() == 0) {
            throw new EasyJobException("jobgroup_empty");
        }

        model.addAttribute("JobGroupList", jobGroupList);
        model.addAttribute("jobGroup", jobGroup);

        return "jobinfo/jobinfo.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author) {

        return easyJobService.pageList(start, length, jobGroup, triggerStatus, jobDesc, executorHandler, author);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ReturnT<String> add(EasyJobInfo jobInfo) {
        return easyJobService.add(jobInfo);
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(EasyJobInfo jobInfo) {
        return easyJobService.update(jobInfo);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {
        return easyJobService.remove(id);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public ReturnT<String> pause(int id) {
        return easyJobService.stop(id);
    }

    @RequestMapping("/start")
    @ResponseBody
    public ReturnT<String> start(int id) {
        return easyJobService.start(id);
    }

    @RequestMapping("/trigger")
    @ResponseBody
    public ReturnT<String> triggerJob(int id, String executorParam, String addressList) {
        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        easyJobTriggerPool.addTrigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/nextTriggerTime")
    @ResponseBody
    public ReturnT<List<String>> nextTriggerTime(String cron) {
        List<String> result = new ArrayList<>();
        try {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            for (int i = 0; i < 5; i++) {
                lastTime = cronExpression.getNextValidTimeAfter(lastTime);
                if (lastTime != null) {
                    result.add(DateUtil.formatDateTime(lastTime));
                } else {
                    break;
                }
            }
        } catch (ParseException e) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "jobinfo_field_cron_unvalid");
        }
        return new ReturnT<>(result);
    }
}
