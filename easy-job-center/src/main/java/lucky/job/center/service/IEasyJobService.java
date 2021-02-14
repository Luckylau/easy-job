package lucky.job.center.service;

import lucky.job.center.entity.EasyJobInfo;
import lucky.job.core.model.ReturnT;

import java.util.Date;
import java.util.Map;

/**
 * @author: luckylau
 * @Date: 2020/12/15 16:21
 * @Description:
 */
public interface IEasyJobService {
    /**
     * page list
     *
     * @param start
     * @param length
     * @param jobGroup
     * @param jobDesc
     * @param executorHandler
     * @param author
     * @return
     */
    Map<String, Object> pageList(int start, int length, int jobGroup, int triggerStatus, String jobDesc, String executorHandler, String author);

    /**
     * add job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> add(EasyJobInfo jobInfo);

    /**
     * update job
     *
     * @param jobInfo
     * @return
     */
    ReturnT<String> update(EasyJobInfo jobInfo);

    /**
     * remove job
     * *
     *
     * @param id
     * @return
     */
    ReturnT<String> remove(int id);

    /**
     * start job
     *
     * @param id
     * @return
     */
    ReturnT<String> start(int id);

    /**
     * stop job
     *
     * @param id
     * @return
     */
    ReturnT<String> stop(int id);

    /**
     * dashboard info
     *
     * @return
     */
    Map<String, Object> dashboardInfo();

    /**
     * chart info
     *
     * @param startDate
     * @param endDate
     * @return
     */
    ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate);
}
