package org.jamesdbloom.acceptance.registration;

import org.jamesdbloom.acceptance.BaseIntegrationTest;
import org.jamesdbloom.domain.User;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author jamesdbloom
 */
public class RegistrationIntegrationTest extends BaseIntegrationTest {

    private User expectedUser = new User(UUID.randomUUID().toString(), "register user", "register@email.com", "Password123");

    @Test
    public void shouldGetPage() throws Exception {
        // when
        mockMvc.perform(
                get("/register")
                        .secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        // when
        mockMvc.perform(
                post("/register")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", expectedUser.getName())
                        .param("email", expectedUser.getEmail())
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(redirectedUrl("/message"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("title", "Account Created"));

        User actualUser = userDAO.findByEmail(expectedUser.getEmail());

        try {
            assertThat(actualUser.getName(), is(expectedUser.getName()));
            assertThat(actualUser.getEmail(), is(expectedUser.getEmail()));
        } finally {
            userDAO.delete(actualUser.getId());
        }
    }

    @Test
    public void shouldGetPageWithNameError() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/register")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "xx")
                        .param("email", expectedUser.getEmail())
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        RegistrationPage registrationPage = new RegistrationPage(mvcResult.getResponse().getContentAsString());
        registrationPage.hasErrors("user", "Please provide a name between 3 and 50 characters");
        registrationPage.hasRegistrationFields("xx", expectedUser.getEmail());
    }

    @Test
    public void shouldGetPageWithEmailError() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/register")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", expectedUser.getName())
                        .param("email", "incorrect_email")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        RegistrationPage registrationPage = new RegistrationPage(mvcResult.getResponse().getContentAsString());
        registrationPage.hasErrors("user", "Please provide a valid email");
        registrationPage.hasRegistrationFields(expectedUser.getName(), "incorrect_email");
    }

    @Test
    public void shouldGetPageWithAllErrors() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/register")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "xx")
                        .param("email", "incorrect_email")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        RegistrationPage registrationPage = new RegistrationPage(mvcResult.getResponse().getContentAsString());
        registrationPage.hasErrors("user", "Please provide a name between 3 and 50 characters", "Please provide a valid email");
        registrationPage.hasRegistrationFields("xx", "incorrect_email");
    }

    @Test
    public void shouldGetPageWithEmailAlreadyTakenError() throws Exception {
        // given
        userDAO.save(new User("already_exists_id", "test name", "already_taken@email.com", "Password123"));

        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/register")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", expectedUser.getName())
                        .param("email", "already_taken@email.com")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        RegistrationPage registrationPage = new RegistrationPage(mvcResult.getResponse().getContentAsString());
        registrationPage.hasErrors("user", "That email address has already been taken");
        registrationPage.hasRegistrationFields(expectedUser.getName(), "already_taken@email.com");
    }

}
