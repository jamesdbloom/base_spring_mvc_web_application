package org.jamesdbloom.security;

import org.jamesdbloom.domain.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jamesdbloom
 */
@Component
public class SpringSecurityAuthenticationProvider implements AuthenticationProvider {

    @Resource
    private CredentialValidation credentialValidation;

    @Resource
    private SpringSecurityUserContext springSecurityUserContext;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        User user;
        if (token.getPrincipal() instanceof User) {
            user = (User) token.getPrincipal();
        } else {
            user = credentialValidation.validateUsernameAndPassword(token.getName(), (CharSequence) token.getCredentials());
        }
        springSecurityUserContext.setCurrentUser(user);
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), AuthorityUtils.createAuthorityList("USER"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
