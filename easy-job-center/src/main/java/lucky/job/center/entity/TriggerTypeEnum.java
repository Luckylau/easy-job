package lucky.job.center.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: luckylau
 * @Date: 2020/11/30 20:41
 * @Description:
 */
@Getter
@AllArgsConstructor
public enum TriggerTypeEnum {

    /**
     *
     */
    MANUAL("jobconf_trigger_type_manual"),
    /**
     *
     */
    CRON("jobconf_trigger_type_cron"),
    /**
     *
     */
    RETRY("jobconf_trigger_type_retry"),
    /**
     *
     */
    PARENT("jobconf_trigger_type_parent"),
    /**
     *
     */
    API("jobconf_trigger_type_api");

    private String title;
}
