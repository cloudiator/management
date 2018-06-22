package io.github.cloudiator.management.user.domain;

import java.util.Optional;

public interface AuthenticationService {

  Optional<Token> authenticateUser(String email, String password);

  Optional<User> validateToken(Token token);


}
