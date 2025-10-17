package com.infernalwhaler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infernalwhaler.model.Movie;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * @author Sdeseure
 * @project quarkus-hibernate-orm-panache
 * @date 16/10/2025
 */

@QuarkusTest
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieResourceTestIT {


    @Inject
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    void getAll() {
        given()
                .when()
                .get("/api/movies")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("id", hasItems(10, 20))
                .body("[0].title", is("Kill Bill 5"))
                .body("title", hasItems("Kill Bill 5", "Jurassic Parc 5"))
                .body("description", hasItem("Action"))
                .body("[1].director", is("Spielberg"))
                .body("director", hasItems("Spielberg", "Tarantino"))
                .body("country", hasItem("USA"));
    }

    @Test
    @Order(1)
    void findById() {
        given()
                .pathParam("id", 10)
                .when()
                .get("/api/movies/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("title", is("Kill Bill 5"))
                .body("description", is("Action"))
                .body("director", is("Tarantino"))
                .body("country", is("USA"));
    }

    @Test
    @Order(1)
    void findById_NOK() {
        given()
                .pathParam("id", 500)
                .when()
                .get("/api/movies/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(1)
    void findByCountry() {
        given()
                .pathParam("country", "USA")
                .when()
                .get("/api/movies/country/{country}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("title", hasItems("Kill Bill 5", "Jurassic Parc 5"))
                .body("description", hasItem("Action"))
                .body("[1].director", is("Tarantino"))
                .body("director", hasItems("Spielberg", "Tarantino"));
    }

    @Test
    @Order(1)
    void findByCountry_NOK() {
        given()
                .pathParam("country", "PLANET")
                .when()
                .get("/api/movies/country/{country}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }

    @Test
    @Order(1)
    void findByTitle() {
        given()
                .pathParam("title", "Kill Bill 5")
                .when()
                .get("/api/movies/title/{title}")
                .then()
                .statusCode(200)
                .body("title", is("Kill Bill 5"))
                .body("description", is("Action"))
                .body("director", is("Tarantino"))
                .body("country", is("USA"));
    }

    @Test
    @Order(1)
    void findByTitle_NOK() {
        given()
                .pathParam("title", "NOT EXISTING")
                .when()
                .get("/api/movies/title/{title}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void create() throws JsonProcessingException {
        var movie = new Movie("The killers", "Action", "Ted Bundy", "USA");

        given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper.writeValueAsString(movie))
                .when()
                .post("/api/movies")
                .then()
                .statusCode(201);

        given()
                .pathParam("title", movie.getTitle())
                .when()
                .get("/api/movies/title/{title}")
                .then()
                .statusCode(200)
                .body("title", is("The killers"))
                .body("description", is("Action"))
                .body("director", is("Ted Bundy"))
                .body("country", is("USA"));
    }

    @Test
    @Order(3)
    void updateMovieById() throws JsonProcessingException {
        var movie = new Movie("The killers", "Action", "Ted Bundy", "USA");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParam("id", 1)
                .body(objectMapper.writeValueAsString(movie))
                .when()
                .put("/api/movies/{id}")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", is("The killers"))
                .body("description", is("Action"))
                .body("director", is("Ted Bundy"))
                .body("country", is("USA"));
    }

    @Test
    @Order(3)
    void updateMovieById_NOK() throws JsonProcessingException {
        var movie = new Movie("The killers", "Action", "Ted Bundy", "USA");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .pathParam("id", 100)
                .body(objectMapper.writeValueAsString(movie))
                .when()
                .put("/api/movies/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    void deleteById() {
        given()
                .pathParam("id", 1)
                .when()
                .delete("/api/movies/{id}")
                .then()
                .statusCode(204);

        given()
                .pathParam("id", 1)
                .when()
                .get("/api/movies/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    void deleteById_NOK() {
        given()
                .pathParam("id", 100)
                .when()
                .delete("/api/movies/{id}")
                .then()
                .statusCode(400);
    }
}