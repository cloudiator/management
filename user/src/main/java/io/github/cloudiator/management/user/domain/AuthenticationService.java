package io.github.cloudiator.management.user.domain;

import java.util.Optional;

public interface AuthenticationService {

  Optional<Token> authenticateUser(String email, String password);

  Optional<User> validateToken(Token token);

  Optional<User> getUser(String email);

  Optional<Tenant> getTenant(String name);

  User createUser(UserNew newUser);

  Tenant createTenant(String name);

  Iterable<User> retrieveUsers();

  Iterable<Tenant> retrieveTenants();


}
