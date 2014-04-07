package org.jamesdbloom.acceptance.login;

import org.jamesdbloom.acceptance.BasePage;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author jamesdbloom
 */
public class LoginPage extends BasePage {

    public LoginPage(String body) throws UnsupportedEncodingException {
        super(body);
    }

    public void shouldHaveCorrectFields() {
        hasCorrectUserNameField();
        hasCorrectPasswordField();
    }

    public void hasCorrectUserNameField() {
        Element usernameElement = html.select("input[name=username]").first();
        assertNotNull(usernameElement);
        assertEquals("1", usernameElement.attr("tabindex"));
        assertEquals("text", usernameElement.attr("type"));
    }

    public void hasCorrectPasswordField() {
        Element passwordElement = html.select("input[name=password]").first();
        assertNotNull(passwordElement);
        assertEquals("2", passwordElement.attr("tabindex"));
        assertEquals("password", passwordElement.attr("type"));
    }
}
