package io.github.cloudiator.management.user.config;

import static io.github.cloudiator.management.user.config.AuthContext.TOKEN_VALID;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import io.github.cloudiator.management.user.Init;
import io.github.cloudiator.management.user.domain.AuthenticationService;
import io.github.cloudiator.management.user.domain.CacheBasedTokenStore;
import io.github.cloudiator.management.user.domain.CreateAdminUserAndTenant;
import io.github.cloudiator.management.user.domain.DatabaseBasedUserStore;
import io.github.cloudiator.management.user.domain.MultiUserModeAuthenticationService;
import io.github.cloudiator.management.user.domain.SingleUserModeAuthenticationService;
import io.github.cloudiator.management.user.domain.TokenGenerator;
import io.github.cloudiator.management.user.domain.TokenGeneratorImpl;
import io.github.cloudiator.management.user.domain.TokenStore;
import io.github.cloudiator.management.user.domain.UserStore;
import io.github.cloudiator.management.user.messaging.AuthListener;
import io.github.cloudiator.management.user.messaging.CreateTenantListener;
import io.github.cloudiator.management.user.messaging.CreateUserListener;
import io.github.cloudiator.management.user.messaging.KafkaListener;
import io.github.cloudiator.management.user.messaging.LoginListener;
import io.github.cloudiator.management.user.messaging.TenantQueryListener;

public class UserManagementModule extends AbstractModule {

  private final AuthContext authContext;

  public UserManagementModule(AuthContext authContext) {
    this.authContext = authContext;
  }

  @Override
  protected void configure() {
    bind(Init.class).asEagerSingleton();
    bind(TokenGenerator.class).to(TokenGeneratorImpl.class);
    bindConstant().annotatedWith(Names.named(TOKEN_VALID)).to(authContext.tokenValidity());

    switch (authContext.authMode()) {
      case MULTI:
        install(new MultiUserMode());
        break;
      case SINGLE:
        install(new SingleUserMode());
        break;
      default:
        throw new IllegalStateException("Unknown auth mode " + authContext.authMode());
    }

  }

  private class SingleUserMode extends AbstractModule {

    @Override
    protected void configure() {
      bind(AuthenticationService.class).to(SingleUserModeAuthenticationService.class);

      Multibinder<KafkaListener> kafkaListenerBinder = Multibinder
          .newSetBinder(binder(), KafkaListener.class);
      kafkaListenerBinder.addBinding().to(AuthListener.class);
      kafkaListenerBinder.addBinding().to(TenantQueryListener.class);


    }
  }

  private class MultiUserMode extends AbstractModule {

    @Override
    protected void configure() {
      bind(AuthenticationService.class).to(MultiUserModeAuthenticationService.class);
      bind(TokenStore.class).to(CacheBasedTokenStore.class);
      bind(UserStore.class).to(DatabaseBasedUserStore.class);
      bind(CreateAdminUserAndTenant.class).asEagerSingleton();

      Multibinder<KafkaListener> kafkaListenerBinder = Multibinder
          .newSetBinder(binder(), KafkaListener.class);
      kafkaListenerBinder.addBinding().to(AuthListener.class);
      kafkaListenerBinder.addBinding().to(CreateTenantListener.class);
      kafkaListenerBinder.addBinding().to(CreateUserListener.class);
      kafkaListenerBinder.addBinding().to(LoginListener.class);
      kafkaListenerBinder.addBinding().to(TenantQueryListener.class);

    }
  }

}
