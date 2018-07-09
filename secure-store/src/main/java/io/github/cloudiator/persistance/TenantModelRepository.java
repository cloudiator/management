package io.github.cloudiator.persistance;

import javax.annotation.Nullable;

interface TenantModelRepository extends ModelRepository<TenantModel> {

  @Nullable
  TenantModel findByName(String name);

  TenantModel createOrGet(String userId);
}
