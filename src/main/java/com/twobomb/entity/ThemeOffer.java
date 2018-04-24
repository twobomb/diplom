package com.twobomb.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "themes_offer")
public class ThemeOffer extends AbstractEntity{

    @Column(name="description")
    String description;

    @Column(name = "text")
    @Type(type = "text")
    String text;

    @Column(name = "date")
    Date date;


    @JoinColumn(name = "id_person_student")
    @OneToOne(cascade = CascadeType.ALL)
    Person person;
}
