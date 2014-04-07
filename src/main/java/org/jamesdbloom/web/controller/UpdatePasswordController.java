package org.jamesdbloom.web.controller;

import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.domain.User;
import org.jamesdbloom.email.EmailService;
import org.jamesdbloom.security.SpringSecurityUserContext;
import org.jamesdbloom.uuid.UUIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author jamesdbloom
 */
@Controller
public class UpdatePasswordController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final Pattern PASSWORD_MATCHER = Pattern.compile(User.PASSWORD_PATTERN);
    @Resource
    private Environment environment;
    @Resource
    private UserDAO userDAO;
    @Resource
    private UUIDFactory uuidFactory;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private SpringSecurityUserContext securityUserContext;
    @Resource
    private EmailService emailService;

    @RequestMapping(value = "/sendUpdatePasswordEmail", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=UTF-8")
    public String sendUpdatePasswordEmail(String email, HttpServletRequest request, RedirectAttributes redirectAttributes) throws MalformedURLException, UnsupportedEncodingException {
        User user = userDAO.findByEmail(email);
        if (user != null) {
            user.setOneTimeToken(uuidFactory.generateUUID());
            userDAO.save(user);
            emailService.sendUpdatePasswordMessage(user, request);
        }
        redirectAttributes.addFlashAttribute("message", "An email has been sent to " + email + " with a link to create your password and login");
        redirectAttributes.addFlashAttribute("title", "Message Sent");
        return "redirect:/message";
    }

    private boolean hasInvalidToken(User user, String oneTimeToken, HttpServletRequest request, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (!uuidFactory.hasMatchingUUID(user, oneTimeToken)) {
            redirectAttributes.addFlashAttribute("message", "Invalid email or one-time-token" + (user != null ? " - click <a href=\"/sendUpdatePasswordEmail?email=" + URLEncoder.encode(user.getEmail(), "UTF-8") + "\">resend email</a> to receive a new email" : ""));
            redirectAttributes.addFlashAttribute("title", "Invalid Request");
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("csrfParameterName", ((CsrfToken)request.getAttribute(CsrfToken.class.getName())).getParameterName());
            redirectAttributes.addFlashAttribute("csrfToken", ((CsrfToken)request.getAttribute(CsrfToken.class.getName())).getToken());
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.GET)
    public String updatePasswordForm(String email, String oneTimeToken, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        if (hasInvalidToken(userDAO.findByEmail(email), oneTimeToken, request, redirectAttributes)) {
            return "redirect:/message";
        }
        model.addAttribute("passwordPattern", User.PASSWORD_PATTERN);
        model.addAttribute("environment", environment);
        model.addAttribute("email", email);
        model.addAttribute("oneTimeToken", oneTimeToken);
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("csrfParameterName", csrfToken.getParameterName());
            model.addAttribute("csrfToken", csrfToken.getToken());
        }
        return "updatePassword";
    }

    @Transactional
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String email, String password, String passwordConfirm, String oneTimeToken, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
        User user = userDAO.findByEmail(email);
        if (hasInvalidToken(user, oneTimeToken, request, redirectAttributes)) {
            return "redirect:/message";
        }
        boolean passwordFormatError = !PASSWORD_MATCHER.matcher(String.valueOf(password)).matches();
        boolean passwordsMatchError = !String.valueOf(password).equals(passwordConfirm);
        if (passwordFormatError || passwordsMatchError) {
            model.addAttribute("passwordPattern", User.PASSWORD_PATTERN);
            model.addAttribute("environment", environment);
            model.addAttribute("email", email);
            model.addAttribute("oneTimeToken", oneTimeToken);
            List<String> errors = new ArrayList<>();
            if (passwordFormatError) {
                errors.add("validation.user.password");
            }
            if (passwordsMatchError) {
                errors.add("validation.user.passwordNonMatching");
            }
            model.addAttribute("validationErrors", errors);
            logger.info("Validation error while trying to update password for " + email + "\n" + errors);
            return "updatePassword";
        }
        user.setPassword(passwordEncoder.encode(password));
        userDAO.save(user);

        redirectAttributes.addFlashAttribute("message", "Your password has been updated");
        redirectAttributes.addFlashAttribute("title", "Password Updated");
        logger.info("Password updated for " + email);
        return "redirect:/message";
    }

}
