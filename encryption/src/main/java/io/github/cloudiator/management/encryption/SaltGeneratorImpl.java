package io.github.cloudiator.management.encryption;

import com.google.inject.Inject;
import io.github.cloudiator.management.encryption.storage.KeyValueStore;
import io.github.cloudiator.management.encryption.storage.KeyValueStoreFactory;
import javax.inject.Named;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class SaltGeneratorImpl implements SaltGenerator {

  public final static String SALT_GENERATOR_DIRECTORY_CONFIG = "salt.directory";
  private final static String SALT_KEY = "salt";
  private KeyValueStore keyValueStore;

  @Inject
  SaltGeneratorImpl(@Named(SALT_GENERATOR_DIRECTORY_CONFIG) String directory,
      KeyValueStoreFactory keyValueStoreFactory) {
    this.keyValueStore = keyValueStoreFactory.fileBased(directory);
  }

  @Override
  public String get() {

    if (keyValueStore.retrieve(SALT_KEY).isPresent()) {
      return keyValueStore.retrieve(SALT_KEY).get();
    }

    final String salt = KeyGenerators.string().generateKey();
    keyValueStore.store(SALT_KEY, salt);

    return salt;
  }
}
