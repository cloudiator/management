package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import io.github.cloudiator.persistance.SecureEntryStore;
import com.google.inject.persist.Transactional;
import org.cloudiator.messaging.services.SecureStoreService;

public class StoreRequestSubscriber implements Runnable {

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;

  @Inject
  public StoreRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
  }

  @Transactional
  protected void store(String key, String value, String userId) {
    secureEntryStore.store(key, value, userId);
  }

  @Override
  public void run() {
    secureStoreService.subscribeStoreRequest((id, content) -> {

      final String key = content.getKey();
      final String value = content.getValue();
      final String userId = content.getUserId();

      try {
        store(key, value, userId);
        //todo reply success message
      } catch (Exception e) {
        //todo reply error message
      }
    });
  }
}
