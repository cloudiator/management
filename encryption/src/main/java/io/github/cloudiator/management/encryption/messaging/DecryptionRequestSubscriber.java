package io.github.cloudiator.management.encryption.messaging;

import com.google.inject.Inject;
import io.github.cloudiator.management.encryption.EncryptionFactory;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.Encryption.DecryptionResponse;
import org.cloudiator.messages.entities.Encryption.EncryptionResponse;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.services.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecryptionRequestSubscriber implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(DecryptionRequestSubscriber.class);
  private final EncryptionFactory encryptionFactory;
  private final EncryptionService encryptionMessageService;
  private final MessageInterface messageInterface;

  @Inject
  public DecryptionRequestSubscriber(
      EncryptionFactory encryptionFactory,
      EncryptionService encryptionMessageService,
      MessageInterface messageInterface) {
    this.encryptionFactory = encryptionFactory;
    this.encryptionMessageService = encryptionMessageService;
    this.messageInterface = messageInterface;
  }

  @Override
  public void run() {
    encryptionMessageService.subscribeDecryption((id, content) -> {

      try {

        String userId = content.getUserId();

        String plaintext = encryptionFactory.forUser(userId).decrypt(content.getCiphertext());

        DecryptionResponse decryptionResponse = DecryptionResponse.newBuilder()
            .setPlaintext(plaintext)
            .build();

        messageInterface.reply(id, decryptionResponse);
      } catch (Exception e) {
        LOGGER.error("Error while doing encryption.", e);
        messageInterface.reply(EncryptionResponse.class, id, Error.newBuilder().setCode(500)
            .setMessage("Error during encryption :" + e.getMessage()).build());
      }
    });
  }
}
