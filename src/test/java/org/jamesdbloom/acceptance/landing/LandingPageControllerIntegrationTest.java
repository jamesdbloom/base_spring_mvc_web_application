package org.jamesdbloom.acceptance.landing;

import org.jamesdbloom.acceptance.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author jamesdbloom
 */
public class LandingPageControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    public void getPage() throws Exception {
        mockMvc.perform(
                get("/").secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();
    }

    @Test
    public void testShouldNotReturnLoginPageForPostRequest() throws Exception {
        mockMvc
                .perform(
                        post("/")
                                .secure(true)
                                .session(session)
                                .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
                )
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
    }

}
