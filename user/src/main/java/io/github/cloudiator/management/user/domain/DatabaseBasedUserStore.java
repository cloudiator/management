package io.github.cloudiator.management.user.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;
import io.github.cloudiator.persistance.TenantDomainRepository;
import io.github.cloudiator.persistance.UserDomainRepository;
import java.util.Optional;

public class DatabaseBasedUserStore implements UserStore {

  private final UserDomainRepository userDomainRepository;
  private final TenantDomainRepository tenantDomainRepository;

  @Inject
  public DatabaseBasedUserStore(
      UserDomainRepository userDomainRepository,
      TenantDomainRepository tenantDomainRepository) {
    this.userDomainRepository = userDomainRepository;
    this.tenantDomainRepository = tenantDomainRepository;
  }

  @Override
  public Optional<User> getUser(String email) {
    checkNotNull(email, "email is null");
    return Optional.ofNullable(userDomainRepository.findUserByMail(email));
  }

  @Override
  public Optional<Tenant> getTenant(String name) {
    return Optional.ofNullable(tenantDomainRepository.findTenantbyName(name));
  }

  @Override
  public Iterable<User> retrieveUsers() {
    //todo implement
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public Iterable<Tenant> retrieveTenants() {
    return tenantDomainRepository.findAllTenants();
  }
}
