package org.jamesdbloom.web.controller;

import org.jamesdbloom.web.configuration.WebMvcConfiguration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author jamesdbloom
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMvcConfiguration.class)
public class LoginPageControllerIntegrationTest {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setupFixture() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testShouldReturnLoginPageForGetRequest() throws Exception {
        MvcResult response = mockMvc.perform(get("/login").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();
        Document html = Jsoup.parse(response.getResponse().getContentAsString());

        // test username field
        Element usernameElement = html.select("input[name=j_username]").first();
        assertNotNull(usernameElement);
        assertEquals("1", usernameElement.attr("tabindex"));
        assertEquals("text", usernameElement.attr("type"));

        // test password field
        Element passwordElement = html.select("input[name=j_password]").first();
        assertNotNull(passwordElement);
        assertEquals("2", passwordElement.attr("tabindex"));
        assertEquals("password", passwordElement.attr("type"));
    }

    @Test
    public void testShouldNotReturnLoginPageForPostRequest() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isMethodNotAllowed());
    }
}
