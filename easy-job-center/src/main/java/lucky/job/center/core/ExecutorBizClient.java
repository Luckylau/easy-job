package lucky.job.center.core;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lucky.job.core.biz.ExecutorBiz;
import lucky.job.core.model.*;
import lucky.job.core.util.GsonTool;
import lucky.job.core.util.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

import static lucky.job.core.constant.Constants.EASY_JOB_ACCESS_TOKEN;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:39
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorBizClient implements ExecutorBiz {
    private String addressUrl;
    private String accessToken;

    @Override
    public ReturnT<String> beat() {
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String resultJson = OkHttpUtil.postJson(addressUrl + "beat", headerMap, null);
        return GsonTool.fromJson(resultJson, ReturnT.class, String.class);
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String resultJson = OkHttpUtil.postJson(addressUrl + "idleBeat", headerMap, GsonTool.toJson(idleBeatParam));
        return GsonTool.fromJson(resultJson, ReturnT.class, String.class);
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String resultJson = OkHttpUtil.postJson(addressUrl + "run", headerMap, GsonTool.toJson(triggerParam));
        return GsonTool.fromJson(resultJson, ReturnT.class, String.class);
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String resultJson = OkHttpUtil.postJson(addressUrl + "kill", headerMap, GsonTool.toJson(killParam));
        return GsonTool.fromJson(resultJson, ReturnT.class, String.class);
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String resultJson = OkHttpUtil.postJson(addressUrl + "log", headerMap, GsonTool.toJson(logParam));
        return GsonTool.fromJson(resultJson, ReturnT.class, LogResult.class);
    }
}
