package io.github.cloudiator.management.user.domain;

import java.util.Optional;

public interface UserStore {

  Optional<User> getUser(String email);

  Optional<Tenant> getTenant(String name);

  void storeUser(User user);

  void storeTenant(Tenant tenant);

  Iterable<User> retrieveUsers();

  Iterable<Tenant> retrieveTenants();
}
