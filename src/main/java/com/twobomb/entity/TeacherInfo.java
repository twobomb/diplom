package com.twobomb.entity;

import javax.persistence.*;

@Entity
@Table(name = "teatcher_info_coursework")
public class TeacherInfo extends AbstractEntity{

    @Column(name="count_theme")
    Integer count_theme;

    @JoinColumn(name = "id_person_teacher")
    @ManyToOne(cascade = CascadeType.ALL)
    Person person;

    @JoinColumn(name = "id_coursework")
    @ManyToOne(cascade = CascadeType.ALL)
    CourseWork courseWork;
}
