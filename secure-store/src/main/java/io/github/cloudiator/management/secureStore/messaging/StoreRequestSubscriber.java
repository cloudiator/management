package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import io.github.cloudiator.persistance.SecureEntryStore;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.SecureStore.SecureStoreResponse;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.services.SecureStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreRequestSubscriber implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoreRequestSubscriber.class);

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;
  private final MessageInterface messageInterface;

  @Inject
  public StoreRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore, MessageInterface messageInterface) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
    this.messageInterface = messageInterface;
  }

  @SuppressWarnings("WeakerAccess")
  @Transactional
  protected String store(String key, String value, String userId) {
    return secureEntryStore.store(key, value, userId);
  }

  @Override
  public void run() {
    secureStoreService.subscribeStoreRequest((id, content) -> {

      final String key = content.getKey();
      final String value = content.getValue();
      final String userId = content.getUserId();

      try {
        final String store = store(key, value, userId);
        messageInterface
            .reply(id, SecureStoreResponse.newBuilder().setEncryptedValue(store).build());
      } catch (Exception e) {
        LOGGER.error(String.format("Error while processing request %s.", content), e);
        messageInterface.reply(SecureStoreResponse.class, id,
            Error.newBuilder().setCode(500).setMessage("Error during storage: " + e.getMessage())
                .build());
      }
    });
  }
}
