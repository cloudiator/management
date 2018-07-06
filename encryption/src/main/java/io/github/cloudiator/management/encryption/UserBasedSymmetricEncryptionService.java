package io.github.cloudiator.management.encryption;

import java.util.Base64;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class UserBasedSymmetricEncryptionService implements EncryptionService {

  private final String user;
  private final PasswordStore passwordStore;
  private final SaltGenerator saltGenerator;

  UserBasedSymmetricEncryptionService(
      String user, PasswordStore passwordStore,
      SaltGenerator saltGenerator) {
    this.user = user;
    this.passwordStore = passwordStore;
    this.saltGenerator = saltGenerator;
  }

  private String generatePasswordForUserAndStore() {

    final String generatedPassword = Base64.getEncoder()
        .encodeToString(KeyGenerators.secureRandom(128).generateKey());
    passwordStore.storePassword(user, generatedPassword);
    return generatedPassword;
  }

  private String createOrRetrievePassword() {
    if (passwordStore.retrievePassword(user).isPresent()) {
      return passwordStore.retrievePassword(user).get();
    }
    return generatePasswordForUserAndStore();
  }

  @Override
  public String encrypt(String plaintext) {
    final String salt = saltGenerator.get();
    final String password = createOrRetrievePassword();

    return Encryptors.delux(password, salt).encrypt(plaintext);
  }

  @Override
  public String decrypt(String cipher) {
    final String salt = saltGenerator.get();
    final String password = createOrRetrievePassword();

    return Encryptors.delux(password, salt).decrypt(cipher);
  }
}
