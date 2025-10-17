package com.infernalwhaler.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 17/10/2025
 */

@QuarkusTest
class MovieRepositoryTest {

    @Inject
    MovieRepository movieRepository;


    @Test
    void findByCountry() {
        var movies = movieRepository.findByCountry("USA");
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertEquals(2, movies.size());
        assertEquals(20L, movies.getFirst().getId());
        assertEquals("USA", movies.getFirst().getCountry());
        assertEquals("Jurassic Parc 5", movies.getFirst().getTitle());
        assertEquals("Spielberg", movies.getFirst().getDirector());
        assertEquals("Action", movies.getFirst().getDescription());
    }

    @Test
    void findByCountry_NOK() {
        var movies = movieRepository.findByCountry("Planet");
        assertNotNull(movies);
        assertTrue(movies.isEmpty());
    }

}

