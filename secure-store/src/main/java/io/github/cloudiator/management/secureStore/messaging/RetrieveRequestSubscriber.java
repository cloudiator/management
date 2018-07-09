package io.github.cloudiator.management.secureStore.messaging;

import com.google.inject.Inject;
import org.cloudiator.messaging.services.SecureStoreService;

public class RetrieveRequestSubscriber implements Runnable {

  private final SecureStoreService secureStoreService;

  @Inject
  public RetrieveRequestSubscriber(
      SecureStoreService secureStoreService) {
    this.secureStoreService = secureStoreService;
  }

  @Override
  public void run() {
    secureStoreService.subscribeRetrieveRequest((id, content) -> {

    });
  }
}
