package com.infernalwhaler;

import com.infernalwhaler.model.Movie;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/movies")
public class MovieResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        return Response.status(Response.Status.OK).entity(Movie.listAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        return Movie.findByIdOptional(id)
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("country/{country}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByCountry(@PathParam("country") String country) {
        final List<Movie> listCountries = Movie.list("SELECT m FROM Movie m WHERE m.country = ?1 ORDER BY m.id DESC", country);
        return Response.status(Response.Status.OK).entity(listCountries).build();
    }

    @GET
    @Path("title/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByTitle(@PathParam("title") String title) {
        return Movie.find("title", title)
                .singleResultOptional()
                .map(movie -> Response.ok(movie).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Movie movie) {
        Movie.persist(movie);
        return movie.isPersistent() ?
                Response.created(URI.create("/movies/" + movie.id)).build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id) {
        return Movie.deleteById(id) ?
                Response.noContent().build() :
                Response.status(Response.Status.BAD_REQUEST).build();
    }
}
