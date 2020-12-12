package lucky.job.center.core.route;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: luckylau
 * @Date: 2020/12/1 14:16
 * @Description:
 */
@AllArgsConstructor
@Getter
public enum ExecutorBlockStrategyEnum {

    /**
     *
     */
    SERIAL_EXECUTION("Serial execution"),
    /**
     * CONCURRENT_EXECUTION("并行"),
     * *
     */
    DISCARD_LATER("Discard Later"),
    COVER_EARLY("Cover Early");

    private String title;

    public static ExecutorBlockStrategyEnum match(String name, ExecutorBlockStrategyEnum defaultItem) {
        if (name != null) {
            for (ExecutorBlockStrategyEnum item : ExecutorBlockStrategyEnum.values()) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
        }
        return defaultItem;
    }
}
