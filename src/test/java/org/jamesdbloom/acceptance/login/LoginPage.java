package org.jamesdbloom.acceptance.login;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author jamesdbloom
 */
public class LoginPage {
    private final Document html;

    public LoginPage(MvcResult response) throws UnsupportedEncodingException {
        html = Jsoup.parse(response.getResponse().getContentAsString());
    }

    public void shouldHaveCorrectFields() {
        hasCorrectUserNameField();
        hasCorrectPasswordField();
    }

    public void hasCorrectUserNameField() {
        Element usernameElement = html.select("input[name=j_username]").first();
        assertNotNull(usernameElement);
        assertEquals("1", usernameElement.attr("tabindex"));
        assertEquals("text", usernameElement.attr("type"));
    }

    public void hasCorrectPasswordField() {
        Element passwordElement = html.select("input[name=j_password]").first();
        assertNotNull(passwordElement);
        assertEquals("2", passwordElement.attr("tabindex"));
        assertEquals("password", passwordElement.attr("type"));
    }
}
