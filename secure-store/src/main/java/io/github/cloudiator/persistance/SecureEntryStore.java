package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import java.util.Optional;
import org.cloudiator.messages.entities.Encryption.DecryptionRequest;
import org.cloudiator.messages.entities.Encryption.EncryptionRequest;
import org.cloudiator.messages.entities.Encryption.EncryptionResponse;
import org.cloudiator.messaging.ResponseException;
import org.cloudiator.messaging.services.EncryptionService;

public class SecureEntryStore {

  private final SecureEntryModelRepository secureEntryModelRepository;
  private final EncryptionService encryptionService;
  private final TenantModelRepository tenantModelRepository;

  @Inject
  public SecureEntryStore(
      SecureEntryModelRepository secureEntryModelRepository,
      EncryptionService encryptionService,
      TenantModelRepository tenantModelRepository) {
    this.secureEntryModelRepository = secureEntryModelRepository;
    this.encryptionService = encryptionService;
    this.tenantModelRepository = tenantModelRepository;
  }

  public String store(String key, String value, String userId) {

    checkNotNull(key, "key is null");
    checkNotNull(value, "value is null");
    checkNotNull(userId, "userId is null");

    SecureEntryModel existing = secureEntryModelRepository.getEntry(key, userId);

    TenantModel tenantModel = tenantModelRepository.createOrGet(userId);

    //encrypt value
    try {
      final EncryptionResponse encryptionResponse = encryptionService
          .encrypt(EncryptionRequest.newBuilder().setUserId(userId).setPlaintext(value).build());

      if (existing == null) {
        existing = new SecureEntryModel(tenantModel, key,
            encryptionResponse.getCiphertext());
      } else {
        existing.setEncryptedValue(encryptionResponse.getCiphertext());
      }

      secureEntryModelRepository.save(existing);

      return encryptionResponse.getCiphertext();

    } catch (ResponseException e) {
      throw new IllegalStateException("Could not encrypt value.", e);
    }
  }

  public void delete(String key, String userId) {

    checkNotNull(key, "key is null");
    checkNotNull(userId, "userId is null");

    SecureEntryModel entry = secureEntryModelRepository.getEntry(key, userId);
    checkNotNull(entry, String.format("Entry with key %s does not exist.", key));
    secureEntryModelRepository.delete(entry);
  }


  public Optional<String> retrieve(String key, String userId) {

    checkNotNull(key, "key is null");
    checkNotNull(userId, "userId is null");

    final SecureEntryModel entry = secureEntryModelRepository.getEntry(key, userId);
    if (entry == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(encryptionService.decrypt(
          DecryptionRequest.newBuilder().setCiphertext(entry.getEncryptedValue()).setUserId(userId)
              .build()).getPlaintext());
    } catch (ResponseException e) {
      throw new IllegalStateException("Could not decrypt value", e);
    }
  }

}
