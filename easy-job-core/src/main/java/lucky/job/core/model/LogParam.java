package lucky.job.core.model;

import java.io.Serializable;

/**
 * @author: luckylau
 * @Date: 2020/11/27 15:34
 * @Description:
 */
public class LogParam implements Serializable {

    private long logDateTim;
    private long logId;
    private int fromLineNum;

    public LogParam(long logDateTim, long logId, int fromLineNum) {
        this.logDateTim = logDateTim;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    public LogParam() {
    }

    public long getLogDateTim() {
        return logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

    @Override
    public String toString() {
        return "LogParam{" +
                "logDateTim=" + logDateTim +
                ", logId=" + logId +
                ", fromLineNum=" + fromLineNum +
                '}';
    }
}
