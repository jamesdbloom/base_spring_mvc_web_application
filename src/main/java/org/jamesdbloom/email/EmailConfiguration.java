package org.jamesdbloom.email;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * @author jamesdbloom
 */
@Configuration
@PropertySource("classpath:email.properties")
@ComponentScan(basePackages = {"org.jamesdbloom.email"})
@EnableAspectJAutoProxy
public class EmailConfiguration {


    @Resource
    private Environment environment;

    @Bean
    public MailSender mailSender() {
        return new JavaMailSenderImpl() {{
            setHost(environment.getProperty("email.host"));
            setPort(environment.getProperty("email.port", Integer.class));
            setProtocol(environment.getProperty("email.protocol"));
            setUsername(environment.getProperty("email.username"));
            setPassword(environment.getProperty("email.password"));
            setJavaMailProperties(new Properties() {{ // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
                setProperty("mail.smtp.auth", "true");
                setProperty("mail.smtp.starttls.enable", environment.getProperty("email.starttls"));
                setProperty("mail.smtp.quitwait", "false");
            }});
        }};
    }

//    @Bean
//    public ServletContextTemplateResolver templateResolver() {
//        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
//        templateResolver.setPrefix("/WEB-INF/pages/");
//        templateResolver.setSuffix(".leaf");
//        templateResolver.setTemplateMode("HTML5");
//        templateResolver.setCharacterEncoding("UTF-8");
//        templateResolver.setCacheable(false);
//        return templateResolver;
//    }
//
//    @Bean
//    public SpringTemplateEngine templateEngine(ServletContextTemplateResolver templateResolver) {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.setTemplateResolver(templateResolver);
//        return templateEngine;
//    }
//
//    @Bean
//    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
//        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
//        thymeleafViewResolver.setTemplateEngine(templateEngine);
//        thymeleafViewResolver.setOrder(1);
//        thymeleafViewResolver.setCharacterEncoding("UTF-8");
//        return thymeleafViewResolver;
//    }
}
