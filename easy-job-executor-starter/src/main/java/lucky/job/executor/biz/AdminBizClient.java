package lucky.job.executor.biz;

import lucky.job.core.biz.AdminBiz;
import lucky.job.core.model.HandleCallbackParam;
import lucky.job.core.model.RegistryParam;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.GsonTool;
import lucky.job.core.util.OkHttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lucky.job.core.constant.Constants.EASY_JOB_ACCESS_TOKEN;

/**
 * @author: luckylau
 * @Date: 2021/1/5 17:03
 * @Description:
 */
public class AdminBizClient implements AdminBiz {

    private String addressUrl;
    private String accessToken;

    public AdminBizClient() {
    }

    public AdminBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    }

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String response = OkHttpUtil.postJson(addressUrl + "api/callback", headMap, GsonTool.toJson(callbackParamList));
        return GsonTool.fromJson(response, ReturnT.class);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String response = OkHttpUtil.postJson(addressUrl + "api/registry", headMap, GsonTool.toJson(registryParam));
        return GsonTool.fromJson(response, ReturnT.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        Map<String, String> headMap = new HashMap<>();
        headMap.put(EASY_JOB_ACCESS_TOKEN, accessToken);
        String response = OkHttpUtil.postJson(addressUrl + "api/registryRemove", headMap, GsonTool.toJson(registryParam));
        return GsonTool.fromJson(response, ReturnT.class);
    }
}
