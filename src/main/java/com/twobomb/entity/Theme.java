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

    @Type(type = "text")
    @Column(name="description")
    String description;

    @Type(type = "text")
    @Column(name = "text")
    String text;

    @Column(name = "edit_date")
    Date edit_date;

    @Column(name = "date")
    @ColumnDefault("now()")
    Date date;

    @JoinColumn(name = "id_edit_person")
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
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

    public Theme() {
    }

    public void setDescription(String description,Person whoEdited) {
        this.description = description;
        this.edit_person = whoEdited;
        this.edit_date = Calendar.getInstance().getTime();
    }

    public void setText(String text, Person whoEdited) {
        this.text = text;
        this.edit_person = whoEdited;
        this.edit_date = Calendar.getInstance().getTime();
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


    //Открпить студента от темы
        public void detachThemeFromCourseworkWithStudent(CourseWork coursework,Person student){
            Map<CourseWork,Person> affixStud = getCoueseworkWidthAffixedStudents();
            for(CourseWork cw:affixStud.keySet())
                if(cw.equals(coursework) && affixStud.get(cw).equals(student)){
                    affixStud.remove(cw,affixStud.get(cw));
                    break;
                }
        }
//    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    @JoinTable(name = "student_bind_theme_in_coursework",
//            joinColumns = @JoinColumn(name = "id_theme"),
//            inverseJoinColumns = @JoinColumn(name = "id_person_student"))
    public void addAttachStudentAndCoursework(Person student,CourseWork cw){
        try{
            if(!student.getUser().getRole().getRole().equals(Role.STUDENT))
                throw new Exception("Role this person not equals "+Role.STUDENT);
        }
        catch (Exception e){
            e.printStackTrace();
            return;
        }
        coueseworkWidthAffixedStudents.put(cw,student);
    }
    public Map<CourseWork, Person> getCoueseworkWidthAffixedStudents() {
        return coueseworkWidthAffixedStudents;
    }

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
