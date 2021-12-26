package com.eduhelp.servicediscipline_members.repo.model;

import javax.persistence.*;

@Entity

@Table(name = "disciplines_users",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "discipline_id" }) })

public final class DisciplineUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long user_id;
    private long discipline_id;

    public DisciplineUser(){

    }

    public DisciplineUser(long user_id, long discipline_id){
        this.user_id = user_id;
        this.discipline_id = discipline_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){this.id = id;}

    public long getUser_id(){
        return user_id;
    }

    public void setUser_id(long user_id){
        this.user_id = user_id;
    }

    public long getDiscipline_id(){
        return discipline_id;
    }

    public void setDiscipline_id(long discipline_id){
        this.discipline_id = discipline_id;
    }



}