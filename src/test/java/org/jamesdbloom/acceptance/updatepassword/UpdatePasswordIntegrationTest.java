package org.jamesdbloom.acceptance.updatepassword;

import org.jamesdbloom.acceptance.BaseIntegrationTest;
import org.jamesdbloom.domain.User;
import org.jamesdbloom.uuid.UUIDFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author jamesdbloom
 */
public class UpdatePasswordIntegrationTest extends BaseIntegrationTest {

    private User expectedUser = new User(UUID.randomUUID().toString(), "password user", "password@email.com", "Password123");

    @Before
    public void saveUser() {
        expectedUser.setOneTimeToken(new UUIDFactory().generateUUID());
        userDAO.save(expectedUser);
    }

    @After
    public void removeUser() {
        userDAO.delete(expectedUser.getId());
    }

    @Test
    public void shouldGetPage() throws Exception {
        // when
        mockMvc.perform(
                get("/updatePassword")
                        .secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", expectedUser.getEmail())
                        .param("oneTimeToken", expectedUser.getOneTimeToken())
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }


    @Test
    public void shouldValidateEmailAndOneTimeTokenWhenDisplayingForm() throws Exception {
        // when
        mockMvc.perform(
                get("/updatePassword")
                        .secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "incorrect_email")
                        .param("oneTimeToken", "incorrect_token")
        )
                // then
                .andExpect(redirectedUrl("/message"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("title", "Invalid Request"))
                .andExpect(flash().attribute("error", true));
    }

    @Test
    public void shouldUpdatePassword() throws Exception {
        // when
        mockMvc.perform(
                post("/updatePassword")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", expectedUser.getEmail())
                        .param("oneTimeToken", expectedUser.getOneTimeToken())
                        .param("password", "NewPassword123")
                        .param("passwordConfirm", "NewPassword123")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(redirectedUrl("/message"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("title", "Password Updated"));

        User actualUser = userDAO.findByEmail(expectedUser.getEmail());
        assertTrue(passwordEncoder.matches("NewPassword123", actualUser.getPassword()));
    }

    @Test
    public void shouldGetPageWithPasswordFormatError() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/updatePassword")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", expectedUser.getEmail())
                        .param("oneTimeToken", expectedUser.getOneTimeToken())
                        .param("password", "password")
                        .param("passwordConfirm", "password")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        UpdatePasswordPage updatePasswordPage = new UpdatePasswordPage(mvcResult.getResponse().getContentAsString());
        updatePasswordPage.hasErrors("Please provide a password of 8 or more characters with at least 1 digit and 1 letter");
    }

    @Test
    public void shouldGetPageWithPasswordMatchingError() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                post("/updatePassword")
                        .secure(true)
                        .session(session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", expectedUser.getEmail())
                        .param("oneTimeToken", expectedUser.getOneTimeToken())
                        .param("password", "NewPassword123")
                        .param("passwordConfirm", "Password123")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andReturn();

        UpdatePasswordPage updatePasswordPage = new UpdatePasswordPage(mvcResult.getResponse().getContentAsString());
        updatePasswordPage.hasErrors("The second password field does not match the first password field");
    }

    @Test
    public void shouldValidateEmailAndOneTimeTokenWhenSubmittingForm() throws Exception {
        // when
        mockMvc.perform(
                post("/updatePassword")
                        .secure(true)
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "incorrect_email")
                        .param("oneTimeToken", "incorrect_token")
                        .param("password", "NewPassword123")
                        .param("passwordConfirm", "NewPassword123")
                        .param((csrfToken != null ? csrfToken.getParameterName() : "_csrf"), (csrfToken != null ? csrfToken.getToken() : ""))
        )
                // then
                .andExpect(redirectedUrl("/message"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("title", "Invalid Request"))
                .andExpect(flash().attribute("error", true));
    }

}
