package com.infernalwhaler.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 15/10/2025
 */

@Entity
@Table(name = "movies")
public class Movie extends PanacheEntity {


    @Column(length = 100)
    public String title;
    @Column(length = 200)
    public String description;
    public String director;
    public String country;
}
