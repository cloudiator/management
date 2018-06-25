package io.github.cloudiator.management.user.domain;

import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import java.util.Collections;
import java.util.Optional;

public class SingleUserModeAuthenticationService implements AuthenticationService {

  private static final Tenant SINGLE_TENANT = new Tenant("admin");
  private static final User SINGLE_USER = new User("john.doe@example.com", "admin", "salt",
      SINGLE_TENANT);
  private static final Token STATIC_TOKEN = new Token(Configuration.conf().getString("auth.token"),
      SINGLE_USER.email(), Long.MIN_VALUE, Long.MAX_VALUE);

  @Override
  public Optional<Token> authenticateUser(String email, String password) {
    return Optional.of(STATIC_TOKEN);
  }

  @Override
  public Optional<User> validateToken(Token token) {

    if (STATIC_TOKEN.getStringToken().equals(token.getStringToken())) {
      return Optional.of(SINGLE_USER);
    }

    return Optional.empty();
  }

  @Override
  public Optional<User> getUser(String email) {
    if (email.equals(SINGLE_USER.email())) {
      return Optional.of(SINGLE_USER);
    }
    return Optional.empty();
  }

  @Override
  public Optional<Tenant> getTenant(String name) {
    if (name.equals(SINGLE_TENANT.getName())) {
      return Optional.of(SINGLE_TENANT);
    }
    return Optional.empty();
  }

  @Override
  public void createUser(UserNew newUser) {
    throw new UnsupportedOperationException(
        String.format("%s does not support creating users", this));
  }

  @Override
  public Iterable<User> retrieveUsers() {
    return Collections.singleton(SINGLE_USER);
  }

  @Override
  public Iterable<Tenant> retrieveTenants() {
    return Collections.singleton(SINGLE_TENANT);
  }
}
