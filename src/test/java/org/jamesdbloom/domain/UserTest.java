package org.jamesdbloom.domain;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author jamesdbloom
 */
public class UserTest {

    @Test
    public void shouldReturnValuesSetInConstructor() {
        // given
        String id = "id";
        String name = "name";
        String email = "email";
        String password = "password";

        // when
        User user = new User(id, name, email, password);

        // then
        assertThat(user.getId(), is(id));
        assertThat(user.getName(), is(name));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getPassword(), is(password));
    }

    @Test
    public void shouldReturnValuesSetInSetters() {
        // given
        String id = "id";
        String name = "name";
        String email = "email";
        String password = "password";

        // when
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // then
        assertThat(user.getId(), is(id));
        assertThat(user.getName(), is(name));
        assertThat(user.getEmail(), is(email));
        assertThat(user.getPassword(), is(password));
    }
}
