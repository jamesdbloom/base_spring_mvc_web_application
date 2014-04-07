package org.jamesdbloom.web.configuration;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.jamesdbloom.web.interceptor.bundling.AddBundlingModelToViewModelInterceptor;
import org.jamesdbloom.web.interceptor.bundling.WroModelHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.manager.factory.WroManagerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;

/**
 * @author jamesdbloom
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"org.jamesdbloom.web"})
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Resource
    private Environment environment;

    @Resource
    private WroModelHolder wroModelHolder;

    @Resource
    private ServletContext servletContext;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("default");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AddBundlingModelToViewModelInterceptor(wroModelHolder, environment.getProperty("bundling.enabled")));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

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
    public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setConfiguration(new freemarker.template.Configuration() {{
            setTemplateLoader(new MultiTemplateLoader(
                    new TemplateLoader[]{
                            new ClassTemplateLoader(FreeMarkerConfig.class, "/"),
                            new WebappTemplateLoader(servletContext, "/")
                    }
            ));
            setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
            setStrictSyntaxMode(true);
            setWhitespaceStripping(true);
        }});
        return freeMarkerConfigurer;
    }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setOrder(1);
        freeMarkerViewResolver.setPrefix("/WEB-INF/view/");
        freeMarkerViewResolver.setSuffix(".ftl");
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        return freeMarkerViewResolver;
    }
}
