package com.infernalwhaler.repository;

import com.infernalwhaler.model.Movie;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 16/10/2025
 */

@ApplicationScoped
public class MovieRepository implements PanacheRepository<Movie> {


    public List<Movie> findByCountry(final String country) {
        return list("SELECT m FROM Movie m WHERE m.country = ?1 ORDER BY m.id DESC", country);
    }
}
