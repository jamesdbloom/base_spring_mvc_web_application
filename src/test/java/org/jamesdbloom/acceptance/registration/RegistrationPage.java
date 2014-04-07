package org.jamesdbloom.acceptance.registration;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jamesdbloom.acceptance.BasePage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * @author jamesdbloom
 */
public class RegistrationPage extends BasePage {

    public RegistrationPage(String body) throws UnsupportedEncodingException {
        super(body);
    }

    public void hasErrors(String objectName, String... expectedErrorMessages) {
        List<String> errorMessages = Lists.transform(html.select("#validation_error_" + objectName + " .validation_error"), new Function<Element, String>() {
            public String apply(Element input) {
                return input.text().replace("– ", "");
            }
        });
        assertThat(errorMessages, containsInAnyOrder(expectedErrorMessages));
    }

    public void shouldHaveCorrectFields() {
        hasRegistrationFields("", "");
    }

    public void hasRegistrationFields(String name, String email) {
        Element nameInputElement = html.select("#name").first();
        assertNotNull(nameInputElement);
        assertEquals(name, nameInputElement.val());

        Element emailInputElement = html.select("#email").first();
        assertNotNull(emailInputElement);
        assertEquals(email, emailInputElement.val());
    }
}
