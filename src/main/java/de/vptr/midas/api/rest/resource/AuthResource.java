package de.vptr.midas.api.rest.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vptr.midas.api.rest.util.ResponseUtil;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {

    private static final Logger LOG = LoggerFactory.getLogger(AuthResource.class);

    @HEAD
    @Authenticated
    public Response validateAuth() {
        LOG.trace("validateCredentials() method called");
        return ResponseUtil.ok();
    }
}
