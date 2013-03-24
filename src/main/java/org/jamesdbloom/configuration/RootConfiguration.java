package org.jamesdbloom.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * This configuration contains top level beans and any configuration required by filters (as WebMvcConfiguration only loaded within Dispatcher Servlet)
 *
 * @author jamesdbloom
 */
@Configuration
@PropertySource("classpath:web.properties")
@ImportResource(value = {"classpath:/config/security-context.xml"})
public class RootConfiguration {
    @Resource
    private Environment environment;

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

}
