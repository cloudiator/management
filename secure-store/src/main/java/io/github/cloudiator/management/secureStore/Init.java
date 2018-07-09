package io.github.cloudiator.management.secureStore;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class Init implements Runnable {

  private final PersistService persistService;

  @Inject
  public Init(PersistService persistService) {
    this.persistService = persistService;
    run();
  }


  @Override
  public void run() {
    this.persistService.start();
  }
}
