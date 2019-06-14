package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import io.github.cloudiator.persistance.SecureEntryStore;
import java.util.Optional;
import org.cloudiator.messaging.services.SecureStoreService;

public class RetrieveRequestSubscriber implements Runnable {

  private final SecureStoreService secureStoreService;
  private final SecureEntryStore secureEntryStore;

  @Inject
  public RetrieveRequestSubscriber(
      SecureStoreService secureStoreService,
      SecureEntryStore secureEntryStore) {
    this.secureStoreService = secureStoreService;
    this.secureEntryStore = secureEntryStore;
  }

  @SuppressWarnings("WeakerAccess")
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
        //todo reply success and value
      } catch (Exception e) {
        //todo reply error
      }

    });
  }
}
