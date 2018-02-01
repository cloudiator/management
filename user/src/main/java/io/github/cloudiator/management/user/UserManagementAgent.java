package io.github.cloudiator.management.user;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import io.github.cloudiator.management.user.config.UserManagementModule;
import io.github.cloudiator.management.user.messaging.AuthListener;
import io.github.cloudiator.management.user.messaging.CreateTenantListener;
import io.github.cloudiator.management.user.messaging.CreateUserListener;
import io.github.cloudiator.management.user.messaging.LoginListener;
import org.cloudiator.messaging.kafka.KafkaContext;
import org.cloudiator.messaging.kafka.KafkaMessagingModule;

public class UserManagementAgent {

  private final static Injector injector = Guice
      .createInjector(new UserManagementModule(), new KafkaMessagingModule(
          new KafkaContext(Configuration.conf())));

  public static void main(String[] args) {

    injector.getInstance(CreateUserListener.class).run();
    injector.getInstance(CreateTenantListener.class).run();
    injector.getInstance(AuthListener.class).run();
    injector.getInstance(LoginListener.class).run();

  }

}
