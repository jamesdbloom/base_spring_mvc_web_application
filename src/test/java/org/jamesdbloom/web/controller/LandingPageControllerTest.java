package org.jamesdbloom.web.controller;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;

/**
 * @author jamesdbloom
 */
public class LandingPageControllerTest {

    @Test
    public void testShouldReturnCorrectLogicalViewName() {
        assertEquals("landing", new LandingPageController().getPage());
    }
}
