package com.eduhelp.servicedisciplines.repo.model;


import javax.persistence.*;

@Entity
@Table(name = "disciplines")
public final class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String description;

    public Discipline(){

    }

    public Discipline(String title, String description){
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){this.id = id;}

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }



}
