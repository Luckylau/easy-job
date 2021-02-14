package lucky.job.center.core.route.strategy;

import lombok.extern.slf4j.Slf4j;
import lucky.job.center.core.route.ExecutorRouter;
import lucky.job.core.model.ReturnT;
import lucky.job.core.model.TriggerParam;

import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/11/30 21:19
 * @Description:
 */
@Slf4j
public class ExecutorRouteBusyOver implements ExecutorRouter {


    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return null;
    }
}
