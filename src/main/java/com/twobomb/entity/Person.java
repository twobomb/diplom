package com.twobomb.entity;

import com.twobomb.service.DisciplineService;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "persons")
public class Person extends AbstractEntity{

    @Column(name = "firstname")
    String firstname;

    @Column(name = "lastname")
    String lastname;

    @JoinColumn(name = "user_id")
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    User user;
/*
    @JoinColumn(name = "sciece_agreement_id")
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    List<VKR> vkrs_when_i_agreement;

    @JoinColumn(name = "sciece_head_id")
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    List<VKR> vkrs_when_i_head;*/

    @JoinColumn(name = "group_id")
    @ManyToOne(cascade =  {CascadeType.MERGE,CascadeType.REFRESH})
    Group group;


    public List<Theme> getAdd_themes() {
        return add_themes;
    }

    public Person(String firstname, String lastname, User user, Group group) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.user = user;
        this.group = group;
    }

    @OneToMany(mappedBy = "add_person",fetch = FetchType.LAZY)
    private List<Theme> add_themes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teacher_bind_discipline",
            joinColumns = @JoinColumn(name = "id_person_teacher"),
            inverseJoinColumns = @JoinColumn(name = "id_discipline_load"))
    private List<Discipline> disciplinesTeacher;


    @OneToMany(mappedBy = "person",fetch = FetchType.LAZY)
    private List<TeacherInfo> teacherInfos;


    public List<TeacherInfo> getTeacherInfos() {
        return teacherInfos;
    }

    public void addTeacherInfo(TeacherInfo teacherInfo){
        try{
            if(!getUser().getRole().isTeacher())
                throw new Exception("This person not have role teacher");
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        this.teacherInfos.add(teacherInfo);
    }

    public void addDisciplineTeacher(Discipline disciplinesTeacher) {
        this.disciplinesTeacher.add(disciplinesTeacher);
    }

    //темы прикрепленные к студенту

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "student_bind_theme_in_coursework",
            joinColumns = @JoinColumn(name = "id_person_student"),
            inverseJoinColumns = @JoinColumn(name = "id_theme"))
    @MapKeyJoinColumn(name = "id_coursework")
    private Map<CourseWork,Theme> affixThemesStudent;


    public List<Discipline> getDisciplinesTeacher() {
        return disciplinesTeacher;
    }


    public List<Theme> getAffixThemesStudent() {
        return new ArrayList<>(affixThemesStudent.values());
    }
    public Person() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Person){
            Person p = (Person)obj;
            return p.id == id;
        }
        else
            return false;
    }

    public Group getGroup() {
        return group;
    }
}
