package com.infernalwhaler;

import com.infernalwhaler.model.Movie;
import com.infernalwhaler.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Movie REST Endpoint")
public class MovieResource {

    @Inject
    MovieRepository movieRepository;


    @GET
    @Operation(summary = "Get all movies", description = "Find all Movies from the db")
    public Response getAll() {
        return Response.status(Response.Status.OK).entity(movieRepository.listAll()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a movies by id", description = "Find a movies by id from the db")
    public Response findById(@Parameter(description = "Movie id")
                             @PathParam("id") Long id) {
        return movieRepository.findByIdOptional(id)
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("country/{country}")
    @Operation(summary = "Get a movies by country", description = "Find a movies by country from the db")
    public Response findByCountry(@Parameter(description = "Movie country")
                                  @PathParam("country") String country) {
        final List<Movie> listCountries = movieRepository.findByCountry(country);
        return Response.status(Response.Status.OK).entity(listCountries).build();
    }

    @GET
    @Path("title/{title}")
    @Operation(summary = "Get a movies by title", description = "Find a movies by title from the db")
    public Response findByTitle(@Parameter(description = "Movie title")
                                @PathParam("title") String title) {
        return movieRepository.find("title", title)
                .singleResultOptional()
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Operation(summary = "Add a new movie", description = "Add a new movie to the db")
    public Response create(@RequestBody(description = "Movie to create") Movie movie) {
        movieRepository.persist(movie);
        return movieRepository.isPersistent(movie) ?
                Response.created(URI.create("/movies/" + movie.getId())).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateMovie(@Parameter(description = "Movie id to update") @PathParam("id") Long id,
                                @RequestBody(description = "Movie to update") Movie movie) {
        return movieRepository.updateMovie(id, movie) > 0 ?
                Response.ok(movie).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a movie", description = "Delete an existing movie from the db")
    public Response deleteById(@Parameter(description = "Movie id")
                               @PathParam("id") Long id) {
        return movieRepository.deleteById(id) ?
                Response.noContent().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}
