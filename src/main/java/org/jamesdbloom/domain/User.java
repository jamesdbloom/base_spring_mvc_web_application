package org.jamesdbloom.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author jamesdbloom
 */
@Entity
public class User extends EqualsHashCodeToString {

    public static final String PASSWORD_PATTERN = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z]).*$";
    public static final String EMAIL_PATTERN = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";
    @Id
    private String id;
    @Version
    private Integer version;
    @NotNull(message = "validation.user.name")
    @Size(min = 3, max = 50, message = "validation.user.name")
    private String name;
    @NotNull(message = "validation.user.email")
    @Pattern(regexp = EMAIL_PATTERN, message = "validation.user.email")
    @Column(unique = true)
    private String email;
    @JsonIgnore
    @Pattern(regexp = PASSWORD_PATTERN, message = "validation.user.password")
    private String password;
    @JsonIgnore
    private String oneTimeToken;

    public User() {
    }

    public User(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User withName(String name) {
        withName(name);
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User withEmail(String email) {
        setEmail(email);
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User withPassword(String password) {
        setPassword(password);
        return this;
    }

    public String getOneTimeToken() {
        return oneTimeToken;
    }

    public void setOneTimeToken(String oneTimeToken) {
        this.oneTimeToken = oneTimeToken;
    }

    public User withOneTimeToken(String oneTimeToken) {
        setOneTimeToken(oneTimeToken);
        return this;
    }
}
