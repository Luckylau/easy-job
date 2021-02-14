package lucky.job.center.controller;

import lucky.job.center.service.IEasyJobService;
import lucky.job.center.service.LoginService;
import lucky.job.center.web.annotation.PermissionLimit;
import lucky.job.core.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * @author: luckylau
 * @Date: 2020/12/11 16:27
 * @Description:
 */
@Controller
public class IndexController {
    @Autowired
    private IEasyJobService easyJobService;
    @Autowired
    private LoginService loginService;


    @RequestMapping("/")
    public String index(Model model) {

        Map<String, Object> dashboardMap = easyJobService.dashboardInfo();
        model.addAllAttributes(dashboardMap);

        return "index";
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
        return easyJobService.chartInfo(startDate, endDate);
    }


    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        boolean ifRem = "on".equals(ifRemember);
        return loginService.login(request, response, userName, password, ifRem);
    }


    @RequestMapping("/toLogin")
    @PermissionLimit(limit = false)
    public String toLogin(HttpServletRequest request, HttpServletResponse response) {
        if (loginService.ifLogin(request, response) != null) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        return loginService.logout(request, response);
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }


}
