package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import de.uniulm.omi.cloudiator.util.Password;
import java.util.Base64;
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
  public Optional<User> validateToken(String token) {

    final Optional<Token> retrievedToken = tokenStore.retrieveToken(token);
    if (!retrievedToken.isPresent()) {
      return Optional.empty();
    }

    checkState(retrievedToken.get().getIssuedAt() <= System.currentTimeMillis(),
        "Token was created in the future.");

    //validate if token is still valid
    if (System.currentTimeMillis() > retrievedToken.get().getExpires()) {
      return Optional.empty();
    }

    return userStore.getUser(retrievedToken.get().getOwner());
  }

  @Override
  public Optional<User> getUser(String email) {
    return userStore.getUser(email);
  }

  @Override
  public Optional<Tenant> getTenant(String name) {
    return userStore.getTenant(name);
  }

  @Override
  public User createUser(UserNew newUser) {

    byte[] salt = Password.getInstance().generateSalt();
    String encodedSalt = Base64.getEncoder().encodeToString(salt);
    String hashed = new String(
        Password.getInstance().hash(newUser.getPassword().toCharArray(), salt));

    userStore.storeUser(new User(newUser.getEmail(), hashed, encodedSalt, newUser.getTenant()));

    return null;
  }

  @Override
  public Tenant createTenant(String name) {
    final Tenant tenant = new Tenant(name);
    userStore.storeTenant(tenant);
    return tenant;
  }

  @Override
  public Iterable<User> retrieveUsers() {
    return userStore.retrieveUsers();
  }

  @Override
  public Iterable<Tenant> retrieveTenants() {
    return userStore.retrieveTenants();
  }
}
