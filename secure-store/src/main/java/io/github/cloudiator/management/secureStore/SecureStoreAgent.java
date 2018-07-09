package io.github.cloudiator.management.secureStore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uniulm.omi.cloudiator.util.configuration.Configuration;
import io.github.cloudiator.management.secureStore.config.SecureStoreModule;
import io.github.cloudiator.management.secureStore.messaging.RetrieveRequestSubscriber;
import io.github.cloudiator.management.secureStore.messaging.StoreRequestSubscriber;
import io.github.cloudiator.persistance.JpaModule;
import io.github.cloudiator.util.JpaContext;
import org.cloudiator.messaging.kafka.KafkaContext;
import org.cloudiator.messaging.kafka.KafkaMessagingModule;
import org.cloudiator.messaging.services.MessageServiceModule;

public class SecureStoreAgent {

  private static final Injector INJECTOR = Guice
      .createInjector(new SecureStoreModule(), new MessageServiceModule(),
          new KafkaMessagingModule(new KafkaContext()),
          new JpaModule("defaultPersistenceUnit", new JpaContext(
              Configuration.conf())));

  public static void main(String[] args) {
    INJECTOR.getInstance(RetrieveRequestSubscriber.class).run();
    INJECTOR.getInstance(StoreRequestSubscriber.class).run();
  }

}
