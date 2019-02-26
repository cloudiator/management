package io.github.cloudiator.management.user;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;
import io.github.cloudiator.management.user.messaging.KafkaListener;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Init implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Init.class);
  private final PersistService persistService;
  private final Set<KafkaListener> kafkaListenerSet;

  @Inject
  public Init(PersistService persistService,
      Set<KafkaListener> kafkaListenerSet) {
    this.persistService = persistService;
    this.kafkaListenerSet = kafkaListenerSet;
    run();
  }


  @Override
  public void run() {
    LOGGER.info("Starting persist service");
    this.persistService.start();

    LOGGER.info("Starting kafka listeners");
    for (KafkaListener kafkaListener : kafkaListenerSet) {
      LOGGER.debug(String.format("Starting kafka listener %s.", kafkaListener));
      kafkaListener.run();
    }

  }


}
