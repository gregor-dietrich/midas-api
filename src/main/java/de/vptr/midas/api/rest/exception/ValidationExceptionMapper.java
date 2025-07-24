package de.vptr.midas.api.rest.exception;

import de.vptr.midas.api.rest.util.ResponseUtil;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

    @Override
    public Response toResponse(final ValidationException exception) {
        return ResponseUtil.unprocessable(new ErrorResponse("Validation failed", exception.getMessage()));
    }

    public static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(final String error, final String message) {
            this.error = error;
            this.message = message;
        }
    }
}
