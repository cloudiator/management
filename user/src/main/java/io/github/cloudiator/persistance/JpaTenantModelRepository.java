package io.github.cloudiator.persistance;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import io.github.cloudiator.util.JpaResultHelper;
import java.util.Optional;
import javax.persistence.EntityManager;

public class JpaTenantModelRepository extends BaseModelRepositoryJpa<TenantModel> implements
    TenantModelRepository {

  @Inject
  protected JpaTenantModelRepository(
      Provider<EntityManager> entityManager,
      TypeLiteral<TenantModel> type) {
    super(entityManager, type);
  }

  @Override
  public Optional<TenantModel> findTenantByName(String name) {

    String query = String.format("from %s where name=:name", type.getName());
    final TenantModel trenantModel = (TenantModel) JpaResultHelper
        .getSingleResultOrNull(em().createQuery(query).setParameter("name", name));
    return Optional.ofNullable(trenantModel);
  }
}
