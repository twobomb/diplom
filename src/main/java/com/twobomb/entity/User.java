package com.twobomb.entity;

//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="users")
public class User extends AbstractEntity{
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }


    @NotNull
    @Column(name="username",unique = true)
    private String login;

    @NotNull
    @Column(name="password")
    private String password;

    @OneToOne(mappedBy = "user",fetch = FetchType.EAGER)
    Person person;

    public User(@NotNull String login, @NotNull String password, @NotNull Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }

    @NotNull
    @ManyToOne(cascade = {CascadeType.REMOVE,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name="role_id")
    private Role role;

    public User() {
    }
    public Long getId() {
        return id;

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;

    }
    public Collection<GrantedAuthority> getAuthorities(){
        Collection<GrantedAuthority> col = new ArrayList<GrantedAuthority>();
        col.add(new SimpleGrantedAuthority(role.getRole()));
        return col;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public Person getPerson() {
        return person;
    }
}
