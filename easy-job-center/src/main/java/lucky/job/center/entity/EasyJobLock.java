package lucky.job.center.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author luckylau
 * @since 2020-11-25
 */
public class EasyJobLock extends Model<EasyJobLock> {

    private static final long serialVersionUID = 1L;

    /**
     * 锁名称
     */
    private String lockName;

    private String address;

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    protected Serializable pkVal() {
        return this.lockName;
    }

    @Override
    public String toString() {
        return "EasyJobLock{" +
                "lockName=" + lockName + "address=" + address +
                "}";
    }
}
