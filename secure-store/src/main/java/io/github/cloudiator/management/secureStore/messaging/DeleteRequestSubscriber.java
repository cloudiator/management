package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import io.github.cloudiator.persistance.SecureEntryStore;
import javax.transaction.Transactional;
import org.cloudiator.messaging.services.SecureStoreService;

public class DeleteRequestSubscriber implements Runnable {

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;

  @Inject
  public DeleteRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
  }

  @Transactional
  protected void delete(String key, String userId) {
    secureEntryStore.delete(key, userId);
  }

  @Override
  public void run() {
    secureStoreService.subscribeDeleteRequest((id, content) -> {

      final String key = content.getKey();
      final String userId = content.getUserId();

      try {
        delete(key, userId);
        //todo reply success and value
      } catch (Exception e) {
        //todo reply error
      }

    });
  }
}
