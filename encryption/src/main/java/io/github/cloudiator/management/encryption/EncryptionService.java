package io.github.cloudiator.management.encryption;

public interface EncryptionService {

  String encrypt(String plaintext);

  String decrypt(String cipher);

}
