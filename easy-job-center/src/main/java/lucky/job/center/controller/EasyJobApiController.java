package lucky.job.center.controller;

import lucky.job.center.web.annotation.PermissionLimit;
import lucky.job.core.biz.AdminBiz;
import lucky.job.core.constant.Constants;
import lucky.job.core.model.HandleCallbackParam;
import lucky.job.core.model.RegistryParam;
import lucky.job.core.model.ReturnT;
import lucky.job.core.util.GsonTool;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/12/15 16:40
 * @Description:
 */
@Controller
@RequestMapping("/api")
public class EasyJobApiController {

    @Autowired
    private AdminBiz adminBiz;

    @Value("${easy.job.accessToken}")
    private String accessToken;

    /**
     * api
     *
     * @param uri
     * @param data
     * @return
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (StringUtils.isBlank(uri)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (accessToken.equals(request.getHeader(Constants.EASY_JOB_ACCESS_TOKEN))) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        if ("callback".equals(uri.trim().toLowerCase())) {
            List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
            return adminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri.trim().toLowerCase())) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registry(registryParam);
        } else if ("registryRemove".equals(uri.trim().toLowerCase())) {
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return adminBiz.registryRemove(registryParam);
        } else {
            return new ReturnT<>(ReturnT.FAIL_CODE, "invalid request, uri-mapping(" + uri + ") not found.");
        }

    }
}
