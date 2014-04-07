package org.jamesdbloom.acceptance.login;

import org.jamesdbloom.acceptance.BaseIntegrationTest;
import org.jamesdbloom.acceptance.PropertyMockingApplicationContextInitializer;
import org.jamesdbloom.configuration.RootConfiguration;
import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.domain.User;
import org.jamesdbloom.uuid.UUIDFactory;
import org.jamesdbloom.web.configuration.WebMvcConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author jamesdbloom
 */
public class LoginPageControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testShouldReturnLoginPageForGetRequest() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                get("/login")
                        .secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        LoginPage loginPage = new LoginPage(mvcResult.getResponse().getContentAsString());
        loginPage.shouldHaveCorrectFields();
    }

    @Test
    public void testShouldNotReturnLoginPageForPostRequest() throws Exception {
        // when
        mockMvc.perform(
                post("/login")
                        .secure(true)
                        .session(session)
        )
                // then
                .andExpect(status().isForbidden());
    }
}
