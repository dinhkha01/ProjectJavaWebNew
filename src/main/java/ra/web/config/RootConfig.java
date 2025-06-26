package ra.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties") // Nguồn tài nguyên chứa các thuộc tính cấu hình
public class RootConfig {
    @Autowired
    private Environment env;
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(env.getProperty("datasource.driver","com.mysql.cj.jdbc.Driver")); // Driver của MySQL
        source.setUrl(env.getProperty("datasource.url"));
        source.setUsername(env.getProperty("datasource.username"));
        source.setPassword(env.getProperty("datasource.password"));
        return source;
    }

    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto")); // tự đong tạo CSDL cũng như các bang (DDL)
        properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect")); // cung cấp phiên bản và Hệ QTCSDL tương thích
        properties.setProperty("hibernate.show_sql",env.getProperty("hibernate.show_sql")); // hiển thị câu SQL mà hibernate thực thi
        properties.setProperty("hibernate.format_sql", env.getProperty("hibernate.format_sql")); // format câu SQL mà hibernate thực thi
        return properties;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory =
                new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("ra.web.entity"); // phat hiện các ClassMapping vs Table
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setJpaProperties(additionalProperties());
        return entityManagerFactory;
    }
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
