package com.twobomb.entity;

import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "discipline_load")
public class Discipline extends AbstractEntity{


    @Column(name = "name")
    String name;

    @OneToOne(mappedBy = "discipline",fetch = FetchType.EAGER)
    Control_Discipline control_discipline;

    @OneToMany(mappedBy = "discipline",fetch = FetchType.EAGER)
    List<CourseWork> courseWork;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_bind_discipline",
            joinColumns = @JoinColumn(name = "id_discipline_load"),
            inverseJoinColumns = @JoinColumn(name = "id_person_teacher"))
    List<Person> attachedTeachers;

    public Discipline(String name) {
        this.name = name;
    }


    public List<Group> getGroups() {
        return groups;
    }

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "groups_bind_discipline",
            joinColumns = @JoinColumn(name = "id_discipline_load"),
            inverseJoinColumns = @JoinColumn(name = "id_group"))
    List<Group> groups;


    public Discipline() {
    }

    public Control_Discipline getControl_discipline() {
        return control_discipline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CourseWork> getCourseWork() {
        return courseWork;
    }
}
