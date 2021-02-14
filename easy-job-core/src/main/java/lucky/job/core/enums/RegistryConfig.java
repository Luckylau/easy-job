package lucky.job.core.enums;

/**
 * @author: luckylau
 * @Date: 2020/12/17 11:20
 * @Description:
 */
public class RegistryConfig {

    public static final int BEAT_TIMEOUT = 30;
    public static final int DEAD_TIMEOUT = BEAT_TIMEOUT * 3;

    public enum RegistryType {EXECUTOR, ADMIN}
}
