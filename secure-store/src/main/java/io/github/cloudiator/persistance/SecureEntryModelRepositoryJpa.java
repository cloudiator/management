package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import java.util.List;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class SecureEntryModelRepositoryJpa extends
    BaseModelRepositoryJpa<SecureEntryModel> implements
    SecureEntryModelRepository {

  @Inject
  protected SecureEntryModelRepositoryJpa(
      Provider<EntityManager> entityManager,
      TypeLiteral<SecureEntryModel> type) {
    super(entityManager, type);
  }

  @Nullable
  @Override
  public SecureEntryModel getEntry(String key, String userId) {

    checkNotNull(userId, "userId is null");
    checkNotNull(key, "key is null");
    String queryString = String.format(
        "select entry from %s entry inner join entry.tenant tenant where tenant.name = :name and entry.key = :key",
        type.getName());
    Query query = em().createQuery(queryString).setParameter("name", userId)
        .setParameter("key", key);
    @SuppressWarnings("unchecked") List<SecureEntryModel> entries = query.getResultList();
    return entries.stream().findFirst().orElse(null);
  }
}
