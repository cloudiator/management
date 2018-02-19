package io.github.cloudiator.management.user;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import io.github.cloudiator.management.user.config.JpaModule;
import io.github.cloudiator.management.user.config.UserManagementModule;
import io.github.cloudiator.util.JpaContext;
import io.github.cloudiator.management.user.messaging.AuthListener;
import io.github.cloudiator.management.user.messaging.CreateTenantListener;
import io.github.cloudiator.management.user.messaging.CreateUserListener;
import io.github.cloudiator.management.user.messaging.LoginListener;
import org.cloudiator.messaging.kafka.KafkaContext;
import org.cloudiator.messaging.kafka.KafkaMessagingModule;

public class UserManagementAgent {

  private final static Injector injector = Guice
      .createInjector(new UserManagementModule(), new KafkaMessagingModule(
              new KafkaContext(Configuration.conf())),
          new JpaModule("defaultPersistenceUnit", new JpaContext(
              Configuration.conf())));


  public static void main(String[] args) {

    final CreateUserListener userListener = injector.getInstance(CreateUserListener.class);
    userListener.run();
    final CreateTenantListener tenantListener = injector.getInstance(CreateTenantListener.class);
    tenantListener.run();
    final AuthListener authListener = injector.getInstance(AuthListener.class);
    authListener.run();
    final LoginListener loginListener = injector.getInstance(LoginListener.class);
    loginListener.run();


  }

}
