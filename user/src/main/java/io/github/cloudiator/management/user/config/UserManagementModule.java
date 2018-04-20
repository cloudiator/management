package io.github.cloudiator.management.user.config;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import io.github.cloudiator.management.user.Init;
import io.github.cloudiator.management.user.domain.AuthService;
import io.github.cloudiator.persistance.UserDomainRepository;

public class UserManagementModule extends AbstractModule {

  public UserManagementModule(){
  }

  @Override
  protected void configure() {
    bind(Init.class).asEagerSingleton();
  }
}
