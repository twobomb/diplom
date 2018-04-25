package com.twobomb.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.*;

@Entity
@Table(name = "themes")
public class Theme extends AbstractEntity{

    @Column(name="description")
    String description;

    @Type(type = "text")
    @Column(name = "text")
    String text;

    @Column(name = "edit_date")
    Date edit_date;

    @ColumnDefault("now()")
    @Column(name = "date")
    Date date;

    @JoinColumn(name = "id_edit_person")
    @OneToOne(cascade = CascadeType.ALL)
    Person edit_person;




    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Theme){
            Theme cw = (Theme)obj;
            return cw.id == id;
        }
        else
            return false;
    }

    public Theme(String description, String text, @NotNull Person add_person) {
        this.description = description;
        this.text = text;
        this.add_person = add_person;
    }

    @NotNull
    @JoinColumn(name = "id_add_person")
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    Person add_person;


    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "theme_bind_coursework",
            joinColumns = @JoinColumn(name = "id_theme"),
            inverseJoinColumns = @JoinColumn(name = "id_coursework"))
    List<CourseWork> courseWorks;


//    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name = "student_bind_theme_in_coursework",
//            joinColumns = @JoinColumn(name = "id_theme"),
//            inverseJoinColumns = @JoinColumn(name = "id_person_student"))

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "student_bind_theme_in_coursework",
            joinColumns = @JoinColumn(name = "id_theme"),
            inverseJoinColumns = @JoinColumn(name = "id_person_student"))
    @MapKeyJoinColumn(name = "id_coursework")
    private Map<CourseWork,Person> coueseworkWidthAffixedStudents;

    public void addCourseWork(CourseWork cw){
        if(courseWorks == null)
            courseWorks = new ArrayList<>();
        courseWorks.add(cw);
    }
    public List<CourseWork> getCourseWorks() {
        return  courseWorks;
    }

    public String getDescription() {

        return description;
    }

    public String getText() {
        return text;
    }

    public Date getEdit_date() {
        return edit_date;
    }

    public Date getDate() {
        return date;
    }

    public Person getEdit_person() {
        return edit_person;
    }

    public Person getAdd_person() {
        return add_person;
    }

}
