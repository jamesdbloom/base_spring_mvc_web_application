package org.jamesdbloom.uuid;

import org.jamesdbloom.domain.User;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author jamesdbloom
 */
public class UUIDFactory {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public boolean hasMatchingUUID(User user, String oneTimeToken) {
        boolean userTokenValid = user != null && user.getOneTimeToken() != null && UUID_PATTERN.matcher(user.getOneTimeToken()).matches();
        boolean matchingTokenValid = oneTimeToken != null && UUID_PATTERN.matcher(oneTimeToken).matches();
        return userTokenValid && matchingTokenValid && user.getOneTimeToken().equals(oneTimeToken);
    }
}
