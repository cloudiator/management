package io.github.cloudiator.management.encryption;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.cloudiator.management.encryption.config.EncryptionContext;
import io.github.cloudiator.management.encryption.config.EncryptionModule;
import io.github.cloudiator.management.encryption.messaging.DecryptionRequestSubscriber;
import io.github.cloudiator.management.encryption.messaging.EncryptionRequestSubscriber;
import org.cloudiator.messaging.kafka.KafkaContext;
import org.cloudiator.messaging.kafka.KafkaMessagingModule;
import org.cloudiator.messaging.services.MessageServiceModule;

public class EncryptionAgent {

  private static final Injector injector = Guice
      .createInjector(new KafkaMessagingModule(new KafkaContext()), new MessageServiceModule(),
          new EncryptionModule(new EncryptionContext()));

  public static void main(String[] args) {

    injector.getInstance(EncryptionRequestSubscriber.class).run();
    injector.getInstance(DecryptionRequestSubscriber.class).run();

  }

}
