package lucky.job.executor.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: luckylau
 * @Date: 2021/1/12 14:44
 * @Description:
 */
@ConfigurationProperties("spring.easyjob")
public class EasyJobProperties {

    private Center center;

    private Executor executor;

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public Executor getExecutor() {
        return executor;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public static class Executor {
        private String appname;
        private String registerAddress;
        private String ip;
        private Integer port;
        private String logPath;
        private Long logretentiondays;

        public String getAppname() {
            return appname;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }

        public String getRegisterAddress() {
            return registerAddress;
        }

        public void setRegisterAddress(String registerAddress) {
            this.registerAddress = registerAddress;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getLogPath() {
            return logPath;
        }

        public void setLogPath(String logPath) {
            this.logPath = logPath;
        }

        public Long getLogretentiondays() {
            return logretentiondays;
        }

        public void setLogretentiondays(Long logretentiondays) {
            this.logretentiondays = logretentiondays;
        }
    }

    public static class Center {
        private String addresses;

        private String accessToken;

        public String getAddresses() {
            return addresses;
        }

        public void setAddresses(String addresses) {
            this.addresses = addresses;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
