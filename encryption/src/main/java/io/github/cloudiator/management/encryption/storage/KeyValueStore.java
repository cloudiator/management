package io.github.cloudiator.management.encryption.storage;

import java.util.Optional;

public interface KeyValueStore {

  void store(String key, String value);

  Optional<String> retrieve(String key);

}
