package io.github.cloudiator.management.user.persistance;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import de.uniulm.omi.cloudiator.persistance.repositories.BaseModelRepositoryJpa;
import javax.persistence.EntityManager;

public class JpaTenantModelRepository extends BaseModelRepositoryJpa<TenantModel> implements
    TenantModelRepository {

  @Inject
  protected JpaTenantModelRepository(
      Provider<EntityManager> entityManager,
      TypeLiteral<TenantModel> type) {
    super(entityManager, type);
  }
}
