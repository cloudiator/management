package io.github.cloudiator.management.user.config;

import com.google.inject.AbstractModule;
import io.github.cloudiator.management.user.Init;
import io.github.cloudiator.management.user.domain.AuthService;

public class UserManagementModule extends AbstractModule {

  private final AuthService authService;

  public UserManagementModule(){
    this.authService = new AuthService();
  }

  @Override
  protected void configure() {
    bind(Init.class).asEagerSingleton();
  }
}
