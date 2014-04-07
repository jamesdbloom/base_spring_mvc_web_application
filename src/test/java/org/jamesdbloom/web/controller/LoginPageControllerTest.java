package org.jamesdbloom.web.controller;

import org.junit.Test;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author jamesdbloom
 */
public class LoginPageControllerTest {

    @Test
    public void testShouldReturnCorrectLogicalViewName() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        CsrfToken csrfToken = mock(CsrfToken.class);
        when(request.getAttribute(CsrfToken.class.getName())).thenReturn(csrfToken);
        when(csrfToken.getParameterName()).thenReturn("parameterName");
        when(csrfToken.getToken()).thenReturn("token");
        Model model = new ExtendedModelMap();

        // when
        String page = new LoginPageController().getPage(request, model);

        // then
        assertEquals("login", page);
        assertEquals("parameterName", model.asMap().get("csrfParameterName"));
        assertEquals("token", model.asMap().get("csrfToken"));
    }
}
