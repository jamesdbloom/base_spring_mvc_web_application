package org.jamesdbloom.acceptance;

import org.jamesdbloom.configuration.RootConfiguration;
import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.domain.User;
import org.jamesdbloom.email.EmailService;
import org.jamesdbloom.uuid.UUIDFactory;
import org.jamesdbloom.web.configuration.WebMvcConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author jamesdbloom
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(
                name = "root",
                classes = {
                        RootConfiguration.class,
                        BaseIntegrationTest.MockConfiguration.class
                }
        ),
        @ContextConfiguration(
                name = "dispatcher",
                classes = WebMvcConfiguration.class,
                initializers = PropertyMockingApplicationContextInitializer.class
        )
})
public abstract class BaseIntegrationTest {

    protected static final String uuid = UUID.randomUUID().toString();

    @Resource
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    @Resource
    @SuppressWarnings("SpringJavaAutowiringInspection")
    protected FilterChainProxy springSecurityFilter;

    protected static MockHttpSession session;

    @Resource
    protected UserDAO userDAO;
    @Resource
    protected PasswordEncoder passwordEncoder;
    protected static CsrfToken csrfToken;
    protected User user;

    @BeforeClass
    public static void createSession() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@email.com", "password");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    @Before
    public void setupFixture() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilter)
                .alwaysDo(print()).build();

        user = new User(uuid, "user", "user@email.com", passwordEncoder.encode("password"));
        userDAO.save(user);
        csrfToken = (CsrfToken) mockMvc.perform(get("/").secure(true).session(session)).andReturn().getRequest().getAttribute("_csrf");
    }

    @After
    public void cleanFixture() {
        userDAO.delete(user.getId());
    }

    @Configuration
    static class MockConfiguration {

        @Bean
        public UUIDFactory uuidFactory() {
            UUIDFactory mockUUIDFactory = spy(new UUIDFactory());
            when(mockUUIDFactory.generateUUID()).thenReturn(uuid);
            return mockUUIDFactory;
        }

        @Bean
        public EmailService emailService() {
            return mock(EmailService.class);
        }
    }
}
