package com.twobomb.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "discipline_control")
public class ControlDiscipline extends AbstractEntity{

    @Column(name = "date_begin")
    Date dateBegin;

    @Column(name = "date_end")
    Date dateEnd;

    public ControlDiscipline(Date dateBegin, Date dateEnd, Boolean is_autoset, Boolean is_student_change, Boolean is_student_offer, Discipline discipline) {

        this.dateBegin = dateBegin;
        if(dateBegin.after(dateEnd)){
            try {
                throw new Exception("Date begin after date end!");
            }
            catch (Exception e){
                e.printStackTrace();
                this.dateEnd = dateBegin;
            }
        }
        else
            this.dateEnd = dateEnd;
        this.is_autoset = is_autoset;
        this.is_student_change = is_student_change;
        this.is_student_offer = is_student_offer;
        this.discipline = discipline;
    }

    @Type(type = "boolean")

    @Column(name = "is_autoset")
    Boolean is_autoset;

    @Column(name = "is_student_change")
    Boolean is_student_change;

    @Column(name = "is_student_offer")
    Boolean is_student_offer;

    @JoinColumn(name = "id_discipline_load")
    @OneToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    Discipline discipline;

    public ControlDiscipline() {
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public Date getDateBegin() {
        return dateBegin;
    }

    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Boolean getIs_autoset() {
        return is_autoset;
    }

    public void setIs_autoset(Boolean is_autoset) {
        this.is_autoset = is_autoset;
    }

    public Boolean getIs_student_change() {
        return is_student_change;
    }

    public void setIs_student_change(Boolean is_student_change) {
        this.is_student_change = is_student_change;
    }

    public Boolean getIs_student_offer() {
        return is_student_offer;
    }

    public void setIs_student_offer(Boolean is_student_offer) {
        this.is_student_offer = is_student_offer;
    }
}
