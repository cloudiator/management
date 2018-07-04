package io.github.cloudiator.management.encryption.storage;

public class KeyValueStoreFactory {

  KeyValueStoreFactory() {

  }

  public KeyValueStore fileBased(String directory) {
    return new FileBasedKeyValueStore(directory);
  }

}
