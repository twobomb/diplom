package com.twobomb.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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


    public CourseWork() {
    }

    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "theme_bind_coursework",
            joinColumns = @JoinColumn(name = "id_coursework"),
            inverseJoinColumns = @JoinColumn(name = "id_theme"))
    List<Theme> themes;

    public List<Theme> getThemes() {
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
