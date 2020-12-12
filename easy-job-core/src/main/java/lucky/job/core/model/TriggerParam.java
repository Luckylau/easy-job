package lucky.job.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:31
 * @Description:
 */
@Data
public class TriggerParam implements Serializable {

    private int jobId;

    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    private long logId;
    private long logDateTime;

    private String glueType;
    private String glueSource;
    private long glueUpdateTime;

    private int broadcastIndex;
    private int broadcastTotal;
}
