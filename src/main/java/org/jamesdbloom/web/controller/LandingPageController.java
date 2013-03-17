package org.jamesdbloom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jamesdbloom
 */
@Controller
public class LandingPageController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getPage() {
        return "landing";
    }
}
