package com.tuling.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.tuling.mybatis.entity.User;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author admin
 */
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Configuration
@MapperScan(basePackages = {"com.tuling.mybatis.mapper"})/*配置 mapper 接口包的路径*/
@ComponentScan(basePackages = {"com.tuling.mybatis"}) /*配置 spring ioc 扫描包路径 */
@Repository
public class MyBatisConfig {

	/**
	 * <bean class="org.mybatis.spring.SqlSessionFactoryBean">
	 * 该类作为 mybatis 配置的入口，在初始化时 调用 afterPropertiesSet() 将设置的属性注入 Configuration 对象（相当于提供了一个代码设置主配置文件的属性的一个入口，设置的属性的就将属性信息读取到Configuration对象中，最终还是要去调用mybatis源码解析主配置文件、mapper xml 文件。SqlSessionFactoryBean 作为Spring 整合 mybatis 的一个入口），并创建 xmlConfigBuilder 解析 mybatis 的组配置文件，XmlMappedBuilder 解析 mapper xml 文件，
	 * <p>
	 * <p>
	 * public Class<? extends SqlSessionFactory> getObjectType() {
	 * return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
	 * }
	 * <p>
	 * 重写的了 getObjectType 方法，会直接返回 SqlSessionFactory 类型，所以在 Spring 整合 mybatis 是需要根据类型注入 SqlSessionFactory 是，会直接从IOC容器中获取到当前的 sqlSessionFactory 进行注入
	 *
	 * @return
	 * @throws IOException
	 */
	@Bean
	public SqlSessionFactoryBean sqlSessionFactory() throws IOException {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		/**
		 * 这里的属性赋值都会在 CreateBean 反射创建的时候的进行赋值
		 */
		factoryBean.setDataSource(dataSource());
		// 设置 MyBatis 配置文件路径，自定义配置的 主配置文件路径
		factoryBean.setConfigLocation(new ClassPathResource("mybatis/mybatis-config.xml"));
		// 设置 SQL 映射文件路径
		factoryBean.setMapperLocations(new
				PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/*.xml"));
		factoryBean.setTypeAliases(User.class);

		return factoryBean;
	}

	/**
	 * 配置自定义的 DataSource
	 *
	 * @return
	 */
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUsername("root");
		dataSource.setPassword("root1234");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/im?characterEncoding=UTF-8");
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}


}
