package io.github.cloudiator.management.encryption.storage;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileBasedKeyValueStore implements KeyValueStore {

  private final String directory;

  FileBasedKeyValueStore(String directory) {
    this.directory = directory;
  }

  private File directory() {
    final File directory = new File(this.directory);
    checkState(directory.isDirectory(),
        String.format("%s is not a directory", directory.getAbsolutePath()));
    checkState(directory.canWrite(),
        String.format("Can not write to directory %s", this.directory));
    return directory;
  }

  private File getFile(String key) {
    return new File(directory().getAbsolutePath() + File.separator + key);
  }

  private File createFileForKey(String key) {

    File file = getFile(key);

    try {
      final boolean success = file.createNewFile();

      if (!success) {
        throw new IllegalStateException(String.format("File %s already exists", file));
      }

      return file;

    } catch (IOException e) {
      throw new IllegalStateException(String.format("Error while writing file %s.", file), e);
    }
  }

  @Override
  public void store(String key, String value) {
    try {
      Files.write(value, createFileForKey(key), Charsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Error while writing key file.", e);
    }
  }

  @Override
  public Optional<String> retrieve(String key) {

    final File file = getFile(key);
    if (!file.exists()) {
      return Optional.empty();
    }

    try {
      final String content = Files.readFirstLine(file, Charsets.UTF_8);
      checkNotNull(content, "Expected file to contain value, but was empty");
      return Optional.of(content);
    } catch (IOException e) {
      throw new IllegalStateException("Error while reading value from file", e);
    }
  }
}
