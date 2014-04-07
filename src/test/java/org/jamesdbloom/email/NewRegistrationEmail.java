package org.jamesdbloom.email;

import org.jamesdbloom.acceptance.BasePage;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author jamesdbloom
 */
public class NewRegistrationEmail extends BasePage {

    public NewRegistrationEmail(String body) throws UnsupportedEncodingException {
        super(body);
    }

    public String shouldHaveCorrectFields(String email) throws UnsupportedEncodingException {
        hasCorrectTitle();
        return hasCorrectUpdatePasswordLink(email);
    }

    private void hasCorrectTitle() {
        Element title = html.select("h1").first();
        assertNotNull(title);
        assertEquals("MyApplication - New Registration", title.text());
    }

    private String hasCorrectUpdatePasswordLink(String email) throws UnsupportedEncodingException {
        Element updatePasswordLink = html.select("a").first();
        assertNotNull(updatePasswordLink);
        assertThat(updatePasswordLink.attr("href"), containsString("updatePassword"));
        assertThat(updatePasswordLink.attr("href"), containsString("email=" + URLEncoder.encode(email, StandardCharsets.UTF_8.name())));
        assertThat(updatePasswordLink.attr("href"), containsString("oneTimeToken="));
        return updatePasswordLink.attr("href");
    }
}
