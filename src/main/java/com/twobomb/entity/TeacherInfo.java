package com.twobomb.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "teatcher_info")
public class TeacherInfo extends AbstractEntity{

    @NotNull
    @Column(name="count_theme")
    Integer count_theme;

    @NotNull
    @JoinColumn(name = "id_person_teacher")
    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE})
    Person person;

    public TeacherInfo(@NotNull Integer count_theme, @NotNull Person person, @NotNull CourseWork courseWork) {
        this.count_theme = count_theme;
        this.person = person;
        this.courseWork = courseWork;
    }

    public TeacherInfo() {
    }

    @NotNull
    @JoinColumn(name = "id_coursework")
    @ManyToOne(cascade = {CascadeType.REFRESH,CascadeType.MERGE})
    CourseWork courseWork;
}
