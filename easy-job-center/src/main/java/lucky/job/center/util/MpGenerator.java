package lucky.job.center.util;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * Created by luckylau on 2018/7/31
 * 生成文件后的文件需要修改：
 * 将controller层的@contoller改为@RestController
 */
public class MpGenerator {

    public static void main(String[] args) {
        //起到保护作用，执行前请注销
//        if(true) throw new Exception("代码生成很危险，请确定配置后注销该行，再执行代码生成！！！");

        AutoGenerator autoGenerator = new AutoGenerator();
        // 选择 freemarker 引擎，默认 Velocity
        autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //配置作者信息
        globalConfig.setAuthor("luckylau");
        //输出的文件位置
        globalConfig.setOutputDir("E:\\easy-job\\easy-job-center\\src\\main\\java");
        //是否覆盖同名字的文件
        globalConfig.setFileOverride(true);
        //是否使用activeRecord特性
        globalConfig.setActiveRecord(true);
        //是否开启mybatis的二级缓存
        globalConfig.setEnableCache(false);
        //xml文件中的Base_Column_List是否生成
        globalConfig.setBaseColumnList(true);
        //xml文件中的resultMap是否生成
        globalConfig.setBaseResultMap(true);


        //自定义Mapper名字，默认是%sMapper
        //自定义服务的名字，默认是I%sService
        //自定义控制层的名字，默认是%sController

       /* globalConfig.setMapperName("%sDao");
        globalConfig.setXmlName("%sDao");
        globalConfig.setServiceImplName("Mp%sServiceImpl");
        globalConfig.setServiceName("Mp%sService");
        globalConfig.setControllerName("%sAction");*/

        autoGenerator.setGlobalConfig(globalConfig);


        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);
        //用于自定义数据库表字段类型转换，mysql数据库没有自定义数据类型，oracle数据库有
        /*dataSourceConfig.setTypeConvert(new MySqlTypeConvert(){
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                return super.processTypeConvert(fieldType);
            }
        });*/
        dataSourceConfig.setDriverName("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("clpf1p@MBWc");
        dataSourceConfig.setUrl("jdbc:mysql://10.12.6.20:3306/easyJob?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false");
        autoGenerator.setDataSource(dataSourceConfig);

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        /**
         * 设置表前缀，这样生成的类不会带该前缀
         * 例如，表为tb_user_result,如果加这个配置，生成为UserResultMapper，
         * 否则TbUserResultMapper；
         */
        //针对下划线转驼峰模式,还有一种是不处理
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        //指定要处理的数据库表
        strategyConfig.setInclude(new String[]{
                "easy_job_info", "easy_job_log", "easy_job_log_report", "easy_job_log_glue", "easy_job_registry", "easy_job_group", "easy_job_user", "easy_job_lock"});
        autoGenerator.setStrategy(strategyConfig);

        //包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("lucky.job.center");
        //会在一个包的下面创建entity，mapper，service, web
        //packageConfig.setModuleName("test");
        //指定controller层的名称，默认是web
        packageConfig.setController("controller");
        //指定entity层的名字，默认是entity
        //packageConfig.setEntity("model");
        //指定mapper层的名字，默认是mapper
        packageConfig.setMapper("dao");
        //指定xml层的名字，默认是在mapper/xml
        packageConfig.setXml("dao/mapper");
        autoGenerator.setPackageInfo(packageConfig);

        autoGenerator.execute();

    }
}
