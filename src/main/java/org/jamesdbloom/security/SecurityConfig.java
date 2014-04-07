package org.jamesdbloom.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.annotation.Resource;

@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvcSecurity
@ComponentScan("org.jamesdbloom.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private AuthenticationProvider springSecurityAuthenticationProvider;

    @Resource
    private Environment environment;

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder("GMbO8etVKRFDEC8mZ1nCLxodpEd3BrrTn4Ju62R5");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(springSecurityAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico", "/resources/**", "/bundle/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requiresChannel().anyRequest().requiresSecure()
                .and()
                .portMapper().http(Integer.parseInt(environment.getProperty("http.port", "8080"))).mapsTo(Integer.parseInt(environment.getProperty("https.port", "8443")))
                .and()
                .sessionManagement().sessionFixation().newSession()
                .and()
                .authorizeRequests().antMatchers("/register", "/updatePassword").permitAll()
                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login").invalidateHttpSession(true).deleteCookies("JSESSIONID")
                .and()
                .authorizeRequests().antMatchers("/**").authenticated();
    }
}