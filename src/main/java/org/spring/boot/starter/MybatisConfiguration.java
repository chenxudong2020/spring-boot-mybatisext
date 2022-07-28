package org.spring.boot.starter;

import com.ext.mybatisext.ExtObjectWrapperFactory;
import com.ext.mybatisext.activerecord.spring.SpringSupportBean;
import com.ext.mybatisext.interceptor.CommonMapperInterceptor;
import com.ext.mybatisext.interceptor.GenericMapperInterceptor;
import com.ext.mybatisext.interceptor.MyBatisInterceptor;
import com.ext.mybatisext.plugin.BatchPlugin;
import com.ext.mybatisext.plugin.IdentityPlugin;
import com.ext.mybatisext.plugin.IndexingPlugin;
import com.ext.mybatisext.plugin.SQLPrintPlugin;
import com.ext.mybatisext.plugin.paging.PagingPlugin;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author 87260
 */
@Configuration
@ConditionalOnBean({DataSource.class})
public class MybatisConfiguration {


    @Autowired
    DataSource dataSource;


    /**
     * 定义拦截器处理
     * @return
     */
    @Bean
    public MyBatisInterceptor[] getInterceptors(){
        return new MyBatisInterceptor[]{
                new CommonMapperInterceptor(),
                new GenericMapperInterceptor()};
    }



    /**
     *  加载自定义插件,设置Map值的key为驼峰处理
     * @return
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return configuration -> {
            //TODO 加载自定义插件
            configuration.addInterceptor(new SQLPrintPlugin());
            configuration.addInterceptor(new IndexingPlugin());
            configuration.addInterceptor(new PagingPlugin());
            configuration.addInterceptor(new IdentityPlugin());
            configuration.addInterceptor(new BatchPlugin());
            //TODO 设置Map值的key为驼峰处理
            //configuration.setObjectWrapperFactory(new ExtObjectWrapperFactory());
        };
    }





    @Bean
    public SpringSupportBean getSpringSupportBean(){
        SpringSupportBean springSupportBean= new SpringSupportBean();
        springSupportBean.setDataSource(dataSource);
        return springSupportBean;
    }

}
