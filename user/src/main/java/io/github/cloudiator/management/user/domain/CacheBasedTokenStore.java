package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static io.github.cloudiator.management.user.config.AuthContext.TOKEN_VALID;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class CacheBasedTokenStore implements TokenStore {

  private final Cache<String, Token> tokens;

  @Inject
  public CacheBasedTokenStore(@Named(TOKEN_VALID) long tokenValidity) {
    checkArgument(tokenValidity > 0, "tokenValidity needs to be larger than zero.");
    this.tokens = CacheBuilder.newBuilder().expireAfterWrite(tokenValidity, TimeUnit.MILLISECONDS)
        .build();
  }

  @Override
  public Token storeToken(Token token) {
    tokens.put(token.getStringToken(), token);
    return token;
  }

  @Override
  public Optional<Token> retrieveToken(String token) {
    return Optional.ofNullable(tokens.getIfPresent(token));
  }
}
