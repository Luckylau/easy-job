package lucky.job.center.config;


import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Created by luckylau on 2018/7/30
 */
@Configuration
@MapperScan("lucky.job.center.dao")
public class MybatisPlusConfig {
    @Autowired
    private HikariDataSource dataSource;

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }


    @Bean(name = "baseTransactionManager")
    @Primary
    public DataSourceTransactionManager setTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

}
