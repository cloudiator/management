package io.github.cloudiator.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import io.github.cloudiator.management.user.domain.Tenant;
import java.util.ArrayList;
import java.util.List;

public class TenantDomainRepository {

  private final TenantModelRepository tenantModelRepository;

  @Inject
  public TenantDomainRepository(
      TenantModelRepository tenantModelRepository) {
    this.tenantModelRepository = tenantModelRepository;
  }

  public Tenant findTenantbyName(String name) {
    checkState(exists(name), "Tenant does not exist.");
    TenantModel dbBack = tenantModelRepository.findTenantByName(name).get();
    Tenant result = new Tenant(dbBack.getName());
    return result;
  }

  public void addTenant(Tenant newTenant) {
    checkNotNull(newTenant, "Tenant is null");
    checkState(!exists(newTenant.getName()), "Tenant already exists.");
    TenantModel tenantModel = new TenantModel(newTenant.getName());
    tenantModelRepository.save(tenantModel);
  }

  public boolean exists(String name) {
    checkNotNull(name, "Tenant is null");
    return tenantModelRepository.findTenantByName(name).isPresent();
  }

  public List<Tenant> findAllTenants() {
    List<Tenant> allTenants = new ArrayList<Tenant>();
    for (TenantModel tenantModel : tenantModelRepository.findAll()) {
      allTenants.add(new Tenant(tenantModel.getName()));
    }
    return allTenants;
  }


}
