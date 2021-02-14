package lucky.job.core.model;

import java.io.Serializable;

public class IdleBeatParam implements Serializable {

    private int jobId;


    public IdleBeatParam(int jobId) {
        this.jobId = jobId;
    }

    public IdleBeatParam() {
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "IdleBeatParam{" +
                "jobId=" + jobId +
                '}';
    }
}