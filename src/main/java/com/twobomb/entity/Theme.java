package com.twobomb.entity;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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

    @NotNull
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

    @NotNull
    @JoinColumn(name = "id_add_person")
    @OneToOne(cascade = CascadeType.ALL)
    Person add_person;


    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "theme_bind_coursework",
            joinColumns = @JoinColumn(name = "id_theme"),
            inverseJoinColumns = @JoinColumn(name = "id_coursework"))
    List<CourseWork> courseWorks;


    public List<CourseWork> getCourseWorks() {
        return courseWorks;
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
