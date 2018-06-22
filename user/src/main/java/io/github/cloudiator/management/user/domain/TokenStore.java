package io.github.cloudiator.management.user.domain;

import java.util.Optional;

public interface TokenStore {

  Token storeToken(Token token);

  Optional<Token> retrieveToken(String token);

}
