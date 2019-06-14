package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import io.github.cloudiator.persistance.SecureEntryStore;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.SecureStore.SecureStoreDeleteResponse;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.services.SecureStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteRequestSubscriber implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteRequestSubscriber.class);

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;
  private final MessageInterface messageInterface;

  @Inject
  public DeleteRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore, MessageInterface messageInterface) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
    this.messageInterface = messageInterface;
  }

  @SuppressWarnings("WeakerAccess")
  @Transactional
  protected void delete(String key, String userId) {
    final Optional<String> retrieve = secureEntryStore.retrieve(key, userId);
    if (!retrieve.isPresent()) {
      throw new NoSuchElementException(String.format("Entry with key %s does not exist.", key));
    }
    secureEntryStore.delete(key, userId);
  }

  @Override
  public void run() {
    secureStoreService.subscribeDeleteRequest((id, content) -> {

      final String key = content.getKey();
      final String userId = content.getUserId();

      try {
        delete(key, userId);
        messageInterface.reply(id, SecureStoreDeleteResponse.newBuilder().build());
      } catch (NoSuchElementException e) {
        messageInterface.reply(SecureStoreDeleteResponse.class, id,
            Error.newBuilder().setCode(404).setMessage(e.getMessage()).build());
      } catch (Exception e) {
        LOGGER.error(String.format("Unexpected exception while processing request %s: %s", content,
            e.getMessage()));
        messageInterface.reply(SecureStoreDeleteResponse.class, id,
            Error.newBuilder().setCode(500).setMessage("Error during deletion: " + e.getMessage())
                .build());
      }
    });
  }
}
