package lucky.job.core.biz;

import lucky.job.core.model.HandleCallbackParam;
import lucky.job.core.model.RegistryParam;
import lucky.job.core.model.ReturnT;

import java.util.List;

/**
 * @author: luckylau
 * @Date: 2020/12/15 16:43
 * @Description:
 */
public interface AdminBiz {

    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);


    // ---------------------- registry ----------------------

    /**
     * registry
     *
     * @param registryParam
     * @return
     */
    ReturnT<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    ReturnT<String> registryRemove(RegistryParam registryParam);


    // ---------------------- biz (custome) ----------------------
    // group、job ... manage
}
