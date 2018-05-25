package com.twobomb.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "teatcher_info")
public class TeacherInfo extends AbstractEntity{

    @NotNull
    @Column(name="count_theme")
    Integer count_theme;

    public Integer getCount_theme() {
        return count_theme;
    }

    public Person getPerson() {
        return person;
    }

    public CourseWork getCourseWork() {
        return courseWork;
    }

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


    @JoinColumn(name = "id_sciece_agreement")
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    Person sciece_agreement;//Консультат, не обязательно (для ВКР)

    public Person getSciece_agreement() {
        return sciece_agreement;
    }

    public void setSciece_agreement(Person sciece_agreement) {
        this.sciece_agreement = sciece_agreement;
    }
}
