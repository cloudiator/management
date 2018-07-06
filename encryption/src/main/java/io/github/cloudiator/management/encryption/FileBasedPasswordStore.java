package io.github.cloudiator.management.encryption;

import com.google.inject.Inject;
import io.github.cloudiator.management.encryption.storage.KeyValueStore;
import io.github.cloudiator.management.encryption.storage.KeyValueStoreFactory;
import java.util.Optional;
import javax.inject.Named;

public class FileBasedPasswordStore implements PasswordStore {

  public final static String PASSWORD_STORE_DIRECTORY_CONFIG = "password.directory";
  private final KeyValueStore keyValueStore;

  @Inject
  FileBasedPasswordStore(@Named(PASSWORD_STORE_DIRECTORY_CONFIG) String directory,
      KeyValueStoreFactory keyValueStoreFactory) {
    this.keyValueStore = keyValueStoreFactory.fileBased(directory);
  }


  @Override
  public String storePassword(String user, String password) {
    keyValueStore.store(user, password);
    return password;
  }

  @Override
  public Optional<String> retrievePassword(String user) {
    return keyValueStore.retrieve(user);
  }
}
