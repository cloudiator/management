package io.github.cloudiator.management.user.domain;

import static io.github.cloudiator.management.user.config.AuthContext.TOKEN_VALID;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.util.Password;
import javax.inject.Named;

public class TokenGeneratorImpl implements TokenGenerator {

  private static final Password PASSWORD_UTIL = Password.getInstance();
  private final long tokenValidity;

  @Inject
  public TokenGeneratorImpl(@Named(TOKEN_VALID) long tokenValidity) {
    this.tokenValidity = tokenValidity;
  }

  @Override
  public Token apply(String user) {
    long now = System.currentTimeMillis();
    return new Token(PASSWORD_UTIL.generateToken(), user, now, now + tokenValidity);
  }
}
