package io.github.cloudiator.management.user.config;

import com.google.inject.AbstractModule;
import io.github.cloudiator.management.user.Init;

public class UserManagementModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Init.class).asEagerSingleton();
  }
}
