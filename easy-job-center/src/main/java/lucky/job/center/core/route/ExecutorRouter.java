package lucky.job.center.core.route;

import lucky.job.core.model.ReturnT;
import lucky.job.core.model.TriggerParam;

import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/11/30 21:17
 * @Description:
 */
public interface ExecutorRouter {

    ReturnT<String> route(TriggerParam triggerParam, List<String> addressList);
}
