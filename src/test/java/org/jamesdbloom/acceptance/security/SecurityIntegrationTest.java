package org.jamesdbloom.acceptance.security;

import org.jamesdbloom.acceptance.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author jamesdbloom
 */
public class SecurityIntegrationTest extends BaseIntegrationTest {

    @Test
    public void testShouldRedirectIfNotSecure() throws Exception {
        mockMvc.perform(
                get("/").secure(false)
                        .accept(MediaType.TEXT_HTML)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", is("/")));
    }

    @Test
    public void testShouldReturnDirectToLoginPageInNotLoggedIn() throws Exception {
        mockMvc.perform(
                get("/").secure(true)
                        .accept(MediaType.TEXT_HTML)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", is("http://localhost/login")));
    }


    @Test
    public void testShouldNotReturnLoginPageIfLoggedIn() throws Exception {
        mockMvc.perform(
                get("/").secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(header().doesNotExist("Location"));
    }
}
