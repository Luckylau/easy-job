package lucky.job.center.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public class EasyJobLogReport extends Model<EasyJobLogReport> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 调度-时间
     */
    private LocalDateTime triggerDay;

    /**
     * 运行中-日志数量
     */
    private Integer runningCount;

    /**
     * 执行成功-日志数量
     */
    private Integer sucCount;

    /**
     * 执行失败-日志数量
     */
    private Integer failCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getTriggerDay() {
        return triggerDay;
    }

    public void setTriggerDay(LocalDateTime triggerDay) {
        this.triggerDay = triggerDay;
    }

    public Integer getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(Integer runningCount) {
        this.runningCount = runningCount;
    }

    public Integer getSucCount() {
        return sucCount;
    }

    public void setSucCount(Integer sucCount) {
        this.sucCount = sucCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EasyJobLogReport{" +
                "id=" + id +
                ", triggerDay=" + triggerDay +
                ", runningCount=" + runningCount +
                ", sucCount=" + sucCount +
                ", failCount=" + failCount +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                "}";
    }
}
