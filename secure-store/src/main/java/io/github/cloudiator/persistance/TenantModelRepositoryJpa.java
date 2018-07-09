package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class TenantModelRepositoryJpa extends BaseModelRepositoryJpa<TenantModel> implements
    TenantModelRepository {

  @Inject
  protected TenantModelRepositoryJpa(
      Provider<EntityManager> entityManager,
      TypeLiteral<TenantModel> type) {
    super(entityManager, type);
  }

  @Nullable
  @Override
  public TenantModel findByName(String name) {
    checkNotNull(name, "userId is null");
    String queryString = String
        .format("from %s where name=:name", type.getName());
    Query query = em().createQuery(queryString).setParameter("name", name);
    try {
      //noinspection unchecked
      return (TenantModel) query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public TenantModel createOrGet(String userId) {
    checkNotNull(userId, "userId is null");
    final TenantModel byUserId = findByName(userId);
    if (byUserId != null) {
      return byUserId;
    }
    TenantModel tenantModel = new TenantModel(userId);
    save(tenantModel);
    return tenantModel;
  }
}
