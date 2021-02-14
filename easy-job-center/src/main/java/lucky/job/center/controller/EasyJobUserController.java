package lucky.job.center.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobUser;
import lucky.job.center.service.IEasyJobGroupService;
import lucky.job.center.service.IEasyJobUserService;
import lucky.job.center.service.LoginService;
import lucky.job.center.web.annotation.PermissionLimit;
import lucky.job.core.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/user")
public class EasyJobUserController {
    @Resource
    private IEasyJobUserService easyJobUserService;
    @Resource
    private IEasyJobGroupService easyJobGroupService;

    @RequestMapping
    @PermissionLimit(admin = true)
    public String index(Model model) {

        // 执行器列表
        List<EasyJobGroup> groupList = easyJobGroupService.list();
        model.addAttribute("groupList", groupList);

        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(admin = true)
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String username, int role) {

        // page list
        List<EasyJobUser> list = easyJobUserService.pageList(start, length, username, role);
        int listCount = easyJobUserService.pageListCount(start, length, username, role);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        // 总记录数
        maps.put("recordsTotal", listCount);
        // 过滤后的总记录数
        maps.put("recordsFiltered", listCount);
        // 分页列表
        maps.put("data", list);
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(admin = true)
    public ReturnT<String> add(EasyJobUser easyJobUser) {

        // valid username
        if (!StringUtils.hasText(easyJobUser.getUsername())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "system_please_input user_username");
        }
        easyJobUser.setUsername(easyJobUser.getUsername().trim());
        if (!(easyJobUser.getUsername().length() >= 4 && easyJobUser.getUsername().length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "system_lengh_limit [4-20]");
        }
        // valid password
        if (!StringUtils.hasText(easyJobUser.getPassword())) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "system_please_input user_password");
        }
        easyJobUser.setPassword(easyJobUser.getPassword().trim());
        if (!(easyJobUser.getPassword().length() >= 4 && easyJobUser.getPassword().length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "system_lengh_limit [4-20]");
        }
        // md5 password
        easyJobUser.setPassword(DigestUtils.md5DigestAsHex(easyJobUser.getPassword().getBytes()));

        // check repeat
        EasyJobUser existUser = easyJobUserService.getOne(new QueryWrapper<EasyJobUser>().eq("username", easyJobUser.getUsername()));
        if (existUser != null) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "user_username_repeat");
        }

        // write
        easyJobUserService.save(easyJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(admin = true)
    public ReturnT<String> update(HttpServletRequest request, EasyJobUser easyJobUser) {

        // avoid opt login seft
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getUsername().equals(easyJobUser.getUsername())) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), "user_update_loginuser_limit");
        }

        // valid password
        if (StringUtils.hasText(easyJobUser.getPassword())) {
            easyJobUser.setPassword(easyJobUser.getPassword().trim());
            if (!(easyJobUser.getPassword().length() >= 4 && easyJobUser.getPassword().length() <= 20)) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "system_lengh_limit [4-20]");
            }
            // md5 password
            easyJobUser.setPassword(DigestUtils.md5DigestAsHex(easyJobUser.getPassword().getBytes()));
        } else {
            easyJobUser.setPassword(null);
        }

        // write
        easyJobUser.setId(loginUser.getId());
        easyJobUserService.updateById(easyJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(admin = true)
    public ReturnT<String> remove(HttpServletRequest request, int id) {

        // avoid opt login seft
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);
        if (loginUser.getId() == id) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), "user_update_loginuser_limit");
        }

        easyJobUserService.removeById(id);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(HttpServletRequest request, String password) {

        // valid password
        if (password == null || password.trim().length() == 0) {
            return new ReturnT<>(ReturnT.FAIL.getCode(), "密码不可为空");
        }
        password = password.trim();
        if (!(password.length() >= 4 && password.length() <= 20)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "system_lengh_limit [4-20]");
        }

        // md5 password
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // update pwd
        EasyJobUser loginUser = (EasyJobUser) request.getAttribute(LoginService.LOGIN_IDENTITY_KEY);

        // do write
        EasyJobUser existUser = easyJobUserService.getOne(new QueryWrapper<EasyJobUser>().eq("username", loginUser.getUsername()));
        existUser.setPassword(md5Password);
        easyJobUserService.updateById(existUser);

        return ReturnT.SUCCESS;
    }
}
