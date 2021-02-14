package lucky.job.center.controller;

import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.service.LoginService;
import lucky.job.core.exception.EasyJobException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/12/15 20:14
 * @Description:
 */
public class BaseController {
    public static void validPermission(HttpServletRequest request, int jobGroup) {
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (!loginUser.validPermission(jobGroup)) {
            throw new EasyJobException("system_permission_limit" + "[username=" + loginUser.getUsername() + "]");
        }
    }

    public static List<EasyJobGroup> filterJobGroupByRole(HttpServletRequest request, List<EasyJobGroup> jobGroupListAll) {
        List<EasyJobGroup> jobGroupList = new ArrayList<>();
        if (jobGroupListAll == null || jobGroupListAll.isEmpty()) {
            return jobGroupList;
        }
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getRole() == 1) {
            jobGroupList = jobGroupListAll;
        } else {
            List<String> groupIdStrs = new ArrayList<>();
            if (loginUser.getPermission() != null && loginUser.getPermission().trim().length() > 0) {
                groupIdStrs = Arrays.asList(loginUser.getPermission().trim().split(","));
            }
            for (EasyJobGroup groupItem : jobGroupListAll) {
                if (groupIdStrs.contains(String.valueOf(groupItem.getId()))) {
                    jobGroupList.add(groupItem);
                }
            }
        }
        return jobGroupList;
    }
}
