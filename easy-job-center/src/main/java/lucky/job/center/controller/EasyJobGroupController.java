package lucky.job.center.controller;


import lucky.job.center.entity.EasyJobGroup;
import lucky.job.center.entity.EasyJobRegistry;
import lucky.job.center.service.IEasyJobGroupService;
import lucky.job.center.service.IEasyJobInfoService;
import lucky.job.center.service.IEasyJobRegistryService;
import lucky.job.core.enums.RegistryConfig;
import lucky.job.core.exception.EasyJobException;
import lucky.job.core.model.ReturnT;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
@Controller
@RequestMapping("/jobgroup")
public class EasyJobGroupController {

    @Resource
    public IEasyJobInfoService easyJobInfoService;
    @Resource
    public IEasyJobGroupService easyJobGroupService;
    @Resource
    private IEasyJobRegistryService easyJobRegistryService;

    @RequestMapping
    public String index(Model model) {
        return "jobgroup/jobgroup.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        String appname, String title) {

        // page query
        List<EasyJobGroup> list = easyJobGroupService.pageList(start, length, appname, title);
        int listCount = easyJobGroupService.pageListCount(start, length, appname, title);

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

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(EasyJobGroup easyJobGroup) {

        checkParam(easyJobGroup);

        if (easyJobGroup.getAddressType() != 0) {
            if (StringUtils.isBlank(easyJobGroup.getAddressList())) {
                return new ReturnT<>(500, "jobgroup_field_addressType_limit");
            }
            String[] addresss = easyJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (item == null || item.trim().length() == 0) {
                    return new ReturnT<>(500, "jobgroup_field_registryList_unvalid");
                }
            }
        }
        return easyJobGroupService.save(easyJobGroup) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/update")
    @ResponseBody
    public ReturnT<String> update(EasyJobGroup easyJobGroup) {

        checkParam(easyJobGroup);

        if (easyJobGroup.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(easyJobGroup.getAppName());
            String addressListStr = null;
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                addressListStr = "";
                for (String item : registryList) {
                    addressListStr += item + ",";
                }
                addressListStr = addressListStr.substring(0, addressListStr.length() - 1);
            }
            easyJobGroup.setAddressList(addressListStr);
        } else {
            // 1=手动录入
            String[] addresss = easyJobGroup.getAddressList().split(",");
            for (String item : addresss) {
                if (StringUtils.isBlank(item)) {
                    return new ReturnT<>(500, "jobgroup_field_registryList_unvalid");
                }
            }
        }
        return easyJobGroup.updateById() ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<>();
        List<EasyJobRegistry> list = easyJobRegistryService.findAll(RegistryConfig.DEAD_TIMEOUT, new Date());
        if (list != null) {
            for (EasyJobRegistry item : list) {
                if (RegistryConfig.RegistryType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    @RequestMapping("/remove")
    @ResponseBody
    public ReturnT<String> remove(int id) {

        // valid
        int count = easyJobInfoService.pageListCount(0, 10, id, -1, null, null, null);
        if (count > 0) {
            return new ReturnT<String>(500, "jobgroup_del_limit_0");
        }

        List<EasyJobGroup> allList = easyJobGroupService.list();
        if (allList.size() == 1) {
            return new ReturnT<String>(500, "jobgroup_del_limit_1");
        }

        return easyJobGroupService.removeById(id) ? ReturnT.SUCCESS : ReturnT.FAIL;
    }

    @RequestMapping("/loadById")
    @ResponseBody
    public ReturnT<EasyJobGroup> loadById(int id) {
        EasyJobGroup jobGroup = easyJobGroupService.getById(id);
        return jobGroup != null ? new ReturnT<>(jobGroup) : new ReturnT<>(ReturnT.FAIL_CODE, null);
    }

    private void checkParam(EasyJobGroup easyJobGroup) {
        // valid
        if (StringUtils.isBlank(easyJobGroup.getAppName())) {
            throw new EasyJobException("system_please_input AppName");
        }
        if (easyJobGroup.getAppName().length() < 4 || easyJobGroup.getAppName().length() > 64) {
            throw new EasyJobException("jobgroup_field_appname_length");
        }
        if (StringUtils.isBlank(easyJobGroup.getTitle())) {
            throw new EasyJobException("system_please_input jobgroup_field_title");
        }
    }
}
