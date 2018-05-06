package com.twobomb.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "courseworks")
public class CourseWork extends AbstractEntity {

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "id_discipline_load")
    private Discipline discipline;


//    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name = "student_bind_theme_in_coursework",
//            joinColumns = @JoinColumn(name = "id_coursework"),
//            inverseJoinColumns = @JoinColumn(name = "id_theme"))

    public Map<Theme, Person> getThemesWithAffixedStudent() {
        return themesWithAffixedStudent;
    }

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(name = "student_bind_theme_in_coursework",
            joinColumns = @JoinColumn(name = "id_coursework"),
            inverseJoinColumns = @JoinColumn(name = "id_person_student"))
    @MapKeyJoinColumn(name = "id_theme")
    private Map<Theme,Person> themesWithAffixedStudent;

    public Discipline getDiscipline() {
        return discipline;
    }

    public CourseWork() {

    }

    public String getName() {
        return name;
    }

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "theme_bind_coursework",
            joinColumns = @JoinColumn(name = "id_coursework"),
            inverseJoinColumns = @JoinColumn(name = "id_theme"))
    List<Theme> themes;


    public List<TeacherInfo> getTeacherInfos() {
        return teacherInfos;
    }

    @OneToMany(mappedBy = "courseWork",fetch = FetchType.LAZY)
    List<TeacherInfo> teacherInfos;

    public List<Theme> getThemes() {
        if(themes == null)
            themes = new ArrayList<>();
        return themes;
    }

    public CourseWork(@NotNull String name, @NotNull Discipline discipline) {
        this.name = name;
        this.discipline = discipline;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CourseWork){
            CourseWork cw = (CourseWork)obj;
            return cw.id == id;
        }
        else
        return false;
    }
}
