package com.infernalwhaler;

import com.infernalwhaler.model.Movie;
import com.infernalwhaler.repository.MovieRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 17/10/2025
 */

@QuarkusTest
class MovieResourceTest {

    @InjectMock
    MovieRepository movieRepository;

    @Inject
    MovieResource movieResource;

    private Movie movie;
    private Movie movie2;


    @BeforeEach
    void setUp() {
        movie = new Movie("Star Wars", "Sci-Fi", "Lucas", "USA");
        movie.setId(1L);

        movie2 = new Movie("Apocalypse Now", "Psychological Drama", "Coppola", "USA");
        movie2.setId(2L);
    }

    @Test
    void getAll() {
        Mockito.when(movieRepository.listAll())
                .thenReturn(List.of(movie, movie2));

        final Response response = movieResource.getAll();
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        final List<Movie> movieEntities = (List<Movie>) response.getEntity();
        assertNotNull(movieEntities);
        assertFalse(movieEntities.isEmpty());
        assertEquals(2, movieEntities.size());

        assertEquals(1L, movieEntities.getFirst().getId());
        assertEquals(movie.getTitle(), movieEntities.getFirst().getTitle());
        assertEquals(movie.getDescription(), movieEntities.getFirst().getDescription());
        assertEquals(movie.getDirector(), movieEntities.getFirst().getDirector());
        assertEquals(movie.getCountry(), movieEntities.getFirst().getCountry());

        assertEquals(2, movieEntities.get(1).getId());
        assertEquals(movie2.getTitle(), movieEntities.get(1).getTitle());
        assertEquals(movie2.getDescription(), movieEntities.get(1).getDescription());
        assertEquals(movie2.getDirector(), movieEntities.get(1).getDirector());
        assertEquals(movie2.getCountry(), movieEntities.get(1).getCountry());
    }

    @Test
    void findById() {
        Mockito.when(movieRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(movie));

        final Response response = movieResource.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        final Movie movieEntity = (Movie) response.getEntity();
        assertNotNull(movieEntity);
        assertEquals(movie.getTitle(), movieEntity.getTitle());
        assertEquals(movie.getDescription(), movieEntity.getDescription());
        assertEquals(movie.getDirector(), movieEntity.getDirector());
        assertEquals(movie.getCountry(), movieEntity.getCountry());
    }

    @Test
    void findById_NOK() {
        Mockito.when(movieRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        final Response response = movieResource.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void findByCountry() {
        Mockito.when(movieRepository.findByCountry("USA"))
                .thenReturn(List.of(movie, movie2));

        final Response response = movieResource.findByCountry("USA");
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        final List<Movie> movieEntities = (List<Movie>) response.getEntity();
        assertNotNull(movieEntities);
        assertFalse(movieEntities.isEmpty());
        assertEquals(1L, movieEntities.getFirst().getId());
        assertEquals(movie.getTitle(), movieEntities.getFirst().getTitle());
        assertEquals(movie.getDescription(), movieEntities.getFirst().getDescription());
        assertEquals(movie.getDirector(), movieEntities.getFirst().getDirector());
        assertEquals(movie.getCountry(), movieEntities.getFirst().getCountry());
    }

    @Test
    void findByTitle() {
        final PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        Mockito.when(query.page(Mockito.any()))
                .thenReturn(query);
        Mockito.when(query.singleResultOptional())
                .thenReturn(Optional.of(movie2));

        Mockito.when(movieRepository.find("title", "Apocalypse Now"))
                .thenReturn(query);

        final Response response = movieResource.findByTitle("Apocalypse Now");
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        final Movie movieEntity = (Movie) response.getEntity();
        assertNotNull(movieEntity);
        assertEquals("Apocalypse Now", movieEntity.getTitle());
    }

    @Test
    void findByTitle_NOK() {
        final PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
        Mockito.when(query.page(Mockito.any()))
                .thenReturn(query);
        Mockito.when(query.singleResultOptional())
                .thenReturn(Optional.empty());

        Mockito.when(movieRepository.find("title", "Apocalypse Now"))
                .thenReturn(query);

        final Response response = movieResource.findByTitle("Apocalypse Now");
        assertNotNull(response);
        assertEquals(404, response.getStatus());
    }

    @Test
    void create() {
        var newMovie = new Movie("Star Wars A New Hope", "Epic, Dystopian Sci-Fi", "Lucas", "USA");

        Mockito.doNothing()
                .when(movieRepository)
                .persist(Mockito.any(Movie.class));

        Mockito.when(movieRepository.isPersistent(Mockito.any(Movie.class)))
                .thenReturn(true);

        final Response response = movieResource.create(newMovie);
        assertNotNull(response);
        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertNull(response.getEntity());
    }

    @Test
    void create_NOK() {
        Mockito.doNothing()
                .when(movieRepository)
                .persist(Mockito.any(Movie.class));

        Mockito.when(movieRepository.isPersistent(Mockito.any(Movie.class)))
                .thenReturn(false);

        final Response response = movieResource.create(movie);
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    void updateMovieById() {
        var updatedMovie = new Movie("Star Wars A New Hope", "Epic, Dystopian Sci-Fi", "Lucas", "USA");

        Mockito.when(movieRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(movie));

        final Response response = movieResource.updateMovieById(1L, updatedMovie);
        assertNotNull(response);
        assertEquals(200, response.getStatus());

        final Movie movieEntity = (Movie) response.getEntity();
        assertNotNull(movieEntity);
        assertEquals(1L, movieEntity.getId());
        assertEquals(updatedMovie.getTitle(), movieEntity.getTitle());
        assertEquals(updatedMovie.getDescription(), movieEntity.getDescription());
        assertEquals(updatedMovie.getDirector(), movieEntity.getDirector());
        assertEquals(updatedMovie.getCountry(), movieEntity.getCountry());
    }

    @Test
    void updateMovieById_NOK() {
        Mockito.when(movieRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        final Response response = movieResource.updateMovieById(1L, new Movie());
        assertNotNull(response);
        assertEquals(404, response.getStatus());
    }

    @Test
    void deleteById() {
        Mockito.when(movieRepository.deleteById(1L))
                .thenReturn(true);

        final Response response = movieResource.deleteById(1L);
        assertNotNull(response);
        assertEquals(204, response.getStatus());
    }

    @Test
    void deleteById_NOK() {
        Mockito.when(movieRepository.deleteById(1L))
                .thenReturn(false);

        final Response response = movieResource.deleteById(1L);
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }
}