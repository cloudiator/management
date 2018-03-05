package io.github.cloudiator.persistance;

import java.util.Optional;


public interface TenantModelRepository extends ModelRepository<TenantModel> {

  Optional<TenantModel> findTenantByName(String name);

}
