package io.github.cloudiator.management.user.persistance;

import de.uniulm.omi.cloudiator.persistance.repositories.ModelRepository;
import java.util.Optional;

public interface TenantModelRepository extends ModelRepository<TenantModel> {

  Optional<TenantModel> findTenantByName(String name);

}
