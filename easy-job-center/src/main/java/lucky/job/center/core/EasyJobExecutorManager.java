package lucky.job.center.core;

import lombok.extern.slf4j.Slf4j;
import lucky.job.core.biz.ExecutorBiz;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:17
 * @Description:
 */
@Slf4j
public class EasyJobExecutorManager {
    private static ConcurrentMap<String, ExecutorBiz> executorBizRepository = new ConcurrentHashMap<>();


    public static ExecutorBiz getExecutorBiz(String address, String accessToken) {
        // valid
        if (StringUtils.isBlank(address)) {
            return null;
        }

        // load-cache
        address = address.trim();
        ExecutorBiz executorBiz = executorBizRepository.get(address);
        if (executorBiz != null) {
            return executorBiz;
        }

        // set-cache
        executorBiz = new ExecutorBizClient(address, accessToken);
        executorBizRepository.put(address, executorBiz);

        return executorBiz;
    }


}
