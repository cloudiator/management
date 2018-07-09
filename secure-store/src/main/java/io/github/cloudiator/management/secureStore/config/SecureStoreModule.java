package io.github.cloudiator.management.secureStore.config;

import com.google.inject.AbstractModule;
import io.github.cloudiator.management.secureStore.Init;

public class SecureStoreModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Init.class).asEagerSingleton();
  }
}
