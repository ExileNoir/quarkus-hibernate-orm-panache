package com.infernalwhaler.model;

import jakarta.persistence.*;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 15/10/2025
 */

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 200)
    private String description;
    private String director;
    private String country;

    public Movie(String title, String description, String director, String country) {
        this.title = title;
        this.description = description;
        this.director = director;
        this.country = country;
    }

    public Movie() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
