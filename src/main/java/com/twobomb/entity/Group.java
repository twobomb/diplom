package com.twobomb.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="groups")
public class Group extends AbstractEntity {

    @Column(name = "course")
    private Integer course;

    @Column(name = "cipher")
    private String cipher;

    @Column(name = "name")
    private String name;


    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.REFRESH},fetch = FetchType.LAZY)
    @JoinTable(name = "groups_bind_discipline",
            joinColumns = @JoinColumn(name = "id_group"),
            inverseJoinColumns = @JoinColumn(name = "id_discipline_load"))
    private List<Discipline> disciplines;

    public void addDiscipline(Discipline discipline){
        this.disciplines.add(discipline);
    }
    public List<Discipline> getDisciplines(){
        return disciplines;
    }

    @OneToMany(mappedBy = "group",fetch = FetchType.EAGER)
    List<Person> persons;

    public Group(Integer course, String cipher, String name) {
        this.course = course;
        this.cipher = cipher;
        this.name = name;
    }

    public Group() {
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group){
            Group g = (Group)obj;
            return g.id == id;
        }
        else
            return false;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public String getCipher() {
        return cipher;
    }

    public void setCipher(String cipher) {
        this.cipher = cipher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getPersons() {
        return persons;
    }
}
