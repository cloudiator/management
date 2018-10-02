package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import io.github.cloudiator.persistance.SecureEntryStore;
import java.util.Optional;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.SecureStore.SecureStoreRetrieveResponse;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.services.SecureStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetrieveRequestSubscriber implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveRequestSubscriber.class);

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;
  private final MessageInterface messageInterface;

  @Inject
  public RetrieveRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore, MessageInterface messageInterface) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
    this.messageInterface = messageInterface;
  }

  @Transactional
  protected Optional<String> retrieve(String key, String userId) {
    return secureEntryStore.retrieve(key, userId);
  }

  @Override
  public void run() {
    secureStoreService.subscribeRetrieveRequest((id, content) -> {

      final String key = content.getKey();
      final String userId = content.getUserId();

      try {
        final Optional<String> value = retrieve(key, userId);

        if (value.isPresent()) {
          messageInterface
              .reply(id, SecureStoreRetrieveResponse.newBuilder().setValue(value.get()).build());
        } else {
          messageInterface.reply(SecureStoreRetrieveResponse.class, id,
              Error.newBuilder().setCode(404)
                  .setMessage(String.format("Entry with key %s does not exist.", key)).build());
        }
      } catch (Exception e) {
        LOGGER.error(String.format("Unexpected exception while processing request %s: %s", content,
            e.getMessage()));
        messageInterface.reply(SecureStoreRetrieveResponse.class, id,
            Error.newBuilder().setCode(500)
                .setMessage(String.format("Error while retrieving value for key %s.", key))
                .build());
      }
    });
  }
}
