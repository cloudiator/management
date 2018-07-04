package io.github.cloudiator.management.encryption;

import java.util.Optional;

public interface PasswordStore {

  String storePassword(String user, String password);

  Optional<String> retrievePassword(String user);

}
