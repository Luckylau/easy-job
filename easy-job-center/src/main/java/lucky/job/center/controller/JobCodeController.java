package lucky.job.center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lucky.job.center.entity.EasyJobInfo;
import lucky.job.center.entity.EasyJobLogGlue;
import lucky.job.center.service.IEasyJobInfoService;
import lucky.job.center.service.IEasyJobLogGlueService;
import lucky.job.core.exception.EasyJobException;
import lucky.job.core.glue.GlueTypeEnum;
import lucky.job.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/12/15 19:47
 * @Description:
 */
@Controller
@RequestMapping("/jobcode")
public class JobCodeController extends BaseController {

    @Autowired
    private IEasyJobInfoService iEasyJobInfoService;

    @Autowired
    private IEasyJobLogGlueService iEasyJobLogGlueService;


    @RequestMapping
    public String index(HttpServletRequest request, Model model, int jobId) {
        EasyJobInfo jobInfo = iEasyJobInfoService.getById(jobId);
        List<EasyJobLogGlue> jobLogGlues = iEasyJobLogGlueService.list(new QueryWrapper<EasyJobLogGlue>().eq("job_id", jobId));

        if (jobInfo == null) {
            throw new EasyJobException("jobinfo_glue_jobid_unvalid");
        }
        if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
            throw new EasyJobException("jobinfo_glue_gluetype_unvalid");
        }

        // valid permission
        validPermission(request, jobInfo.getJobGroup());

        // Glue类型-字典
        model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());

        model.addAttribute("jobInfo", jobInfo);
        model.addAttribute("jobLogGlues", jobLogGlues);
        return "jobcode/jobcode.index";
    }

    @RequestMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> save(Model model, int id, String glueSource, String glueRemark) {
        // valid
        if (glueRemark == null) {
            return new ReturnT<>(500, "system_please_input" + "jobinfo_glue_remark");
        }
        if (glueRemark.length() < 4 || glueRemark.length() > 100) {
            return new ReturnT<>(500, "jobinfo_glue_remark_limit");
        }
        EasyJobInfo existsJobInfo = iEasyJobInfoService.getById(id);
        if (existsJobInfo == null) {
            return new ReturnT<>(500, "jobinfo_glue_jobid_unvalid");
        }

        // update new code
        existsJobInfo.setGlueSource(glueSource);
        existsJobInfo.setGlueRemark(glueRemark);
        existsJobInfo.setGlueUpdateTime(LocalDateTime.now());

        existsJobInfo.setUpdateTime(LocalDateTime.now());
        iEasyJobInfoService.updateById(existsJobInfo);

        // log old code
        EasyJobLogGlue easyJobLogGlue = new EasyJobLogGlue();
        easyJobLogGlue.setJobId(existsJobInfo.getId());
        easyJobLogGlue.setGlueType(existsJobInfo.getGlueType());
        easyJobLogGlue.setGlueSource(glueSource);
        easyJobLogGlue.setGlueRemark(glueRemark);
        iEasyJobLogGlueService.save(easyJobLogGlue);

        // remove code backup more than 30
        iEasyJobLogGlueService.removeOld(existsJobInfo.getId(), 30);

        return ReturnT.SUCCESS;
    }
}
