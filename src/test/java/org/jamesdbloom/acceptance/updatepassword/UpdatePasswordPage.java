package org.jamesdbloom.acceptance.updatepassword;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.jamesdbloom.acceptance.BasePage;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author jamesdbloom
 */
public class UpdatePasswordPage extends BasePage {

    public UpdatePasswordPage(String body) throws UnsupportedEncodingException {
        super(body);
    }

    public void hasErrors(String... expectedErrorMessages) {
        List<String> errorMessages = Lists.transform(html.select("#validation_error .validation_error"), new Function<Element, String>() {
            public String apply(Element input) {
                return input.text().replace("– ", "");
            }
        });
        assertThat(errorMessages, containsInAnyOrder(expectedErrorMessages));
    }

    public void shouldHaveCorrectFields() {
        Element passwordInputElement = html.select("#password").first();
        assertNotNull(passwordInputElement);

        Element passwordConfirmInputElement = html.select("#passwordConfirm").first();
        assertNotNull(passwordConfirmInputElement);
    }
}
