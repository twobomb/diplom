package com.twobomb.entity;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role extends AbstractEntity{
    public static final String ADMIN = "admin";
    public static final String TEACHER = "teacher";
    public static final String STUDENT = "student";


    @Column(name="role_name")
    private String role_name;

    @Column(name="role_short_name")
    private String role;

    public Role() {
    }
    public boolean isAdmin(){
        return role.equals(ADMIN);
    }
    public boolean isStudent(){
        return role.equals(STUDENT);
    }
    public boolean isTeacher(){
        return role.equals(TEACHER);
    }

    @OneToMany(mappedBy = "role",fetch = FetchType.EAGER,cascade = CascadeType.REFRESH)
    private List<User> users;

    public Role(String role_name, String role) {
        this.role_name = role_name;
        this.role = role;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getRole() {
        return role;
    }

    public List<User> getUsers() {
        return users;
    }
}
