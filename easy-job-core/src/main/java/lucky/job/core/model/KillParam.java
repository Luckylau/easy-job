package lucky.job.core.model;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:32
 * @Description:
 */
public class KillParam implements Serializable {
    private int jobId;

    public KillParam(int jobId) {
        this.jobId = jobId;
    }

    public KillParam() {
    }

    @Override
    public String toString() {
        return "KillParam{" +
                "jobId=" + jobId +
                '}';
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
