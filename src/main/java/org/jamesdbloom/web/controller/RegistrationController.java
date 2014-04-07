package org.jamesdbloom.web.controller;

import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.domain.User;
import org.jamesdbloom.email.EmailService;
import org.jamesdbloom.uuid.UUIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * @author jamesdbloom
 */
@Controller
public class RegistrationController {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private UserDAO userDAO;
    @Resource
    private Environment environment;
    @Resource
    private EmailService emailService;
    @Resource
    private UUIDFactory uuidFactory;

    private void setupModel(Model model) {
        model.addAttribute("passwordPattern", User.PASSWORD_PATTERN);
        model.addAttribute("emailPattern", User.EMAIL_PATTERN);
        model.addAttribute("environment", environment);
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerForm(HttpServletRequest request, Model model) {
        setupModel(model);
        model.addAttribute("user", new User());
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrfParameterName", csrfToken.getParameterName());
            model.addAttribute("csrfToken", csrfToken.getToken());
        }
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid User user, BindingResult bindingResult, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws MalformedURLException, UnsupportedEncodingException {

        boolean userAlreadyExists = user.getEmail() != null && (userDAO.findByEmail(user.getEmail()) != null);
        if (bindingResult.hasErrors() || userAlreadyExists) {
            setupModel(model);
            if (userAlreadyExists) {
                bindingResult.addError(new ObjectError("user", "validation.user.alreadyExists"));
            }
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("user", user);
            return "register";
        }
        user.setOneTimeToken(uuidFactory.generateUUID());
        userDAO.save(user);
        emailService.sendRegistrationMessage(user, request);
        redirectAttributes.addFlashAttribute("message", "Your account has been created and an email has been sent to " + user.getEmail() + " with a link to create your password and login, please check your spam folder if you don't see the email within 5 minutes");
        redirectAttributes.addFlashAttribute("title", "Account Created");
        return "redirect:/message";
    }

}
