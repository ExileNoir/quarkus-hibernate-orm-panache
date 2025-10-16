package com.infernalwhaler;

import com.infernalwhaler.model.Movie;
import com.infernalwhaler.repository.MovieRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieRepository movieRepository;


    @GET
    public Response getAll() {
        return Response.status(Response.Status.OK).entity(movieRepository.listAll()).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        return movieRepository.findByIdOptional(id)
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("country/{country}")
    public Response findByCountry(@PathParam("country") String country) {
        final List<Movie> listCountries = movieRepository.findByCountry(country);
        return Response.status(Response.Status.OK).entity(listCountries).build();
    }

    @GET
    @Path("title/{title}")
    public Response findByTitle(@PathParam("title") String title) {
        return movieRepository.find("title", title)
                .singleResultOptional()
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    public Response create(Movie movie) {
        movieRepository.persist(movie);
        return movieRepository.isPersistent(movie) ?
                Response.created(URI.create("/movies/" + movie.getId())).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id) {
        return movieRepository.deleteById(id) ?
                Response.noContent().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}
