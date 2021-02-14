package lucky.job.core.handler.impl;

import lucky.job.core.handler.IJobHandler;
import lucky.job.core.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: luckylau
 * @Date: 2020/12/28 10:51
 * @Description:
 */
public class GlueJobHandler extends IJobHandler {

    private static Logger logger = LoggerFactory.getLogger(GlueJobHandler.class);

    private long glueUpdatetime;
    private IJobHandler jobHandler;

    public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
        this.jobHandler = jobHandler;
        this.glueUpdatetime = glueUpdatetime;
    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("GlueJobHandler execute :{}", param);
        return jobHandler.execute(param);
    }
}
