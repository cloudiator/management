package io.github.cloudiator.management.user.persistance;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.inject.Inject;
import io.github.cloudiator.management.user.domain.Tenant;
import io.github.cloudiator.management.user.domain.User;

public class TenantDomainRepository {

  private final TenantModelRepository tenantModelRepository;

  @Inject
  public TenantDomainRepository(
      TenantModelRepository tenantModelRepository) {
    this.tenantModelRepository = tenantModelRepository;
  }

  public Tenant findTenantbyName(String name) {
    checkState(!exists(name), "Tenant does not exist.");
    TenantModel dbBack = tenantModelRepository.findTenantByName(name).get();
    Tenant result = new Tenant(dbBack.getName());
    return result;
  }


  public void addTenant(String newTenant) {
    checkNotNull(newTenant, "TenantName is null");
    checkState(!exists(newTenant), "Tenant already exists.");
    TenantModel tenantModel = new TenantModel(newTenant);
    tenantModelRepository.save(tenantModel);
  }

  public boolean exists(String name) {
    checkNotNull(name, "mail is null");
    return tenantModelRepository.findTenantByName(name).isPresent();
  }


}
