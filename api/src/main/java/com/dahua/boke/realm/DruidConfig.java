/*package com.dahua.boke.realm;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
 
*//**
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| -_- |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
 *            佛曰:
 *                   写字楼里写字间，写字间里程序员；
 *                   程序人员写程序，又拿程序换酒钱。
 *                   酒醒只在网上坐，酒醉还来网下眠；
 *                   酒醉酒醒日复日，网上网下年复年。
 *                   但愿老死电脑间，不愿鞠躬老板前；
 *                   奔驰宝马贵者趣，公交自行程序员。
 *                   别人笑我忒疯癫，我笑自己命太贱；
 *                   不见满街漂亮妹，哪个归得程序员？
 *//*
@Configuration
@EnableTransactionManagement
public class DruidConfig {
 
    @Value("${spring.datasource.url}")
    private String dbUrl;
 
    @Value("${spring.datasource.username}")
    private String username;
 
    @Value("${spring.datasource.password}")
    private String password;
 
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
 
    @Value("${spring.datasource.initialSize}")
    private int initialSize;
 
    @Value("${spring.datasource.minIdle}")
    private int minIdle;
 
    @Value("${spring.datasource.maxActive}")
    private int maxActive;
 
    @Value("${spring.datasource.maxWait}")
    private int maxWait;
 
    @Value("${spring.datasource.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
 
    @Value("${spring.datasource.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;
 
    @Value("${spring.datasource.validationQuery}")
    private String validationQuery;
 
    @Value("${spring.datasource.testWhileIdle}")
    private boolean testWhileIdle;
 
    @Value("${spring.datasource.testOnBorrow}")
    private boolean testOnBorrow;
 
    @Value("${spring.datasource.testOnReturn}")
    private boolean testOnReturn;
 
    @Value("${spring.datasource.filters}")
    private String filters;
 
    @Value("${spring.datasource.logSlowSql}")
    private String logSlowSql;
 
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", username);
        reg.addInitParameter("loginPassword", password);
        reg.addInitParameter("logSlowSql", logSlowSql);
        return reg;
    }
 
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }
 
    @Bean
    public DataSource druidDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
        }
        return datasource;
    }
 
 
 //第二种方式
 
    import com.alibaba.druid.pool.DruidDataSource;
    import com.alibaba.druid.util.StringUtils;
    import org.apache.ibatis.io.VFS;
    import org.apache.ibatis.session.SqlSessionFactory;
    import org.mybatis.spring.SqlSessionFactoryBean;
    import org.mybatis.spring.annotation.MapperScan;
    import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.boot.bind.RelaxedPropertyResolver;
    import org.springframework.context.ApplicationContextException;
    import org.springframework.context.EnvironmentAware;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.env.Environment;
    import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
    import org.springframework.jdbc.datasource.DataSourceTransactionManager;
    import org.springframework.transaction.PlatformTransactionManager;
    import org.springframework.transaction.annotation.EnableTransactionManagement;
     
    import java.sql.SQLException;
    import java.util.Arrays;
     
    @Configuration
    @EnableTransactionManagement
    public class DuridConfig implements EnvironmentAware {
        private static final Logger logger = LoggerFactory.getLogger(DuridConfig.class);
     
        private Environment environment;
        private RelaxedPropertyResolver propertyResolver;
     
        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
            this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
        }
     
        //注册dataSource
        @Bean(initMethod = "init", destroyMethod = "close")
        public DruidDataSource dataSource() throws SQLException {
            if (StringUtils.isEmpty(propertyResolver.getProperty("url"))) {
                System.out.println("Your database connectio n pool configuration is incorrect!"
                        + " Please check your Spring profile, current profiles are:"
                        + Arrays.toString(environment.getActiveProfiles()));
                throw new ApplicationContextException(
                        "Database connection pool is not configured correctly");
            }
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setDriverClassName(propertyResolver.getProperty("driver-class-name"));
            druidDataSource.setUrl(propertyResolver.getProperty("url"));
            druidDataSource.setUsername(propertyResolver.getProperty("username"));
            druidDataSource.setPassword(propertyResolver.getProperty("password"));
            druidDataSource.setInitialSize(Integer.parseInt(propertyResolver.getProperty("initialSize")));
            druidDataSource.setMinIdle(Integer.parseInt(propertyResolver.getProperty("minIdle")));
            druidDataSource.setMaxActive(Integer.parseInt(propertyResolver.getProperty("maxActive")));
            druidDataSource.setMaxWait(Integer.parseInt(propertyResolver.getProperty("maxWait")));
            druidDataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(propertyResolver.getProperty("timeBetweenEvictionRunsMillis")));
            druidDataSource.setMinEvictableIdleTimeMillis(Long.parseLong(propertyResolver.getProperty("minEvictableIdleTimeMillis")));
            druidDataSource.setValidationQuery(propertyResolver.getProperty("validationQuery"));
            druidDataSource.setTestWhileIdle(Boolean.parseBoolean(propertyResolver.getProperty("testWhileIdle")));
            druidDataSource.setTestOnBorrow(Boolean.parseBoolean(propertyResolver.getProperty("testOnBorrow")));
            druidDataSource.setTestOnReturn(Boolean.parseBoolean(propertyResolver.getProperty("testOnReturn")));
            druidDataSource.setPoolPreparedStatements(Boolean.parseBoolean(propertyResolver.getProperty("poolPreparedStatements")));
            druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(propertyResolver.getProperty("maxPoolPreparedStatementPerConnectionSize")));
            druidDataSource.setFilters(propertyResolver.getProperty("filters"));
            logger.info("DuridConfig-----数据源完成");
            return druidDataSource;
        }
     
        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(dataSource());
//            //mybatis分页
//            PageHelper pageHelper = new PageHelper();
//            Properties props = new Properties();
//            props.setProperty("dialect", "mysql");
//            props.setProperty("reasonable", "true");
//            props.setProperty("supportMethodsArguments", "true");
//            props.setProperty("returnPageInfo", "check");
//            props.setProperty("params", "count=countSql");
//            pageHelper.setProperties(props);
//            //添加插件
//            sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});
            //添加XML目录
            VFS.addImplClass(SpringBootVFS.class);
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
            logger.info("dao层扫描包为:mapper/*.xml");
            return sqlSessionFactoryBean.getObject();
        }
     
        @Bean
        public PlatformTransactionManager transactionManager() throws SQLException {
            return new DataSourceTransactionManager(dataSource());
     
        }
     
     
    }
 
}
*/