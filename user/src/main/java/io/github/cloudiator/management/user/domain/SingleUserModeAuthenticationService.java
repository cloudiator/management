package io.github.cloudiator.management.user.domain;

import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import java.util.Optional;

public class SingleUserModeAuthenticationService implements AuthenticationService {

  private static final User SINGLE_USER = new User("john.doe@example.com", "admin", "salt",
      new Tenant("admin"));
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
}
