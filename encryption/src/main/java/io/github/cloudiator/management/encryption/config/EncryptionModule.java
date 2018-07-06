package io.github.cloudiator.management.encryption.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.github.cloudiator.management.encryption.FileBasedPasswordStore;
import io.github.cloudiator.management.encryption.PasswordStore;
import io.github.cloudiator.management.encryption.SaltGenerator;
import io.github.cloudiator.management.encryption.SaltGeneratorImpl;

public class EncryptionModule extends AbstractModule {

  private final EncryptionContext encryptionContext;

  public EncryptionModule(EncryptionContext encryptionContext) {
    this.encryptionContext = encryptionContext;
  }

  @Override
  protected void configure() {
    bindConstant()
        .annotatedWith(Names.named(FileBasedPasswordStore.PASSWORD_STORE_DIRECTORY_CONFIG))
        .to(encryptionContext.passwordDirectory());
    bindConstant().annotatedWith(Names.named(SaltGeneratorImpl.SALT_GENERATOR_DIRECTORY_CONFIG))
        .to(encryptionContext.saltDirectory());
    bind(PasswordStore.class).to(FileBasedPasswordStore.class);
    bind(SaltGenerator.class).to(SaltGeneratorImpl.class);
  }
}
