package lucky.job.executor.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author: luckylau
 * @Date: 2021/1/12 14:37
 * @Description:
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({EasyJobProperties.class})
public class EasyJobAutoConfig {


    @Bean
    @ConditionalOnMissingBean(EasyJobSpringExecutor.class)
    public EasyJobSpringExecutor easyJobSpringExecutor(EasyJobProperties easyJobProperties) {
        EasyJobSpringExecutor easyJobSpringExecutor = new EasyJobSpringExecutor();
        easyJobSpringExecutor.setRegisterAddress(easyJobProperties.getExecutor().getRegisterAddress());
        easyJobSpringExecutor.setAppname(easyJobProperties.getExecutor().getAppname());
        easyJobSpringExecutor.setCenterAccessToken(easyJobProperties.getCenter().getAccessToken());
        easyJobSpringExecutor.setCenterAddresses(easyJobProperties.getCenter().getAddresses());
        easyJobSpringExecutor.setIp(easyJobProperties.getExecutor().getIp());
        easyJobSpringExecutor.setPort(easyJobProperties.getExecutor().getPort());
        easyJobSpringExecutor.setLogPath(easyJobProperties.getExecutor().getLogPath());
        easyJobSpringExecutor.setLogRetentionDays(easyJobProperties.getExecutor().getLogretentiondays());
        return easyJobSpringExecutor;

    }

}
