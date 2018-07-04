package io.github.cloudiator.management.encryption;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.cloudiator.management.encryption.config.EncryptionContext;
import io.github.cloudiator.management.encryption.config.EncryptionModule;

public class EncryptionAgent {

  private static final Injector injector = Guice
      .createInjector(new EncryptionModule(new EncryptionContext()));

  public static void main(String[] args) {

    final EncryptionFactory instance = injector.getInstance(EncryptionFactory.class);
    final EncryptionService encryptionService = instance.forUser("admin");

    String test = "blub";

    System.out.println("Unencrypted: " + test);

    String encrypted = encryptionService.encrypt(test);

    System.out.println("Encrypted: " + encrypted);

    String decrypted = encryptionService.decrypt(encrypted);

    System.out.println("Decrypted: " + decrypted);


  }

}
