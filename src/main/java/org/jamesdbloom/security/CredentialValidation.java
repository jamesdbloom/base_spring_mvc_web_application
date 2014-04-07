package org.jamesdbloom.security;

import org.jamesdbloom.dao.UserDAO;
import org.jamesdbloom.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author jamesdbloom
 */
@Component
public class CredentialValidation {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDAO userDAO;
    @Resource
    private Environment environment;

    public User validateUsernameAndPassword(String username, CharSequence password) {
        User user = userDAO.findByEmail(decodeURL(username));
        if (!credentialsMatch(password, user)) {
            logger.info(environment.getProperty("validation.user.invalidCredentials"));
            throw new BadCredentialsException(environment.getProperty("validation.user.invalidCredentials"));
        }
        return user;
    }

    private boolean credentialsMatch(CharSequence password, User user) {
        return user != null && password != null && user.getPassword() != null && passwordEncoder.matches(password, user.getPassword());
    }

    private String decodeURL(String urlEncoded) {
        try {
            return URLDecoder.decode(urlEncoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Exception decoding URL value [" + urlEncoded + "]", e);
        }
    }
}
