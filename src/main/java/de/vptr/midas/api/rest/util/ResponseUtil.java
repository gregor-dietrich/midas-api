package de.vptr.midas.api.rest.util;

import java.util.Optional;

import jakarta.ws.rs.core.Response;

public class ResponseUtil {
    private ResponseUtil() {
    }

    public static Response ok() {
        return Response.status(Response.Status.OK).build();
    }

    public static <T> Response ok(final T entity) {
        return entity == null ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.OK).entity(entity).build();
    }

    public static <T> Response okOrNotFound(final Optional<T> entity) {
        return entity.map(Response::ok)
                .map(Response.ResponseBuilder::build)
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    public static <T> Response created(final T entity) {
        return Response.status(Response.Status.CREATED).entity(entity).build();
    }

    public static Response notFound() {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
