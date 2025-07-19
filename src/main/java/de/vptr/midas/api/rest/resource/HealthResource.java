package de.vptr.midas.api.rest.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vptr.midas.api.rest.util.ResponseUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/health")
public class HealthResource {

    private static final Logger LOG = LoggerFactory.getLogger(HealthResource.class);

    @HEAD
    @PermitAll
    public Response healthCheck() {
        LOG.trace("healthCheck() method called");
        return ResponseUtil.ok();
    }
}
