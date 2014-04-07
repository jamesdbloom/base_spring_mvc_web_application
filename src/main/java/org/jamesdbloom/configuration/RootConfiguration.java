package org.jamesdbloom.configuration;

import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.email.EmailConfiguration;
import org.jamesdbloom.security.SecurityConfig;
import org.jamesdbloom.uuid.UUIDFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;

import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This configuration contains top level beans and any configuration required by filters (as WebMvcConfiguration only loaded within Dispatcher Servlet)
 *
 * @author jamesdbloom
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = {"org.jamesdbloom.dao"})
@Import(value = {SecurityConfig.class, EmailConfiguration.class})
@PropertySource({"classpath:web.properties", "classpath:validation.properties"})
public class RootConfiguration {
    @Resource
    private Environment environment;

    @Bean
    protected UUIDFactory uuidFactory() {
        return new UUIDFactory();
    }

    @Bean
    protected UserDAO userDAO(){
        return new UserDAO();
    }

    // this bean is in this ApplicationContext so that it can be used in DelegatingFilterProxy
    @Bean
    public WroManagerFactory wroManagerFactory() {
        ConfigurableWroManagerFactory wroManagerFactory = new ConfigurableWroManagerFactory();

        wroManagerFactory.setConfigProperties(new Properties() {{
            setProperty("debug", environment.getProperty("bundling.enabled"));
            setProperty("preProcessors", "cssImport,semicolonAppender,conformColors");
            setProperty("postProcessors", "yuiCssMin,googleClosureAdvanced");
            setProperty("cacheGzippedContent", "true");
            setProperty("hashStrategy", "MD5"); // should drive the naming strategy to fingerprint resource urls - NOT YET WORKING / CONFIGURED CORRECTLY
            setProperty("namingStrategy", "hashEncoder-CRC32"); // should drive the naming strategy to fingerprint resource urls - NOT YET WORKING / CONFIGURED CORRECTLY
        }});

        return wroManagerFactory;
    }


    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor() {{
            setCorePoolSize(15);
            setMaxPoolSize(25);
            setQueueCapacity(50);
            setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }};
    }

}
