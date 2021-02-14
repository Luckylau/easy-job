package lucky.job.executor.annotation;

import java.lang.annotation.*;

/**
 * @author: luckylau
 * @Date: 2021/1/12 15:37
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EasyJob {
    /**
     * jobhandler name
     */
    String value();

    /**
     * init handler, invoked when JobThread init
     */
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    String destroy() default "";
}
