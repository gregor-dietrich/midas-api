package de.vptr.midas.api.security;

import java.time.LocalDateTime;

import org.eclipse.microprofile.context.ManagedExecutor;

import de.vptr.midas.api.rest.entity.User;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    @Inject
    ManagedExecutor executor;

    @Inject
    EntityManager entityManager;

    @Inject
    PasswordHashingService passwordHashingService;

    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(final UsernamePasswordAuthenticationRequest request,
            final AuthenticationRequestContext context) {

        final String username = request.getUsername();
        final String password = new String(request.getPassword().getPassword());

        return Uni.createFrom().item(() -> this.authenticateUser(username, password)).runSubscriptionOn(this.executor);
    }

    @Transactional
    SecurityIdentity authenticateUser(final String username, final String password) {
        final User user = User.find("username = ?1", username).firstResult();

        if (user == null || !this.passwordHashingService.verifyPassword(password, user.password, user.salt)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        if (user.banned) {
            throw new AuthenticationFailedException("User is banned");
        }

        if (!user.activated) {
            throw new AuthenticationFailedException("User is not activated");
        }

        // Update lastLogin without triggering validation
        this.entityManager.createQuery("UPDATE User u SET u.lastLogin = :now WHERE u.id = :id")
                .setParameter("now", LocalDateTime.now())
                .setParameter("id", user.id)
                .executeUpdate();

        return QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal(username))
                .build();
    }
}
