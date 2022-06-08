package org.spring.boot.starter;

import com.ext.mybatisext.MapperRegistryExt;
import com.ext.mybatisext.SqlSessionFactoryBeanExt;
import com.ext.mybatisext.helper.ArrayTypeHandlerExt;
import com.ext.mybatisext.helper.SmartDate;
import com.ext.mybatisext.helper.SmartDateTypeHandler;
import com.ext.mybatisext.helper.SqlSessionFactoryHolder;
import com.ext.mybatisext.interceptor.CommonMapperInterceptor;
import com.ext.mybatisext.interceptor.GenericMapperInterceptor;
import com.ext.mybatisext.interceptor.MyBatisInterceptor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
/**
 * @author 87260
 */
@SuppressWarnings("all")
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBeanExt.class,SqlSessionFactoryBean.class})
@ConditionalOnBean({DataSource.class})
@EnableConfigurationProperties({MybatisProperties.class})
@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@Import(MybatisConfiguration.class)
public class ExtMybatisAutoConfiguration extends MybatisAutoConfiguration {
    private static final Log logger = LogFactory.getLog(ExtMybatisAutoConfiguration.class);

    private  MybatisProperties properties;
    private  Interceptor[] interceptors;
    private  ResourceLoader resourceLoader;
    private  DatabaseIdProvider databaseIdProvider;
    private  List<ConfigurationCustomizer> configurationCustomizers;


    @Autowired
    private MyBatisInterceptor[] myBatisInterceptors;



    public ExtMybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider, ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider, ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        super(properties, interceptorsProvider, resourceLoader, databaseIdProvider, configurationCustomizersProvider);
    }





    private void setValue( String name, Object value ) {
        try {
            Field field = SqlSessionFactoryBean.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(this, value);
        } catch ( Exception e ) {
            logger.error(e.getMessage(), e);
        }
    }


    @Bean
    @Override
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactory factory=super.sqlSessionFactory(dataSource);
        if (factory == null ) {
            factory = super.sqlSessionFactory(dataSource);
            setValue("sqlSessionFactory", factory);
            SqlSessionFactoryHolder.setSqlSessionFactory(factory);
        }
        try {
            org.apache.ibatis.session.Configuration config = factory.getConfiguration();
            Class<?> classConfig = org.apache.ibatis.session.Configuration.class;
            // 拦截
            Field field = classConfig.getDeclaredField("mapperRegistry");
            field.setAccessible(true);
            field.set(config, new MapperRegistryExt(config, myBatisInterceptors));
            // 日期格式处理
            config.getTypeHandlerRegistry().register(java.util.Date.class, SmartDateTypeHandler.class);
            config.getTypeHandlerRegistry().register(java.sql.Date.class, SmartDateTypeHandler.class);
            config.getTypeHandlerRegistry().register(SmartDate.class, SmartDateTypeHandler.class);
            config.getTypeHandlerRegistry().register(String[].class, ArrayTypeHandlerExt.class);
            config.getTypeHandlerRegistry().register(Integer[].class, ArrayTypeHandlerExt.class);
            config.getTypeHandlerRegistry().register(BigDecimal[].class, ArrayTypeHandlerExt.class);
            config.getTypeHandlerRegistry().register(Double[].class, ArrayTypeHandlerExt.class);
            config.getTypeHandlerRegistry().register(Float[].class, ArrayTypeHandlerExt.class);
            config.getTypeHandlerRegistry().register(Long[].class, ArrayTypeHandlerExt.class);

        } catch ( Exception e ) {
            logger.error(e.getMessage(), e);
        }
        return factory;
    }


}
