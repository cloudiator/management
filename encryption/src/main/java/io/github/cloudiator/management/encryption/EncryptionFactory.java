package io.github.cloudiator.management.encryption;

import com.google.inject.Inject;

public class EncryptionFactory {

  private final PasswordStore passwordStore;
  private final SaltGenerator saltGenerator;

  @Inject
  EncryptionFactory(PasswordStore passwordStore, SaltGenerator saltGenerator) {
    this.passwordStore = passwordStore;
    this.saltGenerator = saltGenerator;
  }

  public EncryptionService forUser(String user) {
    return new UserBasedSymmetricEncryptionService(user, passwordStore, saltGenerator);
  }


}
