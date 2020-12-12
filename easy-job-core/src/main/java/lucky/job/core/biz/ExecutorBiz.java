package lucky.job.core.biz;

import lucky.job.core.model.*;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:18
 * @Description:
 */
public interface ExecutorBiz {
    /**
     * beat
     *
     * @return
     */
    ReturnT<String> beat();

    /**
     * idle beat
     *
     * @param idleBeatParam
     * @return
     */
    ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    /**
     * run
     *
     * @param triggerParam
     * @return
     */
    ReturnT<String> run(TriggerParam triggerParam);

    /**
     * kill
     *
     * @param killParam
     * @return
     */
    ReturnT<String> kill(KillParam killParam);

    /**
     * log
     *
     * @param logParam
     * @return
     */
    ReturnT<LogResult> log(LogParam logParam);
}
