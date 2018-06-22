package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import java.util.Optional;

public class MultiUserModeAuthenticationService implements AuthenticationService {

  private final TokenStore tokenStore;
  private final UserStore userStore;
  private final TokenGenerator tokenGenerator;

  @Inject
  public MultiUserModeAuthenticationService(TokenStore tokenStore,
      UserStore userStore,
      TokenGenerator tokenGenerator) {
    this.tokenStore = tokenStore;
    this.userStore = userStore;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Optional<Token> authenticateUser(String email, String password) {
    checkNotNull(email, "email is null");
    checkNotNull(password, "password is null");

    final Optional<User> user = userStore.getUser(email);
    if (!user.isPresent()) {
      return Optional.empty();
    }

    if (!user.get().validatePassword(password)) {
      return Optional.empty();
    }

    return Optional.of(tokenStore.storeToken(tokenGenerator.apply(email)));
  }

  @Override
  public Optional<User> validateToken(Token token) {

    final Optional<Token> retrievedToken = tokenStore.retrieveToken(token.getStringToken());
    if (!retrievedToken.isPresent()) {
      return Optional.empty();
    }

    return userStore.getUser(token.getOwner());
  }
}
