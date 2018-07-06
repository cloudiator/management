package io.github.cloudiator.management.encryption.messaging;

import com.google.inject.Inject;
import io.github.cloudiator.management.encryption.EncryptionFactory;
import org.cloudiator.messages.General.Error;
import org.cloudiator.messages.entities.Encryption.EncryptionResponse;
import org.cloudiator.messaging.MessageInterface;
import org.cloudiator.messaging.services.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptionRequestSubscriber implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionRequestSubscriber.class);
  private final EncryptionFactory encryptionFactory;
  private final org.cloudiator.messaging.services.EncryptionService encryptionMessageService;
  private final MessageInterface messageInterface;

  @Inject
  public EncryptionRequestSubscriber(
      EncryptionFactory encryptionFactory,
      EncryptionService encryptionMessageService,
      MessageInterface messageInterface) {
    this.encryptionFactory = encryptionFactory;
    this.encryptionMessageService = encryptionMessageService;
    this.messageInterface = messageInterface;
  }

  @Override
  public void run() {
    encryptionMessageService.subscribeEncryption((id, content) -> {

      try {

        String userId = content.getUserId();

        String ciphertext = encryptionFactory.forUser(userId).encrypt(content.getPlaintext());

        EncryptionResponse encryptionResponse = EncryptionResponse.newBuilder()
            .setCiphertext(ciphertext)
            .build();

        messageInterface.reply(id, encryptionResponse);
      } catch (Exception e) {
        LOGGER.error("Error while doing encryption.", e);
        messageInterface.reply(EncryptionResponse.class, id, Error.newBuilder().setCode(500)
            .setMessage("Error during encryption :" + e.getMessage()).build());
      }
    });
  }
}
