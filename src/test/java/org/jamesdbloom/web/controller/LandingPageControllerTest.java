package org.jamesdbloom.web.controller;

import org.junit.Test;

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
