package org.jamesdbloom.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
