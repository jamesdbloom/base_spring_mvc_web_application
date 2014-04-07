package org.jamesdbloom.acceptance;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;

/**
 * @author jamesdbloom
 */
public class BasePage {
    protected final Document html;

    public BasePage(String body) throws UnsupportedEncodingException {
        html = Jsoup.parse(body);
    }

    public String csrfValue() {
        Element csrfElement = html.select("#csrf").first();
        if (csrfElement != null) {
            return csrfElement.val();
        } else {
            return "";
        }
    }
}
