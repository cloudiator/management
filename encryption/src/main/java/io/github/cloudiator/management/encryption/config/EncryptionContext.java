package io.github.cloudiator.management.encryption.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;

public class EncryptionContext {

  private final Config config;

  public EncryptionContext(Config config) {
    this.config = config;
    config.checkValid(ConfigFactory.defaultReference(), new String[]{"encryption"});
  }

  public EncryptionContext() {
    this(Configuration.conf());
  }

  public String saltDirectory() {
    return this.config.getString("encryption.salt.directory");
  }

  public String passwordDirectory() {
    return this.config.getString("encryption.password.directory");
  }

}
