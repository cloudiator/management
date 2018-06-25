package io.github.cloudiator.management.user.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class AuthContext {

  public static final String TOKEN_VALID = "tokenValid";
  private final Config config;

  public AuthContext(Config config) {
    this.config = config;
    config.checkValid(ConfigFactory.defaultReference(), "auth");
  }

  public AuthMode authMode() {
    return AuthMode.valueOf(config.getString("auth.mode"));
  }

  public long tokenValidity() {
    return config.getLong("auth.tokenValidity");
  }

}
