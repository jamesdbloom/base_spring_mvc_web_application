package org.jamesdbloom.web.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author jamesdbloom
 */
public class LoginPageControllerTest {

    @Test
    public void testShouldReturnCorrectLogicalViewName() {
        assertEquals("login", new LoginPageController().getPage());
    }
}
