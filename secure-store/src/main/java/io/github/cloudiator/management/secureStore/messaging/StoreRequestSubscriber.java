package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import org.cloudiator.messaging.services.SecureStoreService;

public class StoreRequestSubscriber implements Runnable {

  private final SecureStoreService secureStoreService;

  @Inject
  public StoreRequestSubscriber(
      SecureStoreService secureStoreService) {
    this.secureStoreService = secureStoreService;
  }

  @Override
  public void run() {
    secureStoreService.subscribeStoreRequest((id, content) -> {

    });
  }
}
